package com.example.studentdiary.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.studentdiary.databinding.AppInfoBottomSheetDialogBinding
import com.example.studentdiary.databinding.HeaderNavigationDrawerBinding
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.extensions.toast
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.GITHUB_LINK
import com.example.studentdiary.ui.LINKEDIN_LINK
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.STUDENT_DIARY_GITHUB_LINK
import com.example.studentdiary.utils.broadcastReceiver.MyBroadcastReceiver
import com.example.studentdiary.utils.exitGoogleAndFacebookAccount
import com.example.studentdiary.utils.goToUri
import com.google.android.material.bottomsheet.BottomSheetDialog
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            this.toast(getString(R.string.main_activity_permission_granted))
        } else {
            this.toast(getString(R.string.main_activity_permission_not_granted))
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
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun searchUserAndCustomizeHeader() {
        val headerBinding = HeaderNavigationDrawerBinding.inflate(layoutInflater)
        appViewModel.userEmail.observe(this) { email ->
            email?.let {
                headerBinding.headerSubtitle.text = getString(
                    R.string.header_drawer_concatenated_text,
                    getString(R.string.header_drawer_greeting),
                    email
                )
            } ?: kotlin.run {
                headerBinding.headerSubtitle.text = null
            }
        }
        binding.navView.addHeaderView(headerBinding.root)
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
                        onClickingOnPositiveButton = {
                            logout()
                            goToLogin()
                        })
                    false
                }

                R.id.menuItem_drawer_about -> {
                    setupAndOpenAppInfoBottomSheetDialog()
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

    private fun setupAndOpenAppInfoBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.bottonSheetDialog)
        AppInfoBottomSheetDialogBinding.inflate(LayoutInflater.from(this)).apply {
            appInfoBottomSheetDialogChipGitHub.setOnClickListener {
                goToUri(GITHUB_LINK, this@MainActivity)
            }

            appInfoBottomSheetDialogChipGitHubProject.setOnClickListener {
                goToUri(STUDENT_DIARY_GITHUB_LINK, this@MainActivity)
            }

            appInfoBottomSheetDialogChipLinkedin.setOnClickListener {
                goToUri(LINKEDIN_LINK, this@MainActivity)
            }

            bottomSheetDialog.setContentView(root)
            bottomSheetDialog.show()
        }
    }

    private fun goToLogin() {
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }

    private fun showNavigationComponents(hasNavigationComponents: NavigationComponents) {
        if (hasNavigationComponents.menuDrawer) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        if (hasNavigationComponents.toolbar) {
            binding.activityMainToolbar.visibility = View.VISIBLE
        }else{
            binding.activityMainToolbar.visibility = View.INVISIBLE

        }
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
