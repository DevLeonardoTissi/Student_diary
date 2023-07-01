package com.example.studentdiary.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.repository.SendTokenRepository
import java.lang.Exception

class AppViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val sendTokenRepository: SendTokenRepository
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



    fun updateUserProfile(name: String? = null, photoUrl: Uri? = null ) {
        val task = firebaseAuthRepository.updateUserProfile(name = name, userPhotoUri = photoUrl )
        task?.let {
            try {
                task.addOnSuccessListener {
                    firebaseAuthRepository.updateUser()
                }
                task.addOnFailureListener { }
            } catch (e: Exception) {
                Log.i("TAG", "updateUserProfile: error")
            }
        }
    }






    fun logout() {
        if (firebaseAuthRepository.isAuthenticated()) {
            firebaseAuthRepository.logout()
        }
    }

    fun sendToken(t: String) {
        sendTokenRepository.sendToken(t)
    }


}


class NavigationComponents(
    val toolbar: Boolean = false,
    val menuDrawer: Boolean = false
)

