package com.example.studentdiary.notifications

import android.util.Log
import com.example.studentdiary.datastore.setUSerTokenCloudMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentDiaryFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("TAG", "onNewToken: tokenCriado")
            setUSerTokenCloudMessaging(context = applicationContext, token = newToken)
        }
    }
}