package com.example.studentdiary.ui.fragment.disciplineDetailsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.repository.DisciplineRepository
import kotlinx.coroutines.launch

class DisciplineDetailsViewModel(var repository: DisciplineRepository): ViewModel() {

    fun delete(id:String) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun searchDisciplineForId(id:String){
        viewModelScope.launch {
            repository.searchId(id)
        }
    }

}