package com.example.studentdiary.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.studentdiary.ui.SEND_TOKEN_PREFERENCES_KEY
import com.example.studentdiary.ui.USER_AUTH_PROVIDER
import com.example.studentdiary.ui.USER_AUTH_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User_Token")

suspend fun getUserAuthProvider(context: Context): String? {
    return context.dataStore.data.first()[stringPreferencesKey(
        USER_AUTH_PROVIDER
    )]
}

suspend fun setUserProvider(context: Context, provider: String) {
    context.dataStore.edit { preferences ->
        preferences[stringPreferencesKey(USER_AUTH_PROVIDER)] = provider
    }
}

suspend fun getUserTokenAuth(context: Context): String? {
    return context.dataStore.data.first()[stringPreferencesKey(USER_AUTH_TOKEN)]
}

suspend fun setUserTokenAuth(context: Context, token: String) {
    context.dataStore.edit { preferences ->
        preferences[stringPreferencesKey(USER_AUTH_TOKEN)] = token
    }
}

suspend fun removeUserTokenAuth(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(stringPreferencesKey(USER_AUTH_TOKEN))
    }
}

suspend fun getUserTokenCloudMessaging(context: Context): String? {
    return context.dataStore.data.first()[stringPreferencesKey(
        SEND_TOKEN_PREFERENCES_KEY
    )]
}

 fun getCollectUserTokenCloudMessaging(context: Context): Flow<String?> {
    val preferencesKey = stringPreferencesKey(SEND_TOKEN_PREFERENCES_KEY)
    return context.dataStore.data.map { preferences ->
        preferences[preferencesKey]
    }
}


suspend fun setUSerTokenCloudMessaging(context: Context, token: String) {
    context.dataStore.edit { preferences ->
        preferences[stringPreferencesKey(SEND_TOKEN_PREFERENCES_KEY)] = token
    }
}

suspend fun removeUserTokenCloudMessaging(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(
            stringPreferencesKey(
                SEND_TOKEN_PREFERENCES_KEY
            )
        )
    }
}
