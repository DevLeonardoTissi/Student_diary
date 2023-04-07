package com.example.studentdiary.ui.fragment.disciplineFormFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository
import kotlinx.coroutines.launch

class DisciplineFormViewModel(private val repository: DisciplineRepository) : ViewModel() {

    private val _discipline = MutableLiveData<Discipline?>(null)
    val discipline: LiveData<Discipline?> = _discipline

    suspend fun insert(discipline: Discipline) {
        repository.insert(discipline)
    }

    fun searchDisciplideForId(id: String) {
        viewModelScope.launch {
            _discipline.value = repository.searchId(id)
        }
    }

    fun setInitialHour(hour: Int, minute: Int) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(initialHourt = hour, initialMinute = minute))
        }
    }

    fun setFinalHour(hour: Int, minute: Int) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(finalHour = hour, finalMinute = minute))
        }
    }

    fun setName(name: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(name = name))
        }
    }

    fun setDescription(description: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(description = description))
        }
    }

    fun setImg(url: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(img = url))
        }
    }

    fun setFavorite(favorite: Boolean) {
        _discipline.value?.let {
           it.favorite = favorite
        }
    }

    fun setDate(date:androidx.core.util.Pair<Long, Long>){
        _discipline.value?.let {
            _discipline.postValue(it.copy(date = date))
        }
    }
    fun getDiscipline():Discipline? = discipline.value

    fun setDiscipline(discipline:Discipline){
        _discipline.postValue(discipline)
    }


}