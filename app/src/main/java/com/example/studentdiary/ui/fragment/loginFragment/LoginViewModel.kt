package com.example.studentdiary.ui.fragment.loginFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.User
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.utils.Resource
import com.google.firebase.auth.AuthCredential

class LoginViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _firebaseAuthLiveData = MutableLiveData<Resource<Boolean>>()
    val firebaseAuthLiveData: LiveData<Resource<Boolean>> = _firebaseAuthLiveData

    private val _fieldEmail = MutableLiveData<String>()
    val fieldEmail: LiveData<String> = _fieldEmail

    private val _fieldPassword = MutableLiveData<String>()
    val fieldPassword: LiveData<String> = _fieldPassword

    fun setEmail(email: String) {
        _fieldEmail.value = email
    }

    fun setPassword(password: String) {
        _fieldPassword.value = password
    }

    fun clearLiveData() {
        _firebaseAuthLiveData.value = Resource(false, null)
    }

    fun authenticate(user: User) {
        try {
            val task = firebaseAuthRepository.authenticate(user)
            task.addOnSuccessListener { _firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener { _firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: IllegalArgumentException) {
            _firebaseAuthLiveData.value = Resource(false, e)
        }
    }

    fun loginWithCredential(credential: AuthCredential) {
        try {
            val task = firebaseAuthRepository.linkWithCredential(credential)
            task.addOnSuccessListener { _firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener { _firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: Exception) {
            _firebaseAuthLiveData.value = Resource(false, e)
        }
    }

    fun forgotPassword(email:String, onSucess:() -> Unit = {}, onFailure:(e:Exception) -> Unit = {}){
        try {
            val task = firebaseAuthRepository.sendPasswordResetEmail(email)
            task.addOnSuccessListener { onSucess() }
            task.addOnFailureListener { onFailure(it) }
        }catch (e:Exception){
                onFailure(e)
        }
    }

    fun isAuthenticated() = firebaseAuthRepository.isAuthenticated()

}