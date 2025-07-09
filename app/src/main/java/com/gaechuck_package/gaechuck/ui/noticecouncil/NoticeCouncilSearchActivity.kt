package com.gaechuck_package.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.repository.NoticeCouncilRepository
import com.gaechuck_package.gaechuck.ui.noticecouncil.adaptor.NoticeCouncilAdapter
import com.gaechuck_package.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel


class NoticeCouncilSearchActivity : AppCompatActivity() {

    private lateinit var editSearch: EditText
    private lateinit var resultRecycler: RecyclerView
    private lateinit var adapter: NoticeCouncilAdapter
    private lateinit var viewModel: NoticeCouncilViewModel
    private lateinit var clearSearch: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_search)

        editSearch = findViewById(R.id.editSearch)
        editSearch.hint = "궁금한 공지 내용을 입력하세요"

        clearSearch = findViewById(R.id.clearSearch)

        // 텍스트 입력 감지해서 X 버튼 보이기
        editSearch.addTextChangedListener {
            clearSearch.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        // X 버튼 누르면 텍스트 삭제
        clearSearch.setOnClickListener {
            editSearch.text.clear()
            viewModel.search("")
        }

        resultRecycler = findViewById(R.id.resultRecycler)
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }

        adapter = NoticeCouncilAdapter(
            mutableListOf(),
            onDeleteClick = {},
            onUpdateClick = {},
            onItemClick = { notice ->
                val intent = Intent(this, NoticeCouncilDetailActivity::class.java)
                intent.putExtra("notice_id", notice.id)
                startActivity(intent)
            }
        )
        resultRecycler.layoutManager = LinearLayoutManager(this)
        resultRecycler.adapter = adapter

        viewModel = ViewModelProvider(this, NoticeCouncilViewModel.Factory(NoticeCouncilRepository()))
            .get(NoticeCouncilViewModel::class.java)

        viewModel.searchResults.observe(this) {
            adapter.updateData(it)
        }
//        viewModel.searchResult.observe(this) {
//            adapter.updateData(it)
//            findViewById<TextView>(R.id.emptyMessage).visibility =
//                if (it.isEmpty()) View.VISIBLE else View.GONE
//        }

        editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = editSearch.text.toString()
                viewModel.search(query)
                true
            } else false
        }

//        resultRecycler.layoutManager = LinearLayoutManager(this)
    }
}