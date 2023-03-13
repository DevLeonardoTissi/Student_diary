package com.example.studentdiary.repository

import com.example.studentdiary.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {


    fun register(user: User): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(user.name, user.password)
    }

    fun authenticate(user: User): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(user.name, user.password)
    }

    fun linkGoogleAccount(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    fun linkFacebookAccount(credential: AuthCredential): Task<AuthResult>{
        return firebaseAuth.signInWithCredential(credential)
    }
}