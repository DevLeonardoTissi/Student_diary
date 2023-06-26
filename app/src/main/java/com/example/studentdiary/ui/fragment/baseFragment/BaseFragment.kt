package com.example.studentdiary.ui.fragment.baseFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.NavGraphDirections
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import com.example.studentdiary.utils.exitGoogleAndFacebookAccount
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment : Fragment() {

    private val appViewModel: AppViewModel by activityViewModel()
    private val model: LoginViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfItIsAuthenticated()
        setupNavigationComponents()
    }


    private fun goToLogin() {
        val direction = NavGraphDirections.actionGlobalLoginFragment()
        controller.navigate(direction)
    }

    private fun checkIfItIsAuthenticated() {
        if (!model.isAuthenticated()) {
            context?.let {
                exitGoogleAndFacebookAccount(it)
                goToLogin()
            }

        }
    }

     private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(toolbar = true, menuDrawer = true)
    }
}

