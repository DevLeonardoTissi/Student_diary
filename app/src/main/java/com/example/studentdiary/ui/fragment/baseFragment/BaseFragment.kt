package com.example.studentdiary.ui.fragment.baseFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.NavGraphDirections
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment : Fragment() {
    private val model: LoginViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfItIsAuthenticated()
    }

    private fun goToLogin() {
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }

    private fun checkIfItIsAuthenticated() {
        if (!model.isAuthenticated()) {
            goToLogin()
        }
    }
}

