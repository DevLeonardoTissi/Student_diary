package com.example.studentdiary.ui.fragment.baseFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.NavGraphDirections
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfItIsAuthenticated()
    }

    private fun goToLogin() {
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }

    private fun checkIfItIsAuthenticated() {
        if (!loginViewModel.isAuthenticated()) {
            goToLogin()
        }
    }
}