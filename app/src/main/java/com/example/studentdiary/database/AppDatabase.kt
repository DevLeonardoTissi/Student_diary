package com.example.studentdiary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studentdiary.database.converters.Converters
import com.example.studentdiary.database.dao.DisciplineDao
import com.example.studentdiary.model.Discipline

@Database(entities = [Discipline::class], version = 1, exportSchema = false)

@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {

    abstract val disciplineDAO: DisciplineDao
}