package com.example.gaechuck.ui.noticeuniv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeUnivModel
import com.example.gaechuck.network.RetrofitInstance
import com.example.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoticeUnivActivity : AppCompatActivity() {
    private lateinit var noticeUnivAdapter: NoticeUnivAdapter
    private val allNoticeUnivModels = mutableListOf<NoticeUnivModel>()
    private var currentPage = 0
    private val itemsPerPage = 10
    private lateinit var dateTextView: TextView
    var currentBbsId: String = "전체"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_univ)

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


        // 날짜 TextView 초기화
        dateTextView = findViewById(R.id.noticeDateTextView)

        // RecyclerView 초기화
        initRecyclerView()

        // 페이지 로드
        loadNotices()

        // 탭 클릭 리스너 설정
        val tabs = listOf(
            findViewById<TextView>(R.id.tabAll),
            findViewById<TextView>(R.id.tabInstitution),
            findViewById<TextView>(R.id.tabAcademic),
            findViewById<TextView>(R.id.tabScholarship),
            findViewById<TextView>(R.id.tabRecruitment),
            findViewById<TextView>(R.id.tabLegislative)
        )

        val underlines = listOf(
            findViewById<View>(R.id.tabAllUnderline),
            findViewById<View>(R.id.tabInstitutionUnderline),
            findViewById<View>(R.id.tabAcademicUnderline),
            findViewById<View>(R.id.tabScholarshipUnderline),
            findViewById<View>(R.id.tabRecruitmentUnderline),
            findViewById<View>(R.id.tabLegislativeUnderline)
        )

        // TabClickListener 생성 및 초기화
        val tabClickListener = TabClickListener(this, tabs, underlines)
        tabClickListener.setTabClickListeners()

        // 초기 상태 설정: '전체' 탭의 하단바 표시
        selectTab(findViewById(R.id.tabAll), findViewById(R.id.tabAllUnderline))
    }

    // 리싸이클러 초기화 함수
    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        noticeUnivAdapter = NoticeUnivAdapter(mutableListOf())
        recyclerView.adapter = noticeUnivAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 스크롤 리스너
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (dy > 0 && layoutManager.findLastVisibleItemPosition() + 1 == layoutManager.itemCount) {
                    loadNotices()
                }
            }
        })
    }

    // 리싸이클러에서 날짜 업데이트
    private fun updateDateFromItems(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
            val firstItem = allNoticeUnivModels[firstVisibleItemPosition]
            val itemDate = firstItem.time // Assuming `time` contains the date
            dateTextView.text = formatDate(itemDate)
        }
    }

    // loadNotices를 public으로 변경
    fun loadNotices() {
        // Retrofit을 통해 공지사항 데이터를 로드 (예시로 가정)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getNotices(
                    page = currentPage,
                    size = itemsPerPage,
                    bbsId = currentBbsId,  // 현재 선택된 BbsId로 필터링
                    title = ""
                )

                if (response.isSuccessful) {
                    val notices = response.body()?.result ?: emptyList()
                    withContext(Dispatchers.Main) {
                        allNoticeUnivModels.clear() // 새로 데이터를 로드하기 전에 기존 데이터 초기화
                        allNoticeUnivModels.addAll(notices)
                        noticeUnivAdapter.notifyDataSetChanged()

                        // 데이터 로드 후 첫 번째 날짜를 설정
                        if (notices.isNotEmpty()) {
                            val firstItemDate = notices[0].time // Assuming `time` is the date field
                            dateTextView.text = formatDate(firstItemDate)
                        } else {
                            // 항목이 없을 경우 오늘 날짜를 표시
                            dateTextView.text = formatDate(getCurrentDate())
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 날짜 포맷을 지정하여 현재 날짜를 반환
    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("MM월 dd일", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    // 날짜를 화면에 표시할 포맷
    private fun formatDate(dateString: String): String {
        return dateString
    }

    // selectTab을 public으로 변경
    fun selectTab(selectedTab: TextView, selectedUnderline: View) {
        // 모든 탭의 하단바를 숨기고, 선택된 탭의 하단바만 보이도록 설정
        val allTabs = listOf(
            findViewById<TextView>(R.id.tabAll),
            findViewById<TextView>(R.id.tabInstitution),
            findViewById<TextView>(R.id.tabAcademic),
            findViewById<TextView>(R.id.tabScholarship),
            findViewById<TextView>(R.id.tabRecruitment),
            findViewById<TextView>(R.id.tabLegislative)
        )

        val allUnderlines = listOf(
            findViewById<View>(R.id.tabAllUnderline),
            findViewById<View>(R.id.tabInstitutionUnderline),
            findViewById<View>(R.id.tabAcademicUnderline),
            findViewById<View>(R.id.tabScholarshipUnderline),
            findViewById<View>(R.id.tabRecruitmentUnderline),
            findViewById<View>(R.id.tabLegislativeUnderline)
        )

        // 모든 탭의 하단바를 숨기기
        allUnderlines.forEach { it.visibility = View.INVISIBLE }

        // 선택된 탭의 하단바 보이기
        selectedUnderline.visibility = View.VISIBLE

        // 모든 탭의 텍스트 스타일을 원래대로 돌리고, 선택된 탭만 스타일 변경
        allTabs.forEach { it.setTextColor(resources.getColor(R.color.tab_colors)) }
        selectedTab.setTextColor(resources.getColor(R.color.gnu_blue)) // 선택된 탭의 색 변경
    }

    // TabClickListener 클래스 내부에 위치
    class TabClickListener(
        private val activity: NoticeUnivActivity,
        private val tabs: List<TextView>,
        private val underlines: List<View>
    ) {
        // 탭 클릭 리스너 초기화
        fun setTabClickListeners() {
            tabs[0].setOnClickListener { onTabClick(tabs[0], underlines[0], "전체") }
            tabs[1].setOnClickListener { onTabClick(tabs[1], underlines[1], "기관") }
            tabs[2].setOnClickListener { onTabClick(tabs[2], underlines[2], "학사") }
            tabs[3].setOnClickListener { onTabClick(tabs[3], underlines[3], "장학") }
            tabs[4].setOnClickListener { onTabClick(tabs[4], underlines[4], "채용") }
            tabs[5].setOnClickListener { onTabClick(tabs[5], underlines[5], "입법예고") }
        }

        // 탭 클릭 시 하단바 업데이트 및 BbsId 설정
        private fun onTabClick(selectedTab: TextView, selectedUnderline: View, bbsId: String) {
            // 하단바 숨기기
            hideUnderlines()

            // 선택된 하단바 보이기
            selectedUnderline.visibility = View.VISIBLE

            // 모든 탭 텍스트 색상 리셋
            resetTabTextColor()

            // 선택된 탭 색상 변경
            selectedTab.setTextColor(activity.resources.getColor(R.color.gnu_blue))

            // currentBbsId 값 설정
            activity.currentBbsId = bbsId
            activity.loadNotices()  // 데이터 다시 로드
        }

        // 모든 하단바 숨기기
        private fun hideUnderlines() {
            underlines.forEach { it.visibility = View.INVISIBLE }
        }

        // 모든 탭의 텍스트 색상 리셋
        private fun resetTabTextColor() {
            tabs.forEach { it.setTextColor(activity.resources.getColor(R.color.tab_colors)) }
        }
    }
}
