package com.example.studentdiary.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.studentdiary.model.Discipline

@Dao
interface DisciplineDao {

    @Query("SELECT * FROM Discipline ORDER BY id DESC")
    fun searchAll(): List<Discipline>
}