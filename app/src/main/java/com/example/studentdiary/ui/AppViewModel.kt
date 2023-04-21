package com.example.studentdiary.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    private val _navigationComponents = MutableLiveData<NavigationComponents>().also {
        it.value = hasNavigationComponents
    }
    val navigationComponents: LiveData<NavigationComponents> get() = _navigationComponents

    var hasNavigationComponents: NavigationComponents = NavigationComponents()
        set(value) {
            field = value
            _navigationComponents.value = value
        }
}

class NavigationComponents(
    val navigationIcon: Boolean = false,
    val menuDrawer: Boolean = false
)

