package com.gaechuck_package.gaechuck.ui.menu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaechuck_package.gaechuck.data.response.FoodMenuItem
import com.gaechuck_package.gaechuck.repository.CafeteriaMenuRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CafeteriaMenuViewModel(
    private val repository: CafeteriaMenuRepository
) : ViewModel() {

//    일주일 메뉴
    private val _menuList = MutableLiveData<List<FoodMenuItem>>()
    val menuList: LiveData<List<FoodMenuItem>> get() = _menuList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

//    월요일 구하기
    private fun getThisWeekMonday(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()

        today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return format.format(today.time)
    }

//    메뉴 가져오기
    fun loadMenu(seq: Int) {
        viewModelScope.launch {
            try {
                val items = repository.fetchWeeklyMenu(seq)
                _menuList.value = items
            } catch (e: Exception) {
                _menuList.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }

//    특정 날짜 메뉴 필터링
    fun getMenuForDate(date: String): List<FoodMenuItem> {
        val all = _menuList.value ?: emptyList()
        return all.filter { it.date == date }
    }

//    특정 요일 날짜 리턴
    fun getDateByIndex(index: Int): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        calendar.add(Calendar.DAY_OF_MONTH, index)
        return format.format(calendar.time)
    }
}