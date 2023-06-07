package com.example.studentdiary.utils.concatUtils

fun concatenateTimeValues(time: Pair<Int, Int>): String {
    return "${time.component1().toString().padStart(2, '0')}:${
        time.component2().toString().padStart(2, '0')
    }"
}
