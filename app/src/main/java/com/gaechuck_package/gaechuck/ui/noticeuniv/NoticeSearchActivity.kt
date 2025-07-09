package com.gaechuck_package.gaechuck.ui.noticeuniv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import com.gaechuck_package.gaechuck.ui.noticeuniv.viewmodel.NoticeUnivViewModel
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.gaechuck_package.gaechuck.repository.NoticeUnivRepository

class NoticeSearchActivity : AppCompatActivity(){
    private lateinit var backBtn: ImageView
    private lateinit var editSearch: EditText
    private lateinit var resultRecycler: RecyclerView
    private lateinit var emptyMessage: TextView

    private var isLoading = false
    private var hasMoreData = true
    private var currentPage = 0

    private lateinit var adapter: NoticeUnivAdapter
    private lateinit var viewModel: NoticeUnivViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_search)

        backBtn        = findViewById(R.id.backBtn)
        editSearch     = findViewById(R.id.editSearch)
        resultRecycler = findViewById(R.id.resultRecycler)
        emptyMessage = findViewById(R.id.emptyMessage)

        backBtn.setOnClickListener { finish() }

        adapter = NoticeUnivAdapter(mutableListOf()) { url ->
            startActivity(
                Intent(Intent.ACTION_VIEW, url.toUri())
            )
        }
        resultRecycler.layoutManager = LinearLayoutManager(this)
        resultRecycler.adapter = adapter

        resultRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy <= 0) return

                val lm = rv.layoutManager as LinearLayoutManager
                val totalItemCount   = lm.itemCount
                val lastVisibleIndex = lm.findLastVisibleItemPosition()

                // 마지막 아이템 근처에 도달했고, 아직 로딩 중이 아니며, 다음 페이지가 있으면
                if (!isLoading && hasMoreData && lastVisibleIndex + 1 >= totalItemCount) {
                    isLoading = true
                    currentPage += 1
                    triggerFetch(page = currentPage)
                }
            }
        })

        val repo = NoticeUnivRepository()
        viewModel = ViewModelProvider(this,
            NoticeUnivViewModel.Factory(repo))
            .get(NoticeUnivViewModel::class.java)

        viewModel.notices.observe(this, Observer { list ->
            isLoading = false

            if (currentPage == 0) {
                if (list.isEmpty()) {
                    emptyMessage.visibility = View.VISIBLE
                    resultRecycler.visibility = View.GONE
                } else {
                    emptyMessage.visibility = View.GONE
                    resultRecycler.visibility = View.VISIBLE

                    adapter.setNotices(list)
                }
            } else {
                if (list.isEmpty()) {
                    hasMoreData = false
                } else {
                    adapter.appendNotices(list)
                }
            }
        })

        editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editSearch.text.toString().trim()

                if (query.isEmpty()) {
                    return@setOnEditorActionListener false
                }

                currentPage = 0
                hasMoreData = true

                adapter.setNotices(emptyList())
                emptyMessage.visibility = View.GONE
                resultRecycler.visibility = View.GONE

                isLoading = true
                triggerFetch(page = 0, title = query)

                true
            } else {
                false
            }
        }
    }

    private fun triggerFetch(page: Int, title: String? = editSearch.text.toString().trim()) {
        viewModel.fetchNotices(
            page  = page,
            bbsId = null,
            title = title,
            size  = 20
        )
    }
}