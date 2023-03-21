package com.example.studentdiary.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.studentdiary.model.Discipline

@Dao
interface DisciplineDao {

    @Query("SELECT * FROM Discipline ORDER BY id DESC")
    suspend fun searchAll(): List<Discipline>

    @Insert(onConflict = REPLACE)
    suspend fun insert(discipline: Discipline)
}