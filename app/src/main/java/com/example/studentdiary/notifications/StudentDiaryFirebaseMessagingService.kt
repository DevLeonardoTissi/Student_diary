package com.example.studentdiary.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class StudentDiaryFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("TAG", "onNewToken: $token")
    }
}