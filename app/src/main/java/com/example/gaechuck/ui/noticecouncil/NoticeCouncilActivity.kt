package com.example.gaechuck.ui.noticecouncil

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.repository.NoticeCouncilRepository
import com.example.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import kotlinx.coroutines.launch


class NoticeCouncilActivity : AppCompatActivity() {

    private lateinit var noticeAdapter: NoticeCouncilAdapter
    private val viewModel: NoticeCouncilViewModel by viewModels {
        NoticeCouncilViewModelFactory(NoticeCouncilRepository())
    }

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
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<ImageView>(R.id.searchButton)

        updateUI()

        postNoticeButton.setOnClickListener {
            val intent = Intent(this, NoticeCouncilWriteActivity::class.java)
            writeNoticeLauncher.launch(intent)
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
        observeViewModel()
        viewModel.fetchNotices()

        searchButton.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }
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


    private fun updateUI() {
        val token = AuthManager.getToken()
        val postNoticeButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.postNoticeButton)

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


        viewModel.noticeList.observe(this) { notices ->
            noticeAdapter.updateData(notices)
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

    // 공지 삭제
    private fun performDeleteNotice(noticeId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.alert_detail_popup, null)

        // 커스텀 다이얼로그 생성
        val dialog = AlertDialog.Builder(this)
            .setTitle("삭제 확인")
            .setMessage("정말 삭제하시겠습니까?")
            .setView(dialogView) // 커스텀 레이아웃 설정
            .create()

        // 버튼 동작 설정
        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_yes_btn)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_no_btn)

        positiveButton.setOnClickListener {
            // 확인 버튼 클릭 시 삭제 처리
            viewModel.deleteNotice(noticeId)
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(R.drawable.custom_popup_background)
        dialog.show()
    }

    private fun observeViewModel() {
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
        viewModel.fetchNotices()
    }

    private fun performSearch(query: String) {
        noticeAdapter.filter(query)
    }

}