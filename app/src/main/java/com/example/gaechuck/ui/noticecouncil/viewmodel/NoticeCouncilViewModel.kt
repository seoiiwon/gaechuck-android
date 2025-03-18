package com.example.gaechuck.ui.noticecouncil.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.DeleteCouncilNoticeResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.example.gaechuck.repository.NoticeCouncilRepository
import kotlinx.coroutines.launch
import retrofit2.Response

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

    fun fetchNotices() {
        viewModelScope.launch {
            try {
                allNotices = repository.getNoticeCouncilList() ?: emptyList()
                loadMoreNotices()
            } catch (e: Exception) {
                _noticeList.value = emptyList()
            }
        }
    }

    fun loadMoreNotices() {
        val nextPageItems = allNotices.drop(currentPage * itemsPerPage).take(itemsPerPage)
        if (nextPageItems.isNotEmpty()) {
            _noticeList.value = _noticeList.value.orEmpty() + nextPageItems
            currentPage++
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
}
