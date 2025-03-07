package com.example.gaechuck.ui.noticeuniv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gaechuck.repository.NoticeUnivRepository

class NoticeUnivViewModelFactory(private val repository: NoticeUnivRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeUnivViewModel::class.java)) {
            return NoticeUnivViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}