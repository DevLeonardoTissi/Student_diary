package com.example.studentdiary.notifications

import com.google.firebase.messaging.FirebaseMessagingService

class StudentDiaryFirebaseMessagingService(): FirebaseMessagingService() {
    companion object{
        fun clear(){
            token = null
        }
        var token:String? = null
            private set
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
       token = newToken
    }
}