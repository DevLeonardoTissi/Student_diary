package com.example.studentdiary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.repository.FirebaseAuthRepository

class AppViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {
    private val _navigationComponents = MutableLiveData<NavigationComponents>().also {
        it.value = hasNavigationComponents
    }
    val navigationComponents: LiveData<NavigationComponents> get() = _navigationComponents

    var hasNavigationComponents: NavigationComponents = NavigationComponents()
        set(value) {
            field = value
            _navigationComponents.value = value
        }

    val userEmail = firebaseAuthRepository.userEmail

    fun logout() {
        if (firebaseAuthRepository.isAuthenticated()) {
            firebaseAuthRepository.logout()
        }
    }
}


class NavigationComponents(
    val toolbar: Boolean = false,
    val menuDrawer: Boolean = false
)

