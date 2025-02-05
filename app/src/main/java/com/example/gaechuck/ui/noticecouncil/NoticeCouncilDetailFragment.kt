package com.example.gaechuck.ui.noticecouncil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gaechuck.data.model.NoticeCouncilModel
import com.example.gaechuck.databinding.FragmentNoticeCouncilDetailBinding

class NoticeCouncilDetailFragment : Fragment() {

    private lateinit var notice: NoticeCouncilModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트 레이아웃을 인플레이트
        val binding = FragmentNoticeCouncilDetailBinding.inflate(inflater, container, false)

        // 번들에서 데이터 가져오기
        arguments?.let {
            notice = it.getParcelable("notice_details") ?: return@let // 수정된 부분: Parcelable로 받기
        }



        // 데이터 바인딩 (예: 텍스트 뷰에 공지사항 제목과 내용을 표시)
        binding.noticeTitle.text = notice.title
        binding.noticeDescription.text = notice.body
        binding.noticeDate.text = notice.date

        return binding.root  // 뷰 반환
    }
}
