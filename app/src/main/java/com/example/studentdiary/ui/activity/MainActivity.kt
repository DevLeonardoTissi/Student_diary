package com.example.studentdiary.ui.activity

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.studentdiary.NavGraphDirections
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ActivityMainBinding
import com.example.studentdiary.databinding.HeaderNavigationDrawerBinding
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.extensions.toast
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.SEND_TOKEN_PREFERENCES_KEY
import com.example.studentdiary.ui.UPLOAD_TOKEN_WORKER_TAG
import com.example.studentdiary.ui.dialog.AppInfoBottomSheetDialog
import com.example.studentdiary.ui.dialog.CustomImageUserBottomSheetDialog
import com.example.studentdiary.broadcastReceiver.AirplaneModeBroadcastReceiver
import com.example.studentdiary.broadcastReceiver.BatteryStatusBroadcastReceiver
import com.example.studentdiary.datastore.dataStore
import com.example.studentdiary.services.PomodoroService
import com.example.studentdiary.utils.exitGoogleAndFacebookAccount
import com.example.studentdiary.workManager.TokenUploadWorker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val appViewModel: AppViewModel by viewModel()
    private val airplaneModeBroadcastReceiver: BroadcastReceiver = AirplaneModeBroadcastReceiver()
    private val batteryStatusBroadcastReceiver:BroadcastReceiver = BatteryStatusBroadcastReceiver()
    private val controller by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    private val sensorManager: SensorManager by inject()
    private lateinit var temperatureListener: SensorEventListener

    private val requestPermissionNotificationsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            this.toast(getString(R.string.main_activity_toast_message_notifications_permission_granted))
        } else {
            this.toast(getString(R.string.main_activity_toast_message_notifications_permission_not_granted))
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { file ->
            appViewModel.updateUserPhoto(file, onError = {
                this.toast(getString(R.string.main_activity_toast_message_error_update_user_photo))
            }, onSuccessful = {
                this.toast(getString(R.string.main_activity_toast_message_successful_update_user_photo))
            })
        }
    }

    private val requestPermissionGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupMenuDrawerAndToolbarNavigation()
        navigationComponentsVisibility()
        searchUserAndCustomizeHeader()
        registerReceiverAirplaneMode()
        askNotificationPermission()
        registerReceiverBatteryStatus()

        lifecycleScope.launch {
            this@MainActivity.dataStore.data.collect { preferences ->
                preferences[stringPreferencesKey(SEND_TOKEN_PREFERENCES_KEY)]?.let {

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val uploadWorkRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<TokenUploadWorker>()
                            .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                WorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                            ).addTag(UPLOAD_TOKEN_WORKER_TAG)
                            .setConstraints(constraints)
                            .build()
                    WorkManager.getInstance(this@MainActivity).enqueue(uploadWorkRequest)


                    val workManager = WorkManager.getInstance(this@MainActivity)
                    workManager.getWorkInfosByTagLiveData(UPLOAD_TOKEN_WORKER_TAG)
                        .observe(this@MainActivity) { workInfoList ->

                            val firstWorkInfo = workInfoList.firstOrNull()
                            if (firstWorkInfo?.state == WorkInfo.State.SUCCEEDED) {
                                Log.i("TAG", "onCreate: sucesso ao enviar")
                                Snackbar.make(
                                    binding.root,
                                    "sucesso ao enviar token", Snackbar.LENGTH_SHORT
                                )
                                    .show()
                                lifecycleScope.launch {
                                    this@MainActivity.dataStore.edit { preferences ->
                                        preferences.remove(
                                            stringPreferencesKey(
                                                SEND_TOKEN_PREFERENCES_KEY
                                            )
                                        )
                                    }
                                }

                            } else {
                                Log.i("TAG", "onCreate: erro ao enviar")
                            }

                        }

                }
            }
        }
    }


    private fun registerReceiverBatteryStatus() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_LOW)
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        registerReceiver(batteryStatusBroadcastReceiver, filter, receiverFlags)
    }

    private fun setupTemperatureSensorAndUpdateTextView(textView: TextView) {
        val temperatureSensor: Sensor? =
            sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        temperatureListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent) {
                val temperature = String.format("%.1f\u00B0", event.values[0])
                textView.text = temperature
            }
        }

        sensorManager.registerListener(
            temperatureListener,
            temperatureSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun hasTemperatureSensor(): Boolean {
        val temperatureSensor: Sensor? =
            sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        return (temperatureSensor != null)
    }

    private fun setupMenuDrawerAndToolbarNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        val navView = binding.navView
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuItem_drawer_exit -> {
                    alertDialog(
                        getString(R.string.main_activity_alert_dialog_exit_title),
                        getString(R.string.main_activity_alert_dialog_exit_message),
                        icon = R.drawable.ic_exit,
                        onClickingOnPositiveButton = {
                            logout()
                            goToLogin()
                            if (PomodoroService.timerIsRunning.value == true){
                                val intent = Intent(this, PomodoroService::class.java)
                                stopService(intent)
                            }
                        })
                    false
                }

                R.id.menuItem_drawer_about -> {
                    AppInfoBottomSheetDialog(this).show()
                    false
                }

                else -> NavigationUI.onNavDestinationSelected(it, navController)
            }
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }


        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.disciplinesFragment,
                R.id.dictionaryFragment,
                R.id.pomodoroFragment,
                R.id.publicTenderFragment,
                R.id.profileFragment
            ), drawerLayout = binding.drawerLayout
        )
        setSupportActionBar(binding.activityMainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.activityMainToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun navigationComponentsVisibility() {
        appViewModel.navigationComponents.observe(this) {
            it?.let { hasNavigationComponents ->
                showNavigationComponents(hasNavigationComponents)
            }
        }
    }

    private fun showNavigationComponents(hasNavigationComponents: NavigationComponents) {
        binding.drawerLayout
            .setDrawerLockMode(if (hasNavigationComponents.menuDrawer) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.activityMainToolbar.visibility =
            if (hasNavigationComponents.toolbar) View.VISIBLE else View.INVISIBLE

    }

    private fun searchUserAndCustomizeHeader() {
        val headerBinding = HeaderNavigationDrawerBinding.inflate(layoutInflater)
        appViewModel.firebaseUser.observe(this) { user ->
            user?.let { userNonNull ->
                val namePresentation = if (userNonNull.displayName.isNullOrBlank()) {
                    userNonNull.email
                } else {
                    userNonNull.displayName
                }
                headerBinding.headerTextViewSubtitle.text = getString(
                    R.string.header_drawer_concatenated_text,
                    getString(R.string.header_drawer_greeting),
                    namePresentation
                )
                userNonNull.photoUrl?.let {
                    headerBinding.headerShapeableImageView.tryLoadImage(it.toString())
                } ?: headerBinding.headerShapeableImageView.tryLoadImage()

            }
        }

        headerBinding.headerIconButton.setOnClickListener {
            CustomImageUserBottomSheetDialog(this).show(onClickGalleryButton = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    openGallery()
                } else {
                    if (hasPermissionReadExternalStorage()) {
                        openGallery()
                    } else {
                        requestPermissionGalleryLauncher.launch(READ_EXTERNAL_STORAGE)
                    }
                }
            }, onClickRemoveButton = {
                appViewModel.removeUserPhoto(onError = {
                    toast(getString(R.string.main_activity_toast_message_error_remove_user_photo))
                }, onSuccessful = {
                    toast(getString(R.string.main_activity_toast_message_successful_remove_user_photo))
                })
            })
        }
        if (hasTemperatureSensor()) {
            headerBinding.headerImageViewThermostat.visibility = View.VISIBLE
            headerBinding.headerTextViewTemperature.visibility = View.VISIBLE
            setupTemperatureSensorAndUpdateTextView(headerBinding.headerTextViewTemperature)
        } else {
            headerBinding.headerImageViewThermostat.visibility = View.GONE
            headerBinding.headerTextViewTemperature.visibility = View.GONE
        }

        binding.navView.addHeaderView(headerBinding.root)
    }

    private fun registerReceiverAirplaneMode() {
        val filter = IntentFilter(ACTION_AIRPLANE_MODE_CHANGED)
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        registerReceiver(airplaneModeBroadcastReceiver, filter, receiverFlags)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionNotificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun hasPermissionReadExternalStorage(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }

    private fun goToLogin() {
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }

    private fun logout() {
        appViewModel.logout()
        exitGoogleAndFacebookAccount(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasTemperatureSensor()) {
            sensorManager.unregisterListener(temperatureListener)
        }
        unregisterReceiver(airplaneModeBroadcastReceiver)
        unregisterReceiver(batteryStatusBroadcastReceiver)
    }
}
