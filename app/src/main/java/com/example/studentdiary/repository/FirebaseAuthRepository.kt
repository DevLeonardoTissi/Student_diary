package com.example.studentdiary.repository

import com.example.studentdiary.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {


    fun register(user: User): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(user.name, user.password)
    }
}