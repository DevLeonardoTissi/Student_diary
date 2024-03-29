package com.example.studentdiary.ui.fragment.profileFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {
    fun setUserEmail(userEmail: String?) {
        _userEmail.value = userEmail
    }

    fun setUserName(userName: String?) {
        _userName.value = userName
    }


    val firebaseUser = authRepository.firebaseUser.value

    private val _userName = MutableLiveData<String?>(firebaseUser?.displayName)
    val userName: LiveData<String?> = _userName

    private val _userEmail = MutableLiveData<String?>(firebaseUser?.email)
    val userEmail: LiveData<String?> = _userEmail


    fun updatePassword(password: String, onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.updatePassword(password)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateUserEmailAndUserName(
        name: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                updateEmail(email)
                updateUserName(name)
                authRepository.updateUser()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteUser(onSuccess: () -> Unit, onError: (e: Exception) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.deleteUser()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private suspend fun updateEmail(email: String) {
        authRepository.updateEmail(email)?.await()
    }

    private suspend fun updateUserName(name: String) {
        authRepository.updateUserName(name = name)?.await()
    }

    fun reAuthenticate(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                authRepository.reAuthenticate(credential)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}