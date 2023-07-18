package com.example.studentdiary.utils.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentdiary.notifications.StudentDiaryFirebaseMessagingService
import com.example.studentdiary.repository.SendTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TokenUploadWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(
    context,
    workerParams
), KoinComponent {

    private val sendTokenRepository: SendTokenRepository by inject()


    override suspend fun doWork(): Result {
        val token = StudentDiaryFirebaseMessagingService.token
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
