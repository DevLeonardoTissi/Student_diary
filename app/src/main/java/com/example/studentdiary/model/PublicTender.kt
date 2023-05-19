package com.example.studentdiary.model

data class PublicTender(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var url: String? = null,
    var img: String? = null,
    var contest: Boolean = false,
    var course: Boolean  = false
)