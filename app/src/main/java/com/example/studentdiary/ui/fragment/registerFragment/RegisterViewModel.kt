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

    fun register(user: User): LiveData<Resource<Boolean>> {

        try {
            val task = firebaseAuthRepository.register(user)
            task.addOnSuccessListener { firebaseAuthLiveData.value = Resource(true) }
            task.addOnFailureListener { firebaseAuthLiveData.value = Resource(false, it) }
        } catch (e: IllegalArgumentException) {
            firebaseAuthLiveData.value = Resource(false, e)
        }
        return firebaseAuthLiveData
    }
}