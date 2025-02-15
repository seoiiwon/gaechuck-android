package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import kotlinx.coroutines.launch

class NoticeCouncilActivity : AppCompatActivity() {

    private lateinit var noticeAdapter: NoticeCouncilAdapter
    private val viewModel: NoticeCouncilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council)

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        initRecyclerView()
        viewModel.fetchNotices()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        noticeAdapter = NoticeCouncilAdapter(mutableListOf())
        recyclerView.adapter = noticeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.noticeList.observe(this) { notices ->
            noticeAdapter.addNotices(notices)
        }

        noticeAdapter.setOnItemClickListener(object : NoticeCouncilAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val notice = viewModel.noticeList.value?.get(position)
                if (notice != null) {
                    lifecycleScope.launch {
                        val detail = viewModel.getNoticeDetail(notice.id)
                        if (detail != null) {
                            val intent = Intent(this@NoticeCouncilActivity, NoticeCouncilDetailActivity::class.java)
                            intent.putExtra("notice_detail", detail)
                            startActivity(intent)
                        }
                    }
                }
            }
        })





    }
}