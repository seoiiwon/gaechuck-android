package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.repository.NoticeCouncilRepository
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class NoticeCouncilDetailActivity : AppCompatActivity() {

    private val viewModel: NoticeCouncilViewModel by viewModels {
        NoticeCouncilViewModelFactory(NoticeCouncilRepository())
    }

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

                runOnUiThread {
                    findViewById<TextView>(R.id.noticeTitle).text = noticeDetail.title
                    findViewById<TextView>(R.id.noticeBody).text = noticeDetail.body
                    findViewById<TextView>(R.id.noticeDate).text = formatNoticeDate(noticeDetail.time)
                    val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)
                    imageContainer?.let { container ->
                        container.removeAllViews()
                        noticeDetail.images?.forEach { imageUrl ->
                            val imageView = ImageView(this@NoticeCouncilDetailActivity).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(0, 16, 0, 16)
                                }
                                adjustViewBounds = true
                                scaleType = ImageView.ScaleType.FIT_CENTER
                                setBackgroundResource(R.drawable.rounded_image_bg)
                                clipToOutline = true
                            }

                            Glide.with(this@NoticeCouncilDetailActivity)
                                .load(imageUrl)
                                .into(imageView)

                            container.addView(imageView)
                        }
                    } ?: Log.e("NoticeDetail", "imageContainer is null")
                }
            } catch (e: Exception) {
                Log.e("NoticeDetail", "API 요청 실패: ${e.message}")
            }
        }
    }

    private fun formatNoticeDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date)
        } catch (e: Exception) {
            inputDate
        }
    }
}
