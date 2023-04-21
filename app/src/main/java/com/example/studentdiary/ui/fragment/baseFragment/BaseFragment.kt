package com.example.studentdiary.ui.fragment.baseFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.NavGraphDirections
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BaseFragment: Fragment() {
    private val loginViewModel:LoginViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun goToLogin(){
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }



}