package com.example.studentdiary.ui.fragment.dictionaryFragment

import androidx.lifecycle.ViewModel
import com.example.studentdiary.repository.DictionaryRepository

class DictionaryViewModel(private val repository: DictionaryRepository): ViewModel() {

    suspend fun searchMeaning(word: String){
        repository.searchMeaning(word)
    }

    suspend fun searchSynonyms(word: String){
        repository.searchSynonyms(word)
    }

    suspend fun searchSyllables(word: String){
        repository.searchSyllables(word)
    }

    suspend fun searchSentences(word: String){
        repository.searchSentences(word)
    }
}

