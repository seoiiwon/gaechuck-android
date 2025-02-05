package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeCouncilModel

class NoticeCouncilDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council_detail)

        // 뒤로가기
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        // 홈으로 이동하기
        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // Parcelable 객체 받기
        val notice = intent.getParcelableExtra<NoticeCouncilModel>("notice_details")

        // 데이터를 화면에 표시
        notice?.let {
            findViewById<TextView>(R.id.noticeTitle).text = it.title
            findViewById<TextView>(R.id.noticeBody).text = it.body
            findViewById<TextView>(R.id.noticeDate).text = it.date
        }

    }
}