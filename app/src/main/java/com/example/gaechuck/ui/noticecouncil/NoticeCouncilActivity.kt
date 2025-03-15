package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class NoticeCouncilActivity : AppCompatActivity() {

    private lateinit var noticeAdapter: NoticeCouncilAdapter
    private val viewModel: NoticeCouncilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council)

        val postNoticeButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.postNoticeButton)

        updateUI()

        postNoticeButton.setOnClickListener {
            val intent = Intent(this, NoticeCouncilWriteActivity::class.java)
            startActivity(intent)
        }

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
        initSearch()
    }


    private fun updateUI() {
        val token = AuthManager.getToken()
        val postNoticeButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.postNoticeButton)

        // ✅ 토큰이 없으면 버튼 숨김
        if (token.isNullOrEmpty()) {
            postNoticeButton.visibility = View.GONE
        } else {
            postNoticeButton.visibility = View.VISIBLE
        }
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)

        noticeAdapter = NoticeCouncilAdapter(
            mutableListOf(),
            onDeleteClick = { noticeId -> deleteNotice(noticeId) }
        )

        recyclerView.adapter = noticeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.noticeList.observe(this) { notices ->
            noticeAdapter.addNotices(notices)
            updateUI()
        }

        noticeAdapter.setOnItemClickListener(object : NoticeCouncilAdapter.OnItemClickListener {
            override fun onItemClick(noticeId: Int) {
                lifecycleScope.launch {
                    val detail = viewModel.getNoticeDetail(noticeId)
                    if (detail != null) {
                        val intent = Intent(this@NoticeCouncilActivity, NoticeCouncilDetailActivity::class.java)
                        intent.putExtra("notice_id", noticeId)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun deleteNotice(noticeId: Int) {
        lifecycleScope.launch {
            val token = AuthManager.getToken()
            if (token.isNullOrEmpty()) {
                Toast.makeText(this@NoticeCouncilActivity, "인증 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConnection.getRetrofitService.deleteNoticeCouncil(noticeId, "Bearer $token")
                }

                val responseBody = response.errorBody()?.string() ?: "응답 없음"

                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        noticeAdapter.removeNotice(noticeId)
                        Toast.makeText(this@NoticeCouncilActivity, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        updateUI()
                    }
                } else {
                    Log.e("DeleteNotice", "삭제 요청 실패: ${response.code()} - ${response.message()} \n 응답 본문: $responseBody")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@NoticeCouncilActivity, "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("DeleteNotice", "예외 발생", e)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@NoticeCouncilActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initSearch() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<ImageView>(R.id.searchButton)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            Log.d("Search", "Search button clicked, query: $query") // 로그 추가
            performSearch(query)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                Log.d("Search", "IME_ACTION_SEARCH triggered, query: $query") // 로그 추가
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
        noticeAdapter.filter(query)

        val recyclerView = findViewById<RecyclerView>(R.id.noticeRecyclerView)
        recyclerView.scrollToPosition(0)
    }
}