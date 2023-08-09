package com.example.studentdiary.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studentdiary.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

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

    fun sendPasswordResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    suspend fun updatePassword(password: String): Void? {
        return firebaseAuth.currentUser?.updatePassword(password)?.await()
    }

    fun updateEmail(newEmail: String): Task<Void>? {
        return firebaseAuth.currentUser?.updateEmail(newEmail)
    }

    suspend fun deleteUser(): Void? {
        return firebaseAuth.currentUser?.delete()?.await()
    }

    fun updateUserPhoto(userPhotoUri: Uri? = null): Task<Void>? {
        val userProfileChange = userProfileChangeRequest {
            photoUri = userPhotoUri
        }
        return firebaseAuth.currentUser?.updateProfile(userProfileChange)
    }


    fun updateUserName(name: String? = null): Task<Void>? {
        val userProfileChange = userProfileChangeRequest {
            name?.let {
                displayName = name
            }
        }
        return firebaseAuth.currentUser?.updateProfile(userProfileChange)
    }


    fun updateUser() {
        _firebaseUser.value = FirebaseAuth.getInstance().currentUser
    }

    suspend fun reauthenticate(credential: AuthCredential): Void? {
        return firebaseAuth.currentUser?.reauthenticate(credential)?.await()
    }

    private val _firebaseUser = MutableLiveData<FirebaseUser?>().apply {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            value = firebaseAuth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

}