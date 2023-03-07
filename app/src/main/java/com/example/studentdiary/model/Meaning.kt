package com.example.studentdiary.model

class Meaning(
    val partOfSpeech: String?,
    val meanings: List<String?>,
    val etymology: String?
){
    override fun toString(): String {
        return "$partOfSpeech, $meanings, $etymology"

    }
}