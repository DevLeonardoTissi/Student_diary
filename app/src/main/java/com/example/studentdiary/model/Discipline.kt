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
    var startTime: Pair<Int, Int>? = null,
    var endTime: Pair<Int, Int>? = null,
    var favorite: Boolean = false,
    var img: String? = null,
    val date: androidx.core.util.Pair<Long, Long>? = null,
    var eventId: Long? = null,
    var userCalendarEmail: String? = null,
    var userEmailType:String? = null,
    var completed: Boolean = false
)