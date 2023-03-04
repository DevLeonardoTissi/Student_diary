package com.example.studentdiary.ui.fragment.registerFragment

import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.User
import com.example.studentdiary.repository.FirebaseAuthRepository

class RegisterViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {

    fun register (user: User){
        firebaseAuthRepository.register(user)
    }

}