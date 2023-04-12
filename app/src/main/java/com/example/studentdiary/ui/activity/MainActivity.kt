package com.example.studentdiary.ui.activity

import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted ->
        if (isGranted){
            Log.i("TAG", "Permissão concedida: ")
        }else{
            Log.i("TAG", "Permissão NÃO concedida: ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureMenuDrawer()
        checkCalendarPermission {
            Log.i("TAG", "onCreate: foi")
        }
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

    private fun checkCalendarPermission(whenGranted:() -> Unit){
        val permission = READ_CALENDAR
        if (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ){
            whenGranted()
        }else{
            requestPermission.launch(permission)
            requestPermission.launch(WRITE_CALENDAR)

        }
    }
}
