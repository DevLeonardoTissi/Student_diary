package com.example.studentdiary.model

class Sentence(
    val sentence: String?,
    val author: String?
) {
    override fun toString(): String {
        return "$sentence, $author"
    }
}