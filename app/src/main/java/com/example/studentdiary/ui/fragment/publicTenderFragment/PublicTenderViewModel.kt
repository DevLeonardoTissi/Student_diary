package com.example.studentdiary.ui.fragment.publicTenderFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.repository.PublicTenderRepository
import com.example.studentdiary.utils.PublicTenderSuggestion

class PublicTenderViewModel(private val publicTenderRepository: PublicTenderRepository) :
    ViewModel() {

    val publicTenderList: LiveData<List<PublicTender>> = publicTenderRepository.search()

    private val _cardViewSuggestionsIsOpen = MutableLiveData(false)
    fun setIsOpen(isOpen: Boolean) {
        _cardViewSuggestionsIsOpen.value = isOpen
    }

    fun getIsOpen(): Boolean = _cardViewSuggestionsIsOpen.value ?: false


    fun addPublicTenderSuggestion(publicTenderSuggestion: PublicTenderSuggestion, onSuccess:() -> Unit, onFailure:() -> Unit) {
        val task = publicTenderRepository.addPublicTenderSuggestion(publicTenderSuggestion)
        task.addOnSuccessListener {
            onSuccess()
        }
        task.addOnFailureListener {
            onFailure()
        }
    }
}