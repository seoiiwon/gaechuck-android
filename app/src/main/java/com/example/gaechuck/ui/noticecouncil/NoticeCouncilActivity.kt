package com.example.gaechuck.ui.noticecouncil

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.model.NoticeCouncilModel
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.repository.NoticeCouncilRepository
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import com.example.gaechuck.ui.util.DeleteDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NoticeCouncilActivity : AppCompatActivity() {

    private lateinit var noticeAdapter: NoticeCouncilAdapter

    val repository = NoticeCouncilRepository()
    val viewModelFactory = NoticeCouncilViewModel.Factory(repository)

    private val viewModel: NoticeCouncilViewModel by viewModels { NoticeCouncilViewModelFactory(NoticeCouncilRepository()) }

    val updateNoticeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.fetchNotices()
        }
    }

    private val writeNoticeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.fetchNotices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council)

        noticeAdapter = NoticeCouncilAdapter(
            mutableListOf(),
            onDeleteClick = { noticeId -> performDeleteNotice(noticeId) },
            onUpdateClick = { noticeId ->
                val intent = Intent(this, NoticeCouncilUpdateActivity::class.java)
                intent.putExtra("notice_id", noticeId)
                updateNoticeLauncher.launch(intent)
            },
            onItemClick = { notice ->
                showNoticeDetailActivity(notice)
            }
        )

        val postNoticeButton = findViewById<FloatingActionButton>(R.id.postNoticeButton)
        val searchButton = findViewById<ImageView>(R.id.searchButton)

        updateUI()

        // 공지 등록 버튼
        postNoticeButton.setOnClickListener {
            val intent = Intent(this, NoticeCouncilWriteActivity::class.java)
            writeNoticeLauncher.launch(intent)
        }

        // 검색창 띄우기
        searchButton.setOnClickListener {
            val intent = Intent(this, NoticeCouncilSearchActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 / 홈 버튼
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }

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
        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = noticeAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy > 0 && !rv.canScrollVertically(1)) {
                    viewModel.loadMoreNotices()
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.noticeList.observe(this) { notices ->
            Log.d("checking", "notice count : ${notices.size}")
            noticeAdapter.updateData(notices)
            updateUI()
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            Toast.makeText(this, "오류: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

    // 공지 삭제
    private fun performDeleteNotice(noticeId: Int) {
        val deleteDialog = DeleteDialogFragment(this) {
            viewModel.deleteNotice(noticeId)

            viewModel.deleteResult.observe(this) { result ->
                result.onSuccess { response ->
                    if (response.isSuccess) {
                        Toast.makeText(this, "삭제 성공", Toast.LENGTH_SHORT).show()
                        viewModel.removeNoticeFromList(noticeId)
                    } else {
                        Toast.makeText(this, response.message ?: "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { error ->
                    Toast.makeText(this, error.message ?: "삭제 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
        deleteDialog.show()
    }

    private fun showNoticeDetailActivity(notice: GetCouncilNoticeDataResponse) {
        val model = NoticeCouncilModel(
            title = notice.title,
            body = notice.body,
            date = notice.time,
            imageList = notice.representationImages?.let { listOf(it) }
        )

        val intent = Intent(this, NoticeCouncilDetailActivity::class.java)
        intent.putExtra("notice_id", notice.id)
        startActivity(intent)
    }
}