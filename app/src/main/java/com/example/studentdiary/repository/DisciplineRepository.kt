package com.example.studentdiary.repository

import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

class DisciplineRepository(private val dao: DisciplineDao) {

    fun searchAll() = dao.searchAll()
    suspend fun insert(discipline: Discipline) = dao.insert(discipline)

    suspend fun searchId(id: String): Discipline = dao.searchId(id)

    suspend fun delete(id: String) = dao.delete(id)


}