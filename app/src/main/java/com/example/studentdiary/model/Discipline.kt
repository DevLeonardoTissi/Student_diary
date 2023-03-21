package com.example.studentdiary.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Discipline(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String,
    var startTime: String,
    var endTime: String,
    var favorite: Boolean
)