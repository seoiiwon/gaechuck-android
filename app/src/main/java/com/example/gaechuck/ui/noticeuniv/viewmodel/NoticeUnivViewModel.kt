package com.example.gaechuck.ui.noticeuniv.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.model.NoticeUnivModel
import com.example.gaechuck.repository.NoticeUnivRepository
import kotlinx.coroutines.launch

class NoticeUnivViewModel(private val repository: NoticeUnivRepository) : ViewModel() {
    private val _notices = MutableLiveData<List<NoticeUnivModel>>()
    val notices: LiveData<List<NoticeUnivModel>> get() = _notices

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchNotices(page: Int, pageSize: Int, bbsId: String) {
        viewModelScope.launch {
            Log.d("ViewModel", "Fetching notices: page=$page, pageSize=$pageSize, bbsId=$bbsId")

            try {
                val noticeList = repository.getNoticeUnivList(page, pageSize, bbsId)
                _notices.postValue(noticeList)
                Log.d("ViewModel", "Data fetched successfully: ${noticeList.size} items")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error fetching notices: ${e.message}")
                _errorMessage.postValue("오류 발생: ${e.message}")
            }
        }
    }
}
