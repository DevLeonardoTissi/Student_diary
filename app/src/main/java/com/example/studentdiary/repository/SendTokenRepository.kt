package com.example.studentdiary.repository

import com.example.studentdiary.ui.GENERATE_TOKEN_USER
import com.example.studentdiary.utils.Sendtoken
import com.google.firebase.firestore.FirebaseFirestore

class SendTokenRepository(private val firestore: FirebaseFirestore) {


    fun sendToken(token:String){
            val document = firestore.collection(GENERATE_TOKEN_USER).document()
            document.set(Sendtoken(token = token))

    }

}

