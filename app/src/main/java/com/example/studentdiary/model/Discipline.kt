package com.example.studentdiary.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Discipline(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String? = null,
    var initialHourt: Int? = null,
    var initialMinute: Int? = null,
    var finalHour: Int? = null,
    var finalMinute: Int?= null,
    var favorite: Boolean = false,
    var img: String? = null
)