package com.example.studentdiary.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureMenuDrawer()

//        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

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
                R.id.publicTenderFragment
            ), drawerLayout = binding.drawerLayout
        )

        binding.activityMainToolbar.setupWithNavController(navController, appBarConfiguration)
    }


}
