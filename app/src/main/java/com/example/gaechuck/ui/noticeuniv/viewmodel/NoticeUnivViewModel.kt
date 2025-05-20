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
    var currentPage = 0
    var hasMoreData = true
    var isLoading = false


    fun fetchNotices(
        page: Int = 0,
        bbsId: String?,
        title: String? = null,
        size: Int = 20
    ) {
        if (page == 0) {
            currentList.clear()
            hasMoreData = true
        }

        if (isLoading || !hasMoreData) return

        isLoading = true
        viewModelScope.launch {
            try {
                Log.d("VIEWMODEL", "Fetching page: $page for bbsId: $bbsId")

                val (newNotices, hasNextPage) = repository.getNoticeUnivList(
                    page = page,
                    bbsId = bbsId,
                    title = title,
                    size = size
                )

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


    class Factory(private val repository: NoticeUnivRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoticeUnivViewModel::class.java)) {
                return NoticeUnivViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
