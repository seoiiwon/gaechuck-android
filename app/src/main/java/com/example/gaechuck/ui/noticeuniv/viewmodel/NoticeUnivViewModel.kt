package com.example.gaechuck.ui.noticeuniv.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.model.NoticeUnivModel
import com.example.gaechuck.repository.NoticeUnivRepository
import kotlinx.coroutines.launch

class NoticeUnivViewModel(private val repository: NoticeUnivRepository) : ViewModel() {
    private val _notices = MutableLiveData<List<NoticeUnivModel>>()
    val notices: LiveData<List<NoticeUnivModel>> get() = _notices

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var currentList: MutableList<NoticeUnivModel> = mutableListOf()
    var isLoading = false
    var hasMoreData = true
    var currentPage = 0

    fun fetchNotices(page: Int = 0, bbsId: String) {
        if (isLoading || !hasMoreData) return // 불러올 데이터가 없는 경우 중단

        isLoading = true
        viewModelScope.launch {
            try {
                Log.d("VIEWMODEL", "Fetching page: $page for bbsId: $bbsId")

                val (newNotices, hasNextPage) = repository.getNoticeUnivList(page, bbsId)

                if (page == 0) { currentList.clear() }
                currentList.addAll(newNotices)

                _notices.value = currentList
                hasMoreData = hasNextPage
                currentPage = page

                Log.d("VIEWMODEL", "Total items fetched so far: ${currentList.size}")

            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMoreNotices(bbsId: String) {
        if (!isLoading && hasMoreData) {
            fetchNotices(currentPage + 1, bbsId)
        }
    }

    class Factory(private val repository: NoticeUnivRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoticeUnivViewModel::class.java)) {
                return NoticeUnivViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
