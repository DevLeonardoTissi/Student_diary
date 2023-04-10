package com.example.studentdiary.database.converters

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromLongPair(pair: androidx.core.util.Pair<Long, Long>): String {
        return "${pair.first},${pair.second}"
    }

    @TypeConverter
    fun stringToPairLong(pairString: String): androidx.core.util.Pair<Long, Long> {
        val (first, second) = pairString.split(",")
        return androidx.core.util.Pair(first.toLong(), second.toLong())
    }

    @TypeConverter
    fun fromIntPair(pair: Pair<Int, Int>): String {
        return "${pair.first},${pair.second}"
    }


    @TypeConverter
    fun stringToPairInt(pairString: String): Pair<Int, Int> {
        val (first, second) = pairString.split(",")
        return Pair(first.toInt(), second.toInt())
    }
}