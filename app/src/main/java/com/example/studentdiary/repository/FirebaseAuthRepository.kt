package com.example.studentdiary.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.studentdiary.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun register(user: User): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(user.name, user.password)
    }

    fun authenticate(user: User): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(user.name, user.password)
    }

    fun linkWithCredential(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    fun logout() = firebaseAuth.signOut()

    fun isAuthenticated(): Boolean {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        return firebaseUser != null
    }

    fun sendPasswordResetEmail(email:String): Task<Void> {
       return firebaseAuth.sendPasswordResetEmail(email)
    }

    fun updatePassword(password:String): Task<Void>? {
        return firebaseAuth.currentUser?.updatePassword(password)
    }

    fun updateEmail(newEmail:String): Task<Void>? {
        return firebaseAuth.currentUser?.updateEmail(newEmail)
    }

    fun deleteUser(): Task<Void>? {
        return firebaseAuth.currentUser?.delete()
    }


    fun updateUserProfile(name:String? = null, userPhotoUri:Uri? = null): Task<Void>? {
        val userProfileChange = userProfileChangeRequest {
                displayName = name
                photoUri = userPhotoUri

        }

        return firebaseAuth.currentUser?.updateProfile(userProfileChange)
    }

    fun updateUser(){
        firebaseUser.value = FirebaseAuth.getInstance().currentUser
    }


    val firebaseUser = MutableLiveData<FirebaseUser?>().apply {
        val auth = FirebaseAuth.getInstance()
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            value = firebaseAuth.currentUser
        }

        auth.addAuthStateListener(authStateListener)
    }
}