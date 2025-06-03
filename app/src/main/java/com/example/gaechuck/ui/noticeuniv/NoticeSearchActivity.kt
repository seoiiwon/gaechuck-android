package com.example.gaechuck.ui.noticeuniv

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.ui.noticeuniv.adaptor.NoticeUnivAdapter
import com.example.gaechuck.ui.noticeuniv.viewmodel.NoticeUnivViewModel
import androidx.core.net.toUri
import com.example.gaechuck.repository.NoticeUnivRepository

class NoticeSearchActivity : AppCompatActivity(){
    private lateinit var backBtn: ImageView
    private lateinit var editSearch: EditText
    private lateinit var resultRecycler: RecyclerView
    private lateinit var emptyMessage: TextView


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

        val repo = NoticeUnivRepository()
        viewModel = ViewModelProvider(this,
            NoticeUnivViewModel.Factory(repo))
            .get(NoticeUnivViewModel::class.java)

        viewModel.notices.observe(this) { list ->
            adapter.setNotices(list)
            if (list.isEmpty()) {
                emptyMessage.visibility = View.VISIBLE
                resultRecycler.visibility = View.GONE
            } else {
                emptyMessage.visibility = View.GONE
                resultRecycler.visibility = View.VISIBLE
            }
        }

        editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editSearch.text.toString().trim()

                adapter.setNotices(emptyList())

                viewModel.fetchNotices(
                    page  = 0,
                    bbsId = null,
                    title = query,
                    size  = 20
                )

                resultRecycler.scrollToPosition(0)
                true
            } else false
        }
    }
}