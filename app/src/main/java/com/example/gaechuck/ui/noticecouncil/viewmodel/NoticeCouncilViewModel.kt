package com.example.gaechuck.ui.noticecouncil.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.example.gaechuck.repository.NoticeCouncilRepository
import kotlinx.coroutines.launch

class NoticeCouncilViewModel(private val repository: NoticeCouncilRepository) : ViewModel() {
    private var currentPage = 0
    private val itemsPerPage = 10
    private var allNotices: List<GetCouncilNoticeDataResponse> = emptyList()

    private val _noticeList = MutableLiveData<List<GetCouncilNoticeDataResponse>>()
    val noticeList: LiveData<List<GetCouncilNoticeDataResponse>> get() = _noticeList

    private val _deleteStatus = MutableLiveData<Int?>()
    val deleteStatus: LiveData<Int?> get() = _deleteStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _searchResults = MutableLiveData<List<GetCouncilNoticeDataResponse>>()
    val searchResults: LiveData<List<GetCouncilNoticeDataResponse>> = _searchResults

    fun fetchNotices() {
        viewModelScope.launch {
            try {
                currentPage = 0
                allNotices = repository.getNoticeCouncilList() ?: emptyList()
                Log.d("ViewModel check", "fetch : ${allNotices.size}")
                _noticeList.value = emptyList()
                loadMoreNotices()
            } catch (e: Exception) {
                _noticeList.value = emptyList()
            }
        }
    }

    fun loadMoreNotices() {
        val nextPageItems = allNotices.drop(currentPage * itemsPerPage).take(itemsPerPage)
        Log.d("Paging", "currentPage=$currentPage, nextPageItems=${nextPageItems.size}")
        if (nextPageItems.isNotEmpty()) {
            _noticeList.value = _noticeList.value.orEmpty() + nextPageItems
            currentPage++
        } else {
            Log.d("Paging", "더 이상 불러올 공지 없음")
        }
    }

    suspend fun getNoticeDetail(noticeId: Int): GetCouncilNoticeDetailResponse? {
        return repository.getNoticeDetail(noticeId)
    }

    fun deleteNotice(noticeId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteNotice(noticeId)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _deleteStatus.postValue(noticeId)
                } else {
                    _errorMessage.postValue(response.body()?.message ?: "삭제 실패")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "알 수 없는 오류 발생")
            }
        }
    }

    val searchResult = MutableLiveData<List<GetCouncilNoticeDataResponse>>()

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val result = repository.searchNotices(query)
                searchResult.postValue(result)
            } catch (e: Exception) {
                Log.e("Search", "Error: ${e.message}")
            }
        }
    }

    class Factory(private val repo: NoticeCouncilRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NoticeCouncilViewModel(repo) as T
        }
    }
}
