package com.example.studentdiary.database.converters

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPair(pair: androidx.core.util.Pair<Long, Long>): String {
        return "${pair.first},${pair.second}"
    }

    @TypeConverter
    fun stringToPair(pairString: String): androidx.core.util.Pair<Long, Long> {
        val (first, second) = pairString.split(",")
        return androidx.core.util.Pair(first.toLong(), second.toLong())
    }
}