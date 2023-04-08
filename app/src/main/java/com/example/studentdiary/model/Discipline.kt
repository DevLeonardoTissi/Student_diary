package com.example.studentdiary.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Discipline(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var description: String? = null,
    var initialHour: Int? = null,
    var initialMinute: Int? = null,
    var finalHour: Int? = null,
    var finalMinute: Int? = null,
    var favorite: Boolean = false,
    var img: String? = null,
    val date: androidx.core.util.Pair<Long, Long>? = null
)