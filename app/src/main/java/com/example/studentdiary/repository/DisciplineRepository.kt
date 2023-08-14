package com.example.studentdiary.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.workManager.DisciplineReminderWorker
import java.util.concurrent.TimeUnit

class DisciplineRepository(private val dao: DisciplineDao) {
    fun searchAll() = dao.searchAll()
    suspend fun insert(discipline: Discipline) = dao.insert(discipline)
    suspend fun searchId(id: String?) = dao.searchId(id)
    fun searchIdLiveData(id: String) = dao.searchIdLiveData(id)
    suspend fun delete(id: String) = dao.delete(id)


     fun cancelWorkerDisciplineReminder(context: Context, id: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(id)
    }

    fun addWorkerDisciplineReminder (context: Context, startMillis:Long, disciplineId:String){
        val addReminder = OneTimeWorkRequestBuilder<DisciplineReminderWorker>()
            .setInitialDelay(startMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .addTag(disciplineId)
            .build()
        WorkManager.getInstance(context).enqueue(addReminder)
    }
}