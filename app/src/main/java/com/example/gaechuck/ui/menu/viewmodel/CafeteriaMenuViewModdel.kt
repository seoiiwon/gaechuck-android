package com.example.gaechuck.ui.menu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.repository.CafeteriaMenuRepository

class CafeteriaMenuViewModdel : ViewModel() {

    private val repository = CafeteriaMenuRepository()

    private val _menuList = MutableLiveData<List<FoodMenuItem>>()
    val menuList: LiveData<List<FoodMenuItem>> get() = _menuList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchFoodMenu(cafeteriaSeq: Int) {
        repository.getFoodMenu(
            cafeteriaSeq,
            onSuccess = { menuData ->
                _menuList.postValue(menuData)
            },
            onError = { error ->
                _errorMessage.postValue(error)
            }
        )
    }

    fun fetchFoodMenuByDate(cafeteriaSeq: Int, startDate: String) {
        repository.getFoodMenuByDate(
            cafeteriaSeq,
            startDate,
            onSuccess = { menuData ->
                _menuList.postValue(menuData)
            },
            onError = { error ->
                _errorMessage.postValue(error)
            }
        )
    }
}