package com.example.stopbadhabit.ui.viewmodel

import android.app.Application
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdbtest.repository.DiaryRepository
import com.example.roomdbtest.repository.HabitAndDiaryRepository
import com.example.stopbadhabit.data.model.Diary.Diary
import com.example.stopbadhabit.data.model.Habit.Habit
import com.example.stopbadhabit.data.model.HabitAndModel.HabitAndDiary
import com.example.stopbadhabit.data.model.PresentHabit.PresentHabit
import com.example.stopbadhabit.data.repository.HabitRepository
import com.example.stopbadhabit.util.ListLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class  MainViewModel @Inject constructor(
    private val application: Application,
    private val habitRepository: HabitRepository,
    private val diaryRepository: DiaryRepository,
    private val habitAndDiaryRepository: HabitAndDiaryRepository
):ViewModel(){
    private val _habitList = ListLiveData<PresentHabit>()
    val habitList : LiveData<ArrayList<PresentHabit>> get() = _habitList

    private val _diaryList = ListLiveData<Diary>()
    val diaryList : LiveData<ArrayList<Diary>> get() = _diaryList

    private val _detailHabit =  MutableLiveData(-1)
    val detailHabitId: LiveData<Int> get() = _detailHabit
//    var detailHabitId = -1

    init {
        viewModelScope.launch {
            getHabitList()
        }
    }

    fun setDetailId(id: Int) {
        _detailHabit.value = id
    }

    fun getHabitList() {
        viewModelScope.launch {
            _habitList.clear()
            val list = viewModelScope.async(Dispatchers.IO) {
//            _habitList.addAllAsync(repository.getHomeHabits())
                habitRepository.getHomeHabits()
            }.await()
            Log.e(javaClass.simpleName,"${list}")
            _habitList.addAll(list)
        }
    }

    fun getDiaryList(id:Int) {
        viewModelScope.launch {
            _diaryList.clear()
            val list = viewModelScope.async(Dispatchers.IO) {
                diaryRepository.getDiaryAll(id)
            }.await()

            _diaryList.addAll(list)
        }
    }

    fun insertHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            habitRepository.insertHabit(habit)
            getHabitList()
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            diaryRepository.deleteAll()
            habitRepository.deleteAll()
            getHabitList()
        }
    }

    fun insertDiary(diary: Diary, id: Int, habit: Habit?) {
        habit ?:return
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.insertDiary(diary)
            habitRepository.updateHabit(habit)
            getHabitList()
//            getDiaryList(id)
//            habitRepository.getHabitById(id)
//            check()
        }
    }

}
