package com.example.gaechuck.ui.noticeuniv.viewmodel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeUnivModel
import com.example.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import com.example.gaechuck.R
import com.google.gson.Gson

class NoticeUniv : AppCompatActivity() {

    private lateinit var tabs: List<Pair<TextView, View>>
    private lateinit var noticeUnivAdapter: NoticeUnivAdapter
    private val allNoticeUnivModels = mutableListOf<NoticeUnivModel>()
    private var currentPage = 0
    private val itemsPerPage = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub1)

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

        // 각 탭 초기화 및 설정
        initTabs()

        // RecyclerView 초기화 및 설정
        initRecyclerView()

        // JSON 파일 로드
        loadNotices()

        // 초기 데이터 로드
        loadNextPage()
    }

    private fun initTabs() {
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabAllUnderline = findViewById<View>(R.id.tabAllUnderline)
        val tabInstitution = findViewById<TextView>(R.id.tabInstitution)
        val tabInstitutionUnderline = findViewById<View>(R.id.tabInstitutionUnderline)
        val tabAcademic = findViewById<TextView>(R.id.tabAcademic)
        val tabAcademicUnderline = findViewById<View>(R.id.tabAcademicUnderline)
        val tabScholarship = findViewById<TextView>(R.id.tabScholarship)
        val tabScholarshipUnderline = findViewById<View>(R.id.tabScholarshipUnderline)
        val tabRecruitment = findViewById<TextView>(R.id.tabRecruitment)
        val tabRecruitmentUnderline = findViewById<View>(R.id.tabRecruitmentUnderline)

        tabs = listOf(
            Pair(tabAll, tabAllUnderline),
            Pair(tabInstitution, tabInstitutionUnderline),
            Pair(tabAcademic, tabAcademicUnderline),
            Pair(tabScholarship, tabScholarshipUnderline),
            Pair(tabRecruitment, tabRecruitmentUnderline)
        )

        // 탭 클릭 리스너 설정
        for ((tab, underline) in tabs) {
            tab.setOnClickListener {
                selectTab(tab, underline)
            }
        }

        // 기본 탭 선택
        selectTab(tabAll, tabAllUnderline)
    }

    private fun selectTab(selectedTab: TextView, selectedUnderline: View) {
        for ((tab, underline) in tabs) {
            tab.isSelected = (tab == selectedTab)
            underline.visibility = if (tab == selectedTab) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        noticeUnivAdapter = NoticeUnivAdapter(mutableListOf()) // 빈 데이터로 어댑터 초기화
        recyclerView.adapter = noticeUnivAdapter // 어댑터 연결
        recyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 설정

        // 스크롤 이벤트 설정
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (dy > 0 && layoutManager.findLastVisibleItemPosition() + 1 == layoutManager.itemCount) {
                    loadNextPage()
                }
            }
        })
    }

    private fun loadNotices() {
        try {
            val inputStream = assets.open("notice_example.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val noticeUnivModels: List<NoticeUnivModel> = gson.fromJson(json, Array<NoticeUnivModel>::class.java).toList()
            allNoticeUnivModels.addAll(noticeUnivModels)
            noticeUnivAdapter.addNotices(noticeUnivModels) // 어댑터에 데이터 추가
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun loadNextPage() {
        val start = currentPage * itemsPerPage
        val end = minOf(start + itemsPerPage, allNoticeUnivModels.size)
        if (start < end) {
            noticeUnivAdapter.addNotices(allNoticeUnivModels.subList(start, end))
            currentPage++
        }
    }
}
