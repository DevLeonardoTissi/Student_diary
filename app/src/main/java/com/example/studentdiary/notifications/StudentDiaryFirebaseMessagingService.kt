package com.example.studentdiary.notifications

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkRequest.Companion.MIN_BACKOFF_MILLIS
import com.example.studentdiary.ui.UPLOAD_TOKEN_WORKER_TAG
import com.example.studentdiary.utils.workManager.TokenUploadWorker
import com.google.firebase.messaging.FirebaseMessagingService
import java.util.concurrent.TimeUnit

class StudentDiaryFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        fun clear() {
            token = null
        }

        var token: String? = null
            private set
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<TokenUploadWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                ).addTag(UPLOAD_TOKEN_WORKER_TAG)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)
    }
}