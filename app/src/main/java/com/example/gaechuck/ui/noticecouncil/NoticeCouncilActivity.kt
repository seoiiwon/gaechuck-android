package com.example.gaechuck.ui.noticecouncil

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.repository.NoticeCouncilRepository
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import com.example.gaechuck.ui.noticeuniv.NoticeSearchActivity
import com.example.gaechuck.ui.util.DeleteDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class NoticeCouncilActivity : AppCompatActivity() {

    private lateinit var noticeAdapter: NoticeCouncilAdapter
    private val viewModel: NoticeCouncilViewModel by viewModels { NoticeCouncilViewModelFactory(NoticeCouncilRepository()) }

    // NoticeCouncilActivity 내에 추가
    val updateNoticeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fetchNotices()
        }
    }

    private val writeNoticeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // 공지 작성 시 리스트 갱신
            viewModel.fetchNotices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council)

        val postNoticeButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.postNoticeButton)
        val searchButton = findViewById<ImageView>(R.id.searchButton)

        updateUI()

        // 공지 등록 버튼
        postNoticeButton.setOnClickListener {
            val intent = Intent(this, NoticeCouncilWriteActivity::class.java)
            writeNoticeLauncher.launch(intent)
        }

        // 검색창 띄우기
        searchButton.setOnClickListener {
            val intent = Intent(this, NoticeSearchActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 / 홈 버튼
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.homeBtn).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        initRecyclerView()
        observeViewModel()
        viewModel.fetchNotices()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus

        if (view != null) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        return super.dispatchTouchEvent(event)
    }


    private fun updateUI() {
        val token = AuthManager.getToken()
        val postNoticeButton = findViewById<FloatingActionButton>(R.id.postNoticeButton)

        // 토큰이 없으면 버튼 숨김
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
            onDeleteClick = { noticeId -> performDeleteNotice(noticeId) },
            onUpdateClick = { noticeId ->
                val intent = Intent(this, NoticeCouncilUpdateActivity::class.java)
                intent.putExtra("notice_id", noticeId)
                startActivity(intent)
            }
        )

        recyclerView.adapter = noticeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.noticeList.observe(this) { notices ->
            Log.d("checking", "notice count : ${notices.size}")
            noticeAdapter.updateData(notices)
            updateUI()
        }

        viewModel.deleteStatus.observe(this) { deletedNoticeId ->
            deletedNoticeId?.let {
                noticeAdapter.removeNotice(it)
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                updateUI()
            }
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            Toast.makeText(this, "삭제 실패: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onResume() {
        super.onResume()
    }

    // 공지 삭제
    private fun performDeleteNotice(noticeId: Int) {
        val deleteDialog = DeleteDialogFragment(this) {
            viewModel.deleteNotice(noticeId) // 삭제 로직 실행
        }
        deleteDialog.show()
    }
}