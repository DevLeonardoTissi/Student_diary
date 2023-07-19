package com.example.studentdiary.notifications

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.studentdiary.ui.SEND_TOKEN_PREFERENCES_KEY
import com.example.studentdiary.datastore.dataStore
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentDiaryFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        CoroutineScope(Dispatchers.IO).launch {
            applicationContext.dataStore.edit {preferences ->
                preferences[stringPreferencesKey(SEND_TOKEN_PREFERENCES_KEY)] = newToken
            }
        }
    }
}