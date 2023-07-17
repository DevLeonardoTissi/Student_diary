package com.example.studentdiary.utils.workManager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TokenUploadWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}