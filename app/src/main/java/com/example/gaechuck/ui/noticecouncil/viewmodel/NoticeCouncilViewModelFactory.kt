package com.example.gaechuck.ui.noticecouncil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gaechuck.repository.NoticeCouncilRepository

class NoticeCouncilViewModelFactory(private val repository: NoticeCouncilRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeCouncilViewModel::class.java)) {
            return NoticeCouncilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}