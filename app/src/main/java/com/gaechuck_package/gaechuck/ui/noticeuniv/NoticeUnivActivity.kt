package com.gaechuck_package.gaechuck.ui.noticeuniv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.gaechuck_package.gaechuck.repository.NoticeCouncilRepository
import com.gaechuck_package.gaechuck.repository.NoticeUnivRepository
import com.gaechuck_package.gaechuck.ui.noticecouncil.NoticeCouncilDetailActivity
import com.gaechuck_package.gaechuck.ui.noticecouncil.NoticeCouncilUpdateActivity
import com.gaechuck_package.gaechuck.ui.noticecouncil.NoticeCouncilWriteActivity
import com.gaechuck_package.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.gaechuck_package.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.gaechuck_package.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import com.gaechuck_package.gaechuck.ui.noticeuniv.viewmodel.NoticeUnivViewModel
import com.gaechuck_package.gaechuck.ui.util.DeleteDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoticeUnivActivity : AppCompatActivity() {

    enum class NoticeMode { SCHOOL, COUNCIL }

    private var currentMode = NoticeMode.SCHOOL
    private var currentBbsId = ""
    private var searchQuery = ""
    private val searchDebounce = Handler(Looper.getMainLooper())

    private lateinit var schoolViewModel: NoticeUnivViewModel
    private val councilViewModel: NoticeCouncilViewModel by viewModels {
        NoticeCouncilViewModel.Factory(NoticeCouncilRepository())
    }

    private lateinit var schoolAdapter: NoticeUnivAdapter
    private lateinit var councilAdapter: NoticeCouncilAdapter

    private lateinit var noticeTypeDropdown: TextView
    private lateinit var subCategoryScrollView: View
    private lateinit var searchEditText: EditText
    private lateinit var clearBtn: ImageView
    private lateinit var noticeRecyclerView: RecyclerView

    private val updateNoticeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) councilViewModel.fetchNotices()
    }
    private val writeNoticeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) councilViewModel.fetchNotices()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_univ)

        if (intent.getStringExtra("mode") == "COUNCIL") {
            currentMode = NoticeMode.COUNCIL
        }

        schoolViewModel = NoticeUnivViewModel(NoticeUnivRepository())

        noticeTypeDropdown = findViewById(R.id.noticeTypeDropdown)
        subCategoryScrollView = findViewById(R.id.subCategoryScrollView)
        searchEditText = findViewById(R.id.searchEditText)
        clearBtn = findViewById(R.id.clearBtn)
        noticeRecyclerView = findViewById(R.id.noticeRecyclerView)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }

        setupAdapters()
        setupSearch()
        setupDropdown()
        setupCategoryChips()
        setupObservers()
        applyMode()
    }

    private fun setupAdapters() {
        schoolAdapter = NoticeUnivAdapter(mutableListOf()) { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        councilAdapter = NoticeCouncilAdapter(
            mutableListOf(),
            onDeleteClick = { noticeId -> performDelete(noticeId) },
            onUpdateClick = { noticeId ->
                updateNoticeLauncher.launch(
                    Intent(this, NoticeCouncilUpdateActivity::class.java).putExtra("notice_id", noticeId)
                )
            },
            onItemClick = { notice ->
                startActivity(Intent(this, NoticeCouncilDetailActivity::class.java).putExtra("notice_id", notice.id))
            }
        )

        noticeRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty().trim()
                searchQuery = query
                clearBtn.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

                searchDebounce.removeCallbacksAndMessages(null)
                searchDebounce.postDelayed({
                    if (currentMode == NoticeMode.SCHOOL) {
                        schoolViewModel.fetchNotices(page = 0, bbsId = currentBbsId, title = query.ifEmpty { null }, size = 20)
                    } else {
                        if (query.isEmpty()) councilViewModel.fetchNotices()
                        else councilViewModel.search(query)
                    }
                }, 400)
            }
        })

        clearBtn.setOnClickListener {
            searchEditText.setText("")
            searchQuery = ""
            clearBtn.visibility = View.GONE
            loadData()
        }
    }

    private fun setupDropdown() {
        noticeTypeDropdown.setOnClickListener {
            // 현재 모드와 반대 옵션을 위에 표시 (레퍼런스 이미지 기준)
            val options = if (currentMode == NoticeMode.SCHOOL) {
                arrayOf("총학생회", "교내")
            } else {
                arrayOf("교내", "총학생회")
            }
            val popup = ListPopupWindow(this)
            popup.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, options))
            popup.anchorView = noticeTypeDropdown
            popup.width = ListPopupWindow.WRAP_CONTENT
            popup.isModal = true
            popup.setOnItemClickListener { _, _, position, _ ->
                val selected = options[position]
                currentMode = if (selected == "교내") NoticeMode.SCHOOL else NoticeMode.COUNCIL
                searchEditText.setText("")
                searchQuery = ""
                currentBbsId = ""
                clearBtn.visibility = View.GONE
                popup.dismiss()
                resetChips()
                applyMode()
            }
            popup.show()
        }
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            R.id.tabInstitution to "기관",
            R.id.tabAcademic to "학사",
            R.id.tabScholarship to "장학",
            R.id.tabRecruitment to "채용"
        )
        chips.forEach { (id, bbsId) ->
            val chip = findViewById<TextView>(id)
            chip.setOnClickListener {
                val isAlreadySelected = currentBbsId == bbsId
                currentBbsId = if (isAlreadySelected) "" else bbsId
                updateChipStates()
                schoolViewModel.fetchNotices(page = 0, bbsId = currentBbsId, title = searchQuery.ifEmpty { null }, size = 20)
                noticeRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun updateChipStates() {
        val chipBbsMap = mapOf(
            R.id.tabInstitution to "기관",
            R.id.tabAcademic to "학사",
            R.id.tabScholarship to "장학",
            R.id.tabRecruitment to "채용"
        )
        chipBbsMap.forEach { (id, bbsId) ->
            val chip = findViewById<TextView>(id)
            val selected = currentBbsId == bbsId
            chip.setBackgroundResource(if (selected) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected)
            chip.setTextColor(ContextCompat.getColor(this, if (selected) R.color.white else R.color.text_secondary))
        }
    }

    private fun resetChips() {
        currentBbsId = ""
        updateChipStates()
    }

    private fun applyMode() {
        when (currentMode) {
            NoticeMode.SCHOOL -> {
                noticeTypeDropdown.text = "교내 ▾"
                subCategoryScrollView.visibility = View.VISIBLE
                noticeRecyclerView.adapter = schoolAdapter
                loadData()
            }
            NoticeMode.COUNCIL -> {
                noticeTypeDropdown.text = "총학생회 ▾"
                subCategoryScrollView.visibility = View.GONE
                noticeRecyclerView.adapter = councilAdapter
                setupCouncilFab()
                loadData()
            }
        }
    }

    private fun setupCouncilFab() {
        // FAB is not in the layout directly; council write is via postNoticeButton if present
    }

    private fun loadData() {
        when (currentMode) {
            NoticeMode.SCHOOL -> schoolViewModel.fetchNotices(page = 0, bbsId = currentBbsId, title = null, size = 20)
            NoticeMode.COUNCIL -> councilViewModel.fetchNotices()
        }
    }

    private fun setupObservers() {
        // School observers
        schoolViewModel.notices.observe(this) { notices ->
            schoolAdapter.setNotices(notices)
        }
        schoolViewModel.errorMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Council observers
        councilViewModel.noticeList.observe(this) { list ->
            if (searchQuery.isEmpty()) councilAdapter.updateData(list)
        }
        councilViewModel.searchResults.observe(this) { results ->
            if (searchQuery.isNotEmpty()) councilAdapter.updateData(results)
        }
        councilViewModel.errorMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Infinite scroll for school mode
        noticeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (currentMode != NoticeMode.SCHOOL) return
                val lm = rv.layoutManager as LinearLayoutManager
                if (!schoolViewModel.isLoading && schoolViewModel.hasMoreData
                    && lm.findLastVisibleItemPosition() + 1 >= lm.itemCount
                ) {
                    schoolViewModel.fetchNotices(
                        page = schoolViewModel.currentPage + 1,
                        bbsId = currentBbsId,
                        title = searchQuery.ifEmpty { null },
                        size = 20
                    )
                }
                // Council infinite scroll
                if (currentMode == NoticeMode.COUNCIL && dy > 0 && !rv.canScrollVertically(1)) {
                    councilViewModel.loadMoreNotices()
                }
            }
        })
    }

    private fun performDelete(noticeId: Int) {
        val dialog = DeleteDialogFragment(this) {
            councilViewModel.deleteNotice(noticeId)
            councilViewModel.deleteResult.observe(this) { result ->
                result.onSuccess { response ->
                    if (response.isSuccess) {
                        Toast.makeText(this, "삭제 성공", Toast.LENGTH_SHORT).show()
                        councilViewModel.removeNoticeFromList(noticeId)
                    } else {
                        Toast.makeText(this, response.message ?: "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { error ->
                    Toast.makeText(this, error.message ?: "오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
}
