package com.example.studentdiary.ui.fragment.registerFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.User
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.utils.Resource

class RegisterViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
) : ViewModel() {
    val firebaseAuthLiveData = MutableLiveData<Resource<Boolean>>()

    private val _fieldEmail = MutableLiveData<String>()
    val fieldEmail: LiveData<String> = _fieldEmail

    private val _fieldPassword = MutableLiveData<String>()
    val fieldPassword: LiveData<String> = _fieldPassword

    private val _fieldPasswordChecker = MutableLiveData<String>()
    val fieldPasswordChecker: LiveData<String> = _fieldPasswordChecker

    fun setEmail(email: String) {
        _fieldEmail.value = email
    }

    fun setPassword(password: String) {
        _fieldPassword.value = password
    }

    fun setPasswordChecker(password: String) {
        _fieldPasswordChecker.value = password
    }

    fun register(user: User) {
        try {
            val task = firebaseAuthRepository.register(user)
            task.addOnSuccessListener { firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener { firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: IllegalArgumentException) {
            firebaseAuthLiveData.value = Resource(false, e)
        }
    }


}