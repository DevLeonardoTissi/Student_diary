package com.example.studentdiary.webClient.model

import com.example.studentdiary.model.Meaning

class MeaningsResponse(
    private val partOfSpeech: String?,
    private val meanings: List<String>?,
    private val etymology: String?
) {
    val meaning: Meaning
        get() = Meaning(
            partOfSpeech = partOfSpeech ?: "",
            meanings = meanings ?: emptyList(),
            etymology = etymology ?: ""
        )
}