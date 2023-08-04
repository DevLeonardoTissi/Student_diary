package com.example.studentdiary.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentdiary.datastore.getUserTokenCloudMessaging
import com.example.studentdiary.datastore.removeUserTokenCloudMessaging
import com.example.studentdiary.repository.SendTokenRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TokenUploadWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(
    context,
    workerParams
), KoinComponent {

    private val sendTokenRepository: SendTokenRepository by inject()
    override suspend fun doWork(): Result {
        val token = getUserTokenCloudMessaging(applicationContext)
        return if (token != null) {
            try {
                sendTokenRepository.sendToken(token)
                removeUserTokenCloudMessaging(applicationContext)
                Result.success()

            } catch (e: Exception) {
                Result.retry()
            }
        } else {
            Result.failure()
        }
    }
}
