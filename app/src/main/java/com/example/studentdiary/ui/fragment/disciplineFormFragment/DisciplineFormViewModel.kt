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

    fun insert() {
        viewModelScope.launch {
            discipline.value?.let {
                repository.insert(it)
            }
        }
    }
    fun searchDisciplineForId(id: String) {
        viewModelScope.launch {
            _discipline.postValue(repository.searchId(id))
        }
    }
    fun getDiscipline(): Discipline? = discipline.value
    fun setDiscipline() {
        _discipline.postValue(Discipline())
    }

    fun getEventId(): Long? = discipline.value?.eventId
    fun setStartTime(hour: Int, minute: Int) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(startTime = Pair(hour, minute)))
        }
    }
    fun getStartTime(): Pair<Int, Int>? = discipline.value?.startTime
    fun setEndTime(hour: Int, minute: Int) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(endTime = Pair(hour, minute)))
        }
    }
    fun getEndTime(): Pair<Int, Int>? = discipline.value?.endTime
    fun setName(name: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(name = name))
        }
    }

    fun getName():String? = discipline.value?.name
    fun setDescription(description: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(description = description))
        }
    }

    fun getDescription():String? = discipline.value?.description
    fun setImg(url: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(img = url))
        }
    }
    fun getImg(): String? = discipline.value?.img
    fun setFavorite(favorite: Boolean) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(favorite = favorite))
        }
    }
    fun setDate(date: androidx.core.util.Pair<Long, Long>) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(date = date))
        }
    }
    fun getDate(): androidx.core.util.Pair<Long, Long>? = discipline.value?.date
    fun setEventId(id:Long?) {
        _discipline.value?.let {
           _discipline.postValue(it.copy(eventId = id))
        }
    }

}