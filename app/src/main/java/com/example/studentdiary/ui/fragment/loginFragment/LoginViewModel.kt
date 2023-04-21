package com.example.studentdiary.ui.fragment.loginFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.User
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.utils.Resource
import com.google.firebase.auth.AuthCredential

class LoginViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {

    private val _firebaseAuthLiveData = MutableLiveData<Resource<Boolean>>()
    val firebaseAuthLiveData = _firebaseAuthLiveData

    fun authenticate(user: User): MutableLiveData<Resource<Boolean>> {
        try {
            val task = firebaseAuthRepository.authenticate(user)
            task.addOnSuccessListener { _firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener { _firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: IllegalArgumentException) {
            _firebaseAuthLiveData.value = Resource(false, e)
        }
        return firebaseAuthLiveData
    }

    fun linkGoogleAccount(credential: AuthCredential): MutableLiveData<Resource<Boolean>> {
        try {
            val task = firebaseAuthRepository.linkGoogleAccount(credential)
            task.addOnSuccessListener { _firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener {_firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: Exception) {
            Resource(false, e)
        }
        return firebaseAuthLiveData
    }

    fun linkFacebookAccount(credential: AuthCredential): MutableLiveData<Resource<Boolean>> {
        try {
            val task = firebaseAuthRepository.linkFacebookAccount(credential)
            task.addOnSuccessListener { _firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener { _firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: Exception) {
            Resource(false, e)
        }
        return firebaseAuthLiveData
    }

    fun logout() = firebaseAuthRepository.logout()

    fun isAuthenticated() = firebaseAuthRepository.isAuthenticated()

}