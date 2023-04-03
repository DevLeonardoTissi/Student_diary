package com.example.studentdiary.ui.fragment.disciplinesFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository

class DisciplinesViewModel(repository: DisciplineRepository) : ViewModel() {

    var disciplineList: LiveData<List<Discipline>> = repository.searchAll()
}
