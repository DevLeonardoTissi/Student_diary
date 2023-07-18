package com.example.studentdiary.utils.workManager

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentdiary.notifications.StudentDiaryFirebaseMessagingService
import com.example.studentdiary.repository.SendTokenRepository
import com.example.studentdiary.ui.SEND_TOKEN_PREFERENCES_KEY
import com.example.studentdiary.utils.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TokenUploadWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(
    context,
    workerParams
), KoinComponent {

    private val sendTokenRepository: SendTokenRepository by inject()


    override suspend fun doWork(): Result {
        val token = applicationContext.dataStore.data.first()[stringPreferencesKey(
            SEND_TOKEN_PREFERENCES_KEY
        )]
        return if (token != null) {
            try {
                withContext(Dispatchers.IO) {
                    sendTokenRepository.sendToken(token)
                }
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        } else {
            Result.failure()
        }
    }
}
