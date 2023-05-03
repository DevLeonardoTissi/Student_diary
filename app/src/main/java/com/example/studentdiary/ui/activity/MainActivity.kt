package com.example.studentdiary.ui.activity

import android.content.BroadcastReceiver
import android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED
import android.content.IntentFilter
import android.os.Bundle
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
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.extensions.googleSignInClient
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import com.example.studentdiary.utils.broadcastReceiver.MyBroadcastReceiver
import com.facebook.AccessToken
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
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupMenuDrawer()
        navigationComponentsVisibility()
        registerReceiver()

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

    private fun setupMenuDrawer() {
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
                else -> {
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
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
        if (hasNavigationComponents.menuDrawer) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        if (!hasNavigationComponents.navigationIcon) {
            binding.activityMainToolbar.navigationIcon = null
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
        if (loginViewModel.isAuthenticated()) {
            loginViewModel.logout()
            exitGoogleAndFacebookAccount()
        }
    }

    private fun exitGoogleAndFacebookAccount() {
        googleSignInClient().signOut()
        checkAndLogoutIfLoggedInFacebook()
    }

    private fun checkAndLogoutIfLoggedInFacebook() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            AccessToken.setCurrentAccessToken(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
