package com.example.studentdiary.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.studentdiary.model.Discipline

@Dao
interface DisciplineDao {

    @Query("SELECT * FROM Discipline")
    fun searchAll(): LiveData<List<Discipline>>


    @Insert(onConflict = REPLACE)
    suspend fun insert(discipline: Discipline)

    @Query("SELECT * FROM Discipline WHERE id =:id")
    suspend fun searchId(id:String): Discipline
}
