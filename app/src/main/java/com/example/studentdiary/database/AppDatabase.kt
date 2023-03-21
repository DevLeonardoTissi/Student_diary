package com.example.studentdiary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

@Database(entities = [Discipline::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val disciplineDAO: DisciplineDao
}