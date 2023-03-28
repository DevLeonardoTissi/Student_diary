package com.example.studentdiary.repository

import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

class DisciplineRepository(private val dao: DisciplineDao) {

    fun searchAll() = dao.searchAll()

//    fun searchFavorites() = dao.searchFavorites()

    suspend fun insert(discipline: Discipline) {
        dao.insert(discipline)
    }

    suspend fun searchId(id: String) {
        dao.searchId(id)
    }


}