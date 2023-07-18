package com.example.studentdiary.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.repository.FirebaseStorageRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository
) : ViewModel() {
    private val _navigationComponents = MutableLiveData<NavigationComponents>().also {
        it.value = hasNavigationComponents
    }
    val navigationComponents: LiveData<NavigationComponents> get() = _navigationComponents

    var hasNavigationComponents: NavigationComponents = NavigationComponents()
        set(value) {
            field = value
            _navigationComponents.value = value
        }

    val firebaseUser = firebaseAuthRepository.firebaseUser


    private suspend fun updateUserProfile(name: String? = null, photoUrl: Uri? = null) {
        firebaseAuthRepository.updateUserProfile(name = name, userPhotoUri = photoUrl)?.await()
    }


    fun logout() {
        if (firebaseAuthRepository.isAuthenticated()) {
            firebaseAuthRepository.logout()
        }
    }


    fun updateUserPhoto(file: Uri, onError: () -> Unit, onSuccessful: () -> Unit) {
        viewModelScope.launch {
            try {
                val userPhotographUrl = firebaseStorageRepository.updateUserPhoto(file,firebaseUser.value?.email)
                updateUserProfile(photoUrl = userPhotographUrl)
                firebaseAuthRepository.updateUser()
                onSuccessful()
            } catch (e: Exception) {
                onError()
            }
        }
    }

    fun removeUserPhoto(onError: () -> Unit, onSuccessful: () -> Unit) {
        viewModelScope.launch {
            try {
                firebaseStorageRepository.removeUserPhoto(firebaseUser.value?.email)
                updateUserProfile(photoUrl = null)
                firebaseAuthRepository.updateUser()
                onSuccessful()
            } catch (e: Exception) {
                onError()
            }
        }
    }
}


class NavigationComponents(
    val toolbar: Boolean = false,
    val menuDrawer: Boolean = false
)

