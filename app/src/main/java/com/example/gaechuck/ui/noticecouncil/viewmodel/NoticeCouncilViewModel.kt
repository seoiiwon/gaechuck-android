package com.example.gaechuck.ui.noticecouncil.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gaechuck.data.model.NoticeCouncilModel

class NoticeCouncilViewModel : ViewModel() {

    // MutableLiveData는 내부에서 값을 변경할 수 있고, 외부에서는 LiveData로 읽기만 가능
    private val _notice = MutableLiveData<NoticeCouncilModel>()
    val notice: LiveData<NoticeCouncilModel> get() = _notice

    // 데이터를 설정하는 함수
    fun setNotice(noticeCouncilModel: NoticeCouncilModel) {
        _notice.value = noticeCouncilModel
    }
}
