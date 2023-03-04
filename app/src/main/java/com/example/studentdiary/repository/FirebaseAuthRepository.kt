package com.example.studentdiary.repository

import android.util.Log
import com.example.studentdiary.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {


    fun register (user: User){
        try {
            val task = firebaseAuth.createUserWithEmailAndPassword(user.name, user.password)
            task.addOnSuccessListener { Log.i("TAG", "register: register sucesseful") }

            task.addOnFailureListener { exception ->
                Log.e("TAG", "register: rregister fail", exception )
                val errorMessage: String = when (exception){
                    is FirebaseAuthWeakPasswordException -> "Senha precisa de pelo menos 6 dígitos"
                    is FirebaseAuthInvalidCredentialsException -> "E-mail inválido"
                    is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
                    else -> "Erro desconhecido"
                }
                Log.i("TAG", "register: $errorMessage")
            }
        } catch (e: IllegalArgumentException){
            Log.e("TAG", "register: ", e )
        }
    }
}