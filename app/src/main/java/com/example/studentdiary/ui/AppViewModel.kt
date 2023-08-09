package com.example.studentdiary.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.repository.FirebaseStorageRepository
import com.google.firebase.storage.StorageException
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


    private suspend fun updateUserPhoto(photoUrl: Uri? = null) {
        firebaseAuthRepository.updateUserPhoto(userPhotoUri = photoUrl)?.await()
    }


    fun logout() {
        if (firebaseAuthRepository.isAuthenticated()) {
            firebaseAuthRepository.logout()
        }
    }


    fun tryUpdateUserPhoto(file: Uri, onError: (e: Exception) -> Unit, onSuccessful: () -> Unit) {
        viewModelScope.launch {
            try {
                val userPhotographUrl =
                    firebaseStorageRepository.updateUserPhoto(file, firebaseUser.value?.uid)
                updateUserPhoto(photoUrl = userPhotographUrl)
                firebaseAuthRepository.updateUser()
                onSuccessful()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun tryRemoveUserPhoto(onError: (exception: Exception) -> Unit, onSuccessful: () -> Unit) {
        viewModelScope.launch {
            try {
                updateUserPhoto(photoUrl = null)
                firebaseAuthRepository.updateUser()
                firebaseStorageRepository.removeUserPhoto(firebaseUser.value?.uid)
                onSuccessful()

            } catch (exception: Exception) {
                if (exception is StorageException && exception.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    onSuccessful()
                } else {
                    onError(exception)
                }
            }
        }
    }
}


class NavigationComponents(
    val toolbar: Boolean = false,
    val menuDrawer: Boolean = false
)

