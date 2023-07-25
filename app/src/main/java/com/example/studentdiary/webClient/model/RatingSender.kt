package com.example.studentdiary.webClient.model

import com.google.firebase.auth.FirebaseAuth

class RatingSender(
    @Suppress("unused")
    private val userEmail : String? = FirebaseAuth.getInstance().currentUser?.email,
    @Suppress("unused")
     val rating: Float,
    @Suppress("unused")
   val comment: String? = null
)