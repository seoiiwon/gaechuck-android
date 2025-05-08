package com.example.gaechuck.ui.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.example.gaechuck.databinding.AlertPopupBinding

class ImageFragment(private val context : Context) {
    private lateinit var binding: AlertPopupBinding    // 뷰바인딩
    private var dlg: Dialog? = null // 다이얼로그 객체를 한번만 생성하고 재사용

    fun show() {
        // 다이얼로그가 이미 생성되었으면 새로 생성하지 않음
        if (dlg == null) {
            dlg = Dialog(context)
            binding = AlertPopupBinding.inflate(LayoutInflater.from(context))

            // 제목 없는 다이얼로그 설정
            dlg?.requestWindowFeature(Window.FEATURE_NO_TITLE) // 이 부분은 setContentView 전에 호출해야 함
            dlg?.setContentView(binding.root) // 해당 뷰 바인딩을 적용
            dlg?.setCancelable(true)  // 취소 허용
            dlg?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 없애기

            dlg?.setCanceledOnTouchOutside(true)  // 밖에 클릭해도 dialog 사라짐

            // 텍스트 적용
            binding.dialogTitleTv.text = "이미지를 등록해주세요."
            binding.dialogContentTv.visibility = View.VISIBLE
            binding.dialogContentTv.text = "이미지 없이는 게시물 등록이 불가합니다."
            binding.dialogYesBtn.text = "확인"

            binding.dialogYesBtn.setOnClickListener {
                dlg?.dismiss() // 다이얼로그 닫기
            }
        }

        dlg?.show() // 이미 존재하는 dlg를 사용해서 show()
    }

    // 다이얼로그를 닫는 메서드 추가
    fun dismiss() {
        dlg?.dismiss()
        dlg = null // 다이얼로그를 닫을 때 객체를 null로 설정
    }
}