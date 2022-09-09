package com.example.stopbadhabit.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdbtest.repository.HabitAndDiaryRepository
import com.example.stopbadhabit.data.model.Habit.Habit
import com.example.stopbadhabit.data.model.HabitAndModel.HabitAndDiary
import com.example.stopbadhabit.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.PrivateKey
import javax.inject.Inject

@HiltViewModel
class HabitReportViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val habitAndDiaryRepository: HabitAndDiaryRepository
): ViewModel(){
    private val _habit = MutableLiveData<Habit>()
    val habit : LiveData<Habit> get() = _habit

    private val _habitAndDiary = MutableLiveData<HabitAndDiary>()
    val habitAndDiary: LiveData<HabitAndDiary> get() = _habitAndDiary

    fun getHabitAndDiary(id:Int){
        viewModelScope.launch {
            _habitAndDiary.postValue(habitAndDiaryRepository.getHabitAndDiary(id))
        }
    }



//    fun updateHabitState(state: Int,endDate: String){
//        _habitAndDiary.value?.let {
//            _habitAndDiary.value=it.copy(habit = Habit(state = state, end_date = endDate), )
//        }
//        viewModelScope.launch {
//            _habitAndDiary.value?.let { habitRepository.updateHabit(it) }
//        }
//    }

    fun getHabitDetail(id: Int){
        viewModelScope.launch {
            _habit.postValue(habitRepository.getHabitById(id))
        }
    }

    fun updateState(state: Int, endDate: String){
        _habit.value?.let {
            _habit.value=it.copy(state = state, end_date = endDate)
        }
        viewModelScope.launch {
            _habit.value?.let { habitRepository.updateHabit(it) }

        }
    }


}