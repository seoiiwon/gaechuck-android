package com.example.gaechuck.ui.noticecouncil.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.example.gaechuck.repository.NoticeCouncilRepository
import kotlinx.coroutines.launch

class NoticeCouncilViewModel : ViewModel() {
    private val repository = NoticeCouncilRepository()
    private val _noticeList = MutableLiveData<List<GetCouncilNoticeDataResponse>>()
    val noticeList: LiveData<List<GetCouncilNoticeDataResponse>> get() = _noticeList
    private var currentPage = 0
    private val itemsPerPage = 10
    private var allNotices: List<GetCouncilNoticeDataResponse> = emptyList()

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





//    // MutableLiveData는 내부에서 값을 변경할 수 있고, 외부에서는 LiveData로 읽기만 가능
//    private val _notice = MutableLiveData<NoticeCouncilModel>()
//    val notice: LiveData<NoticeCouncilModel> get() = _notice
//
//    // 데이터를 설정하는 함수
//    fun setNotice(noticeCouncilModel: NoticeCouncilModel) {
//        _notice.value = noticeCouncilModel
//    }

//    // 공지 리스트
//    private val _noticeList = MutableLiveData<List<LoseList>>()
//    val noticeList : LiveData<List<LoseList>>
//        get() = _noticeList
//    // 공지 개별 정보
//    private val _noticeDetailData = MutableLiveData<GetLoseDetailResponse>()
//    val noticeDetailData : MutableLiveData<GetLoseDetailResponse>
//        get() = _noticeDetailData
}
