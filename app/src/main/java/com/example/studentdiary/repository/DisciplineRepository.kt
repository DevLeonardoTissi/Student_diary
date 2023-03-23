package com.example.studentdiary.repository

import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

class DisciplineRepository(private val dao: DisciplineDao) {

    suspend fun searchAll(): List<Discipline> = dao.searchAll()

    suspend fun insert(discipline: Discipline) {
        dao.insert(discipline)
    }
}