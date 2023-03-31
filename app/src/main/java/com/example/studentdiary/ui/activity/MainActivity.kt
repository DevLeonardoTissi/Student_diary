package com.example.studentdiary.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity() : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView
            .setupWithNavController(navController)

        //CONFIGURANDO A NAVEGAÇÃO NA TOOLBAR
//        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
//        binding.activityMainToolbar.setupWithNavController(navController, appBarConfiguration)

        //NAVEGAÇÃO NO DRAWER NO APP
        binding.activityMainToolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

    }

}