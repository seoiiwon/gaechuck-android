package com.example.gaechuck.ui.noticeuniv

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.data.model.NoticeUnivModel
import com.example.gaechuck.repository.NoticeUnivRepository
import com.example.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import com.example.gaechuck.ui.noticeuniv.viewmodel.NoticeUnivViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NoticeUnivActivity : AppCompatActivity() {
    private lateinit var noticeUnivAdapter: NoticeUnivAdapter
    private lateinit var viewModel: NoticeUnivViewModel
    private lateinit var dateTextView: TextView
    private var currentBbsId: String = "전체"
    private var currentTitle: String? = null
    private lateinit var searchButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_univ)
        searchButton = findViewById(R.id.searchButton)

        // viewModel 초기화
        val repository = NoticeUnivRepository()
        viewModel = ViewModelProvider(this, NoticeUnivViewModel.Factory(repository)).get(NoticeUnivViewModel::class.java)

        // UI 요소 초기화
        dateTextView = findViewById(R.id.noticeDateTextView)

        // back, home 버튼 초기화
        val backBtn: ImageView = findViewById(R.id.backBtn)
        val homeBtn: ImageView = findViewById(R.id.homeBtn)

        backBtn.setOnClickListener { finish() }

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // 검색창 UI 설정
        searchButton.setOnClickListener {
            val intent = Intent(this, NoticeSearchActivity::class.java).apply {
                putExtra("bbsId", currentBbsId)
            }
            startActivity(intent)
        }

        initRecyclerView()
        observeViewModel()

        // 데이터 로드
        Log.d("Activity", "Fetching notices onCreate")
        currentBbsId = ""
        viewModel.fetchNotices(
            page = 0,
            bbsId = currentBbsId,
            title = null,
            size = 20
        )

        selectTab(
            findViewById(R.id.tabInstitution),
            findViewById(R.id.tabInstitutionUnderline)
        )
        setupTabs()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus
        if (view is EditText) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                // EditText 외부를 클릭하면 키보드 숨기기
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        noticeUnivAdapter = NoticeUnivAdapter(mutableListOf()) { url ->
            openUrl(url)
        }
        recyclerView.adapter = noticeUnivAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLoading && viewModel.hasMoreData && lastVisibleItem + 1 >= totalItemCount) {
                    viewModel.fetchNotices(
                        page = viewModel.currentPage + 1,
                        bbsId = currentBbsId,
                        title = currentTitle,
                        size = 20
                    )
                }

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                    val firstVisibleItem = noticeUnivAdapter.getItem(firstVisibleItemPosition)
                    firstVisibleItem?.let {
                        dateTextView.text = formatDate(it.regiDate)
                    }
                }

            }
        })
    }

    private fun observeViewModel() {
        viewModel.notices.observe(this) { notices ->
            noticeUnivAdapter.setNotices(notices)
        }

        viewModel.errorMessage.observe(this) { error ->
            Log.e("Activity", "Error occurred: $error")
            Toast.makeText(this, "오류 발생: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabs() {
        val tabs = listOf(
            findViewById<TextView>(R.id.tabAll),
            findViewById<TextView>(R.id.tabInstitution),
            findViewById<TextView>(R.id.tabAcademic),
            findViewById<TextView>(R.id.tabScholarship),
            findViewById<TextView>(R.id.tabRecruitment)
        )
        val underlines = listOf(
            findViewById<View>(R.id.tabAllUnderline),
            findViewById<View>(R.id.tabInstitutionUnderline),
            findViewById<View>(R.id.tabAcademicUnderline),
            findViewById<View>(R.id.tabScholarshipUnderline),
            findViewById<View>(R.id.tabRecruitmentUnderline)
        )
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)

        tabs.forEachIndexed { i, tab ->
            val underline = underlines[i]
            tab.setOnClickListener {

                selectTab(tab, underline)

                currentBbsId = if (tab.id == R.id.tabAll) "" else tab.text.toString()
                currentTitle = null
                viewModel.hasMoreData = true
                viewModel.currentPage  = 0

                viewModel.fetchNotices(
                    page  = 0,
                    bbsId = currentBbsId,
                    title = currentTitle,
                    size  = 20
                )

                recyclerView.scrollToPosition(0)
            }
        }

        selectTab(tabs[0], underlines[0])
    }

    private fun selectTab(selectedTab: TextView, selectedUnderline: View) {
        val allTabs = listOf(
            findViewById<TextView>(R.id.tabAll),
            findViewById<TextView>(R.id.tabInstitution),
            findViewById<TextView>(R.id.tabAcademic),
            findViewById<TextView>(R.id.tabScholarship),
            findViewById<TextView>(R.id.tabRecruitment)
        )

        val allUnderlines = listOf(
            findViewById<View>(R.id.tabAllUnderline),
            findViewById<View>(R.id.tabInstitutionUnderline),
            findViewById<View>(R.id.tabAcademicUnderline),
            findViewById<View>(R.id.tabScholarshipUnderline),
            findViewById<View>(R.id.tabRecruitmentUnderline)
        )

        allUnderlines.forEach { it.visibility = View.INVISIBLE }
        selectedUnderline.visibility = View.VISIBLE

        allTabs.forEach { it.setTextColor(resources.getColor(R.color.tab_colors)) }
        selectedTab.setTextColor(resources.getColor(R.color.gnu_blue))
    }


    private fun formatDate(regiDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM월 dd일", Locale.getDefault())
            val date = inputFormat.parse(regiDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "날짜 오류"
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
