package com.example.studentdiary.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentdiary.R
import com.example.studentdiary.notifications.Notification
import com.example.studentdiary.repository.DictionaryRepository
import com.example.studentdiary.ui.DATA_KEY_COMMENT
import com.example.studentdiary.ui.DATA_KEY_RATING
import com.example.studentdiary.webClient.model.RatingSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class APIRatingWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(
    context,
    workerParams
), KoinComponent {
    override suspend fun doWork(): Result {
        val repository: DictionaryRepository by inject()

        val comment = inputData.getString(DATA_KEY_COMMENT)
        val rating = inputData.getFloat(DATA_KEY_RATING, 0.0f)

        return try {
            repository.sendRating(RatingSender(comment = comment, rating = rating))

            Notification(applicationContext)
                .show(
                    title = applicationContext.getString(R.string.api_rating_worker_notification_title),
                    description = applicationContext.getString(R.string.api_rating_worker_notification_description),
                    iconId = R.drawable.ic_info,
                )
            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}