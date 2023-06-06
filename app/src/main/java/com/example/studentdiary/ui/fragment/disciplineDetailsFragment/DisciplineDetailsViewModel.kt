package com.example.studentdiary.ui.fragment.disciplineDetailsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.repository.DisciplineRepository
import kotlinx.coroutines.launch

class DisciplineDetailsViewModel(val repository: DisciplineRepository, val disciplineId: String) :
    ViewModel() {

     val foundDiscipline  = repository.searchIdLiveData(disciplineId)

    fun delete() {
        viewModelScope.launch {
            repository.delete(disciplineId)
        }
    }


}