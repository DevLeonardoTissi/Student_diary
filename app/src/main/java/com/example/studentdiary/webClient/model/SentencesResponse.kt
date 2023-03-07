package com.example.studentdiary.webClient.model

import com.example.studentdiary.model.Sentence

class SentencesResponse(
    private val sentence: String?,
    private val author: String?
) {

    val sentences: Sentence
        get() = Sentence(
            sentence = sentence ?: "",
            author = author ?: ""
        )
}