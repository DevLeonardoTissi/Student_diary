package com.example.studentdiary.repository

import android.content.Context
import androidx.work.WorkManager
import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

class DisciplineRepository(private val dao: DisciplineDao) {
    fun searchAll() = dao.searchAll()
    suspend fun insert(discipline: Discipline) = dao.insert(discipline)
    suspend fun searchId(id: String?) = dao.searchId(id)
    fun searchIdLiveData(id: String) = dao.searchIdLiveData(id)
    suspend fun delete(id: String) = dao.delete(id)


     fun cancelWorkerDisciplineReminder(context: Context, id: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(id)
    }
}