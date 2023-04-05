package com.example.studentdiary.ui.fragment.disciplineFormFragment

import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository

class DisciplineFormViewModel(private val repository: DisciplineRepository) : ViewModel() {

    suspend fun insert(discipline: Discipline) {
        repository.insert(discipline)
    }

    suspend fun searDisciplideForId(id: String): Discipline =
        repository.searchId(id)


}