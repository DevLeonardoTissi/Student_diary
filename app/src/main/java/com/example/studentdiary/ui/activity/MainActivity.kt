package com.example.studentdiary.ui.activity

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.BroadcastReceiver
import android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.studentdiary.NavGraphDirections
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ActivityMainBinding
import com.example.studentdiary.databinding.HeaderNavigationDrawerBinding
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.extensions.toast
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.dialog.AppInfoBottonSheetDialog
import com.example.studentdiary.ui.dialog.CustomImageBottonSheetDialog
import com.example.studentdiary.utils.broadcastReceiver.MyBroadcastReceiver
import com.example.studentdiary.utils.exitGoogleAndFacebookAccount
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val appViewModel: AppViewModel by viewModel()
    private val receiver: BroadcastReceiver = MyBroadcastReceiver()
    private val controller by lazy {
        findNavController(R.id.nav_host_fragment)
    }

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
        registerReceiver()
        searchUserAndCustomizeHeader()
        askNotificationPermission()
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

    private fun searchUserAndCustomizeHeader() {
        val headerBinding = HeaderNavigationDrawerBinding.inflate(layoutInflater)
        appViewModel.firebaseUser.observe(this) { user ->
            user?.let { userNonNull ->
                val namePresentation = if (userNonNull.displayName.isNullOrBlank()) {
                    userNonNull.email
                } else {
                    userNonNull.displayName
                }
                headerBinding.headerSubtitle.text = getString(
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
            CustomImageBottonSheetDialog(this).show(onClickGalleryButton = {
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
                        toast(getString(R.string.main_activity_toast_message_successful_remove_user_photo))
                }, onSuccessful = {
                    toast(getString(R.string.main_activity_toast_message_error_remove_user_photo))
                })
            })
        }
        binding.navView.addHeaderView(headerBinding.root)
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


    private fun registerReceiver() {
        val filter = IntentFilter(ACTION_AIRPLANE_MODE_CHANGED)
        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }
        registerReceiver(receiver, filter, receiverFlags)
    }

    private fun setupMenuDrawerAndToolbarNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView
            .setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuItem_drawer_exit -> {

                    alertDialog(
                        getString(R.string.main_activity_alert_dialog_exit_title),
                        getString(R.string.main_activity_alert_dialog_exit_message),
                        icon = R.drawable.ic_exit,
                        onClickingOnPositiveButton = {
                            logout()
                            goToLogin()
                        })
                    false
                }

                R.id.menuItem_drawer_about -> {
                    AppInfoBottonSheetDialog(this).show()
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
            ), drawerLayout = binding.drawerLayout
        )
        setSupportActionBar(binding.activityMainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.activityMainToolbar.setupWithNavController(navController, appBarConfiguration)
    }


    private fun goToLogin() {
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }

    private fun showNavigationComponents(hasNavigationComponents: NavigationComponents) {
        binding.drawerLayout.setDrawerLockMode(if (hasNavigationComponents.menuDrawer) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.activityMainToolbar.visibility =
            if (hasNavigationComponents.toolbar) View.VISIBLE else View.INVISIBLE

    }

    private fun navigationComponentsVisibility() {
        appViewModel.navigationComponents.observe(this) {
            it?.let { hasNavigationComponents ->
                showNavigationComponents(hasNavigationComponents)
            }
        }
    }

    private fun logout() {
        appViewModel.logout()
        exitGoogleAndFacebookAccount(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
