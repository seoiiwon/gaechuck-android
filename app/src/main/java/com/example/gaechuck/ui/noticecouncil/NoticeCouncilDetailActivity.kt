package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.databinding.ActivityNoticeCouncilDetailBinding
import com.example.gaechuck.repository.NoticeCouncilRepository
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import kotlinx.coroutines.launch


class NoticeCouncilDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeCouncilDetailBinding
    private val viewModel: NoticeCouncilViewModel by viewModels {
        NoticeCouncilViewModelFactory(NoticeCouncilRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeCouncilDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val noticeId = intent.getIntExtra("notice_id", -1)
        if (noticeId == -1) {
            finish()
            return
        }

        // 상세 공지 가져오기
        lifecycleScope.launch {
            val detail = viewModel.getNoticeDetail(noticeId)
            if (detail != null) {
                binding.noticeTitle.text = detail.title
                binding.noticeBody.text = detail.body
                binding.noticeDate.text = detail.time

                val imageList = detail.images
                if (!imageList.isNullOrEmpty()) {
                    binding.imageContainer.removeAllViews()
                    imageList.forEach { imageUrl ->
                        val imageView = ImageView(this@NoticeCouncilDetailActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                (resources.displayMetrics.widthPixels * 0.9).toInt(),
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

                        binding.imageContainer.addView(imageView)
                    }
                } else {
                    binding.imageContainer.visibility = View.GONE
                }
            }
        }

        binding.backBtn.setOnClickListener { finish() }
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }
}
