package com.example.studentdiary.repository

import com.example.studentdiary.model.Meaning
import com.example.studentdiary.model.Sentence
import com.example.studentdiary.webClient.services.DicioApiService

class DictionaryRepository(private val dictioApiService: DicioApiService) {

    suspend fun searchMeaning(word: String): List<Meaning> {
        return dictioApiService.searchMeaning(word).map { meaningsResponse ->
            meaningsResponse.meaning
        }
    }

    suspend fun searchSynonyms(word: String): List<String> {
        return dictioApiService.searchSynonyms(word)
    }

    suspend fun searchSyllables(word: String): List<String> {
        return dictioApiService.searchSyllables(word)
    }

    suspend fun searchSentences(word: String): List<Sentence> {
        return dictioApiService.searchSentences(word).map { sentencesResponse ->
            sentencesResponse.sentences
        }
    }
}