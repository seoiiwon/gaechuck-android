package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import kotlinx.coroutines.launch

class NoticeCouncilDetailActivity : AppCompatActivity() {

    private val viewModel: NoticeCouncilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council_detail)

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // 공지 ID 받기
        val noticeId = intent.getIntExtra("notice_id", -1)
        if (noticeId != -1) {
            fetchNoticeDetail(noticeId)
        }
    }

    private fun fetchNoticeDetail(noticeId: Int) {
        lifecycleScope.launch {
            try {
                Log.d("NoticeDetail", "API 요청 시작: noticeId = $noticeId")
                val noticeDetail = viewModel.getNoticeDetail(noticeId)

                if (noticeDetail == null) {
                    Log.e("NoticeDetail", "API 응답이 null입니다. noticeId: $noticeId")
                    return@launch
                }

                Log.d("NoticeDetail", "API 응답: ${noticeDetail.title}, ${noticeDetail.body}, ${noticeDetail.time}")

                runOnUiThread {
                    findViewById<TextView>(R.id.noticeTitle).text = noticeDetail.title
                    findViewById<TextView>(R.id.noticeBody).text = noticeDetail.body
                    findViewById<TextView>(R.id.noticeDate).text = noticeDetail.time
                }
            } catch (e: Exception) {
                Log.e("NoticeDetail", "API 요청 실패: ${e.message}")
            }
        }
    }


}
