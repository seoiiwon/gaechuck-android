package com.gaechuck_package.gaechuck.ui.menu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.repository.CafeteriaMenuRepository

class CafeteriaMenuViewModelFactory(
    private val repository: CafeteriaMenuRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CafeteriaMenuViewModel::class.java)) {
            return CafeteriaMenuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}