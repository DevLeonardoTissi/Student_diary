package com.example.studentdiary.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ActivityMainBinding
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val appViewModel: AppViewModel by viewModel()
    private val controller by lazy {
        findNavController(R.id.nav_host_fragment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureMenuDrawer()
        navigationComponentsVisibility()
    }

    private fun configureMenuDrawer() {
        //CONFIGURANDO NAV VIEW
        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView
            .setupWithNavController(navController)

        //        CONFIGURANDO A NAVEGAÇÃO NA TOOLBAR
        //Deixar por conta no navGraph adicionar o topLevel de cada fragment
        //        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.disciplinesFragment,
                R.id.dictionaryFragment,
                R.id.pomodoroFragment,
                R.id.publicTenderFragment,
            ), drawerLayout = binding.drawerLayout
        )

        binding.activityMainToolbar.setupWithNavController(navController, appBarConfiguration)


    }


    private fun showNavigationComponents(hasNavigationComponents: NavigationComponents) {
        if (hasNavigationComponents.menuDrawer) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        if (!hasNavigationComponents.navigationIcon){
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

//    private fun hideNavigationIcon() {
//        controller.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.loginFragment -> {
//                    binding.activityMainToolbar.navigationIcon = null
//                }
//            }
//        }
//    }

}
