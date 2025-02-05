package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeCouncilModel
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoticeCouncilActivity : AppCompatActivity() {

    private lateinit var noticeAdapter: NoticeCouncilAdapter
    private val allNotices = mutableListOf<NoticeCouncilModel>()  // allNotices 초기화
    private var currentPage = 0
    private val itemsPerPage = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council)

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

        // RecyclerView 초기화
        initRecyclerView()

        // JSON 데이터 로드
        loadNotices()

        // 초기 데이터 로드
        loadNextPage()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        noticeAdapter = NoticeCouncilAdapter(mutableListOf())
        recyclerView.adapter = noticeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 스크롤 리스너 추가
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem + 1 == totalItemCount && totalItemCount % itemsPerPage == 0) {
                    loadNextPage()
                }
            }
        })



//        // RecyclerView 아이템 클릭 리스너
//        noticeAdapter.setOnItemClickListener(object : NoticeCouncilAdapter.OnItemClickListener {
//            override fun onItemClick(position: Int) {
//                val notice = allNotices[position]
//
//                // Intent에 Parcelable 객체 추가
//                val intent = Intent(this@NoticeCouncilActivity, NoticeCouncilActivity::class.java)
//                intent.putExtra("notice_details", notice)  // putParcelable 사용
//                startActivity(intent)
//            }
//        })

        // RecyclerView 아이템 클릭 리스너
        noticeAdapter.setOnItemClickListener(object : NoticeCouncilAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val notice = allNotices[position]

                // Intent에 Parcelable 객체 추가
                val intent = Intent(this@NoticeCouncilActivity, NoticeCouncilDetailActivity::class.java)
                intent.putExtra("notice_details", notice)  // putParcelable 사용

                // 플래그 추가 (기존 액티비티를 종료하고 새로 시작)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                startActivity(intent)
            }
        })

    }

    private fun loadNotices() {
        try {
            val inputStream = assets.open("notice_council.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<NoticeCouncilModel>>() {}.type
            val noticeCouncilModels: List<NoticeCouncilModel> = Gson().fromJson(json, type)
            allNotices.addAll(noticeCouncilModels)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadNextPage() {
        val start = currentPage * itemsPerPage
        val end = minOf(start + itemsPerPage, allNotices.size)
        if (start < end) {
            noticeAdapter.addNotices(allNotices.subList(start, end))
            currentPage++
        }
    }
}
