package com.example.gaechuck.ui.noticeuniv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
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
import com.example.gaechuck.repository.NoticeUnivRepository
import com.example.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import com.example.gaechuck.ui.noticeuniv.viewmodel.NoticeUnivViewModel
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Queue


class NoticeUnivActivity : AppCompatActivity() {
    private lateinit var noticeUnivAdapter: NoticeUnivAdapter
    private lateinit var viewModel: NoticeUnivViewModel
    private lateinit var dateTextView: TextView
    private var currentBbsId: String = "기관"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_univ)

        // viewModel 초기화
        val repository = NoticeUnivRepository()
        viewModel = ViewModelProvider(this, NoticeUnivViewModel.Factory(repository)).get(NoticeUnivViewModel::class.java)

        // UI 요소 초기화
        dateTextView = findViewById(R.id.noticeDateTextView)

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        initRecyclerView()
        observeViewModel()
        initSearch()

        // 데이터 로드
        Log.d("Activity", "Fetching notices onCreate")
        viewModel.fetchNotices(0, currentBbsId)

        val tabAll = findViewById<TextView>(R.id.tabInstitution)
        val tabAllUnderline = findViewById<View>(R.id.tabInstitutionUnderline)
        selectTab(tabAll, tabAllUnderline)

        setupTabs()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        noticeUnivAdapter = NoticeUnivAdapter(mutableListOf()) { url ->
            openUrl(url)
        }
        recyclerView.adapter = noticeUnivAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dateTextView = findViewById<TextView>(R.id.noticeDateTextView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLoading && viewModel.hasMoreData && lastVisibleItem + 1 >= totalItemCount) {
                    viewModel.loadMoreNotices(currentBbsId)
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
            Log.d("Activity", "Notices received: ${notices.size} items")
            noticeUnivAdapter.setNotices(notices)
        }

        viewModel.errorMessage.observe(this) { error ->
            Log.e("Activity", "Error occurred: $error")
            Toast.makeText(this, "오류 발생: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabs() {
        val tabs = listOf(
//            findViewById<TextView>(R.id.tabAll),
            findViewById<TextView>(R.id.tabInstitution),
            findViewById<TextView>(R.id.tabAcademic),
            findViewById<TextView>(R.id.tabScholarship),
            findViewById<TextView>(R.id.tabRecruitment),
            findViewById<TextView>(R.id.tabLegislative)
        )

        val underlines = listOf(
//            findViewById<View>(R.id.tabAllUnderline),
            findViewById<View>(R.id.tabInstitutionUnderline),
            findViewById<View>(R.id.tabAcademicUnderline),
            findViewById<View>(R.id.tabScholarshipUnderline),
            findViewById<View>(R.id.tabRecruitmentUnderline),
            findViewById<View>(R.id.tabLegislativeUnderline)
        )

        tabs.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectTab(textView, underlines[index])
                currentBbsId = textView.text.toString()

                val searchEditText = findViewById<EditText>(R.id.searchEditText)
                searchEditText.text.clear()

                noticeUnivAdapter.filter("")
                viewModel.fetchNotices(0, currentBbsId)


                val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
                recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun selectTab(selectedTab: TextView, selectedUnderline: View) {
        val allTabs = listOf(
//            findViewById<TextView>(R.id.tabAll),
            findViewById<TextView>(R.id.tabInstitution),
            findViewById<TextView>(R.id.tabAcademic),
            findViewById<TextView>(R.id.tabScholarship),
            findViewById<TextView>(R.id.tabRecruitment),
            findViewById<TextView>(R.id.tabLegislative)
        )

        val allUnderlines = listOf(
//            findViewById<View>(R.id.tabAllUnderline),
            findViewById<View>(R.id.tabInstitutionUnderline),
            findViewById<View>(R.id.tabAcademicUnderline),
            findViewById<View>(R.id.tabScholarshipUnderline),
            findViewById<View>(R.id.tabRecruitmentUnderline),
            findViewById<View>(R.id.tabLegislativeUnderline)
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

    private fun initSearch() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<ImageView>(R.id.searchButton)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            Log.d("Search", "Search button clicked, query: $query")
            performSearch(query)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                Log.d("Search", "IME_ACTION_SEARCH triggered, query: $query")
                performSearch(query)
                true
            } else {
                false
            }
        }

        searchEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchEditText.text.toString().trim()
                Log.d("Search", "Hardware ENTER key pressed, query: $query") // 로그 추가
                performSearch(query)
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        Log.d("Search", "Performing search for query: $query")
        noticeUnivAdapter.filter(query)

        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        recyclerView.scrollToPosition(0)
    }
}
