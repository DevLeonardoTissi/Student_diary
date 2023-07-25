package com.example.studentdiary.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studentdiary.repository.DictionaryRepository
import com.example.studentdiary.webClient.model.RatingSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class APIRatingWorker(context: Context, workerParams: WorkerParameters) :CoroutineWorker(context,
    workerParams
), KoinComponent {
    //Extrair chaves do input data para constantes
    override suspend fun doWork(): Result {
        val repository: DictionaryRepository by inject()

        val comment = inputData.getString("comment")
        val rating = inputData.getFloat("rating", 0.0f)

        return try {
            repository.sendRating(RatingSender(comment = comment, rating = rating))
            Result.success()
        }catch (e:Exception){
            Result.retry()
        }
    }
}