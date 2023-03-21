package com.example.studentdiary.repository

import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

class DisciplineRepository(private val dao: DisciplineDao) {

    fun searchAll(): List<Discipline> = dao.searchAll()

}