package com.example.studentdiary.ui.fragment.dictionaryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.model.Meaning
import com.example.studentdiary.model.Sentence
import com.example.studentdiary.repository.DictionaryRepository
import kotlinx.coroutines.launch

class DictionaryViewModel(private val repository: DictionaryRepository) : ViewModel() {

    private val _fieldWord = MutableLiveData<String>()
    val fieldWord: LiveData<String> = _fieldWord

    private val _meaning = MutableLiveData<List<Meaning>>()
    val meaning: LiveData<List<Meaning>> = _meaning

    private val _synonyms = MutableLiveData<List<String>>()
    val synonyms: LiveData<List<String>> = _synonyms

    private val _syllables = MutableLiveData<List<String>>()
    val syllables: LiveData<List<String>> = _syllables

    private val _sentences = MutableLiveData<List<Sentence>>()
    val senteces: LiveData<List<Sentence>> = _sentences


     fun setQuery(word:String){
        _fieldWord.value = word
    }


    fun searchMeaning(word: String){
        viewModelScope.launch {
            try {
                _meaning.value = repository.searchMeaning(word)
            }catch (e:Exception){

            }
        }

    }


     fun searchSynonyms(word: String) {
        viewModelScope.launch {
            try {
                _synonyms.value = repository.searchSynonyms(word)
            }catch (e:Exception){

            }
        }
    }

     fun searchSyllables(word: String) {
        viewModelScope.launch {
            try {
                _syllables.value = repository.searchSyllables(word)
            }catch (e:Exception){

            }

        }
    }

     fun searchSentences(word: String) {
       viewModelScope.launch {
           try {
               _sentences.value = repository.searchSentences(word)
           }catch (e:Exception){

           }
       }
    }
}

