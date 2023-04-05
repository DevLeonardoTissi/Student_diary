package com.example.studentdiary.utils

 fun formatTime(hour: Int?, minute: Int?): String {
    return  "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}
