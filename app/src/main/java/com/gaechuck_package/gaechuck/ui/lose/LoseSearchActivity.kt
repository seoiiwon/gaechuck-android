package com.gaechuck_package.gaechuck.ui.lose

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.LoseList
import com.gaechuck_package.gaechuck.repository.LoseRepository
import com.gaechuck_package.gaechuck.ui.lose.adapter.GridAdapter
import com.gaechuck_package.gaechuck.ui.lose.adapter.LoseAdapter
import com.gaechuck_package.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.gaechuck_package.gaechuck.ui.util.SearchFailFragment

class LoseSearchActivity : AppCompatActivity(R.layout.activity_lose_search), LoseAdapter.OnLoseItemClickListener {
    private lateinit var loseViewModel: LoseViewModel
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: GridAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton : ImageView
    private lateinit var noSearch : TextView

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false
    private var currentQuery: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lose_search)

        // viewmodel 설정
        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        loseViewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

//        recyclerView = findViewById(R.id.search_result)
//        adapter = LoseAdapter(emptyList(), 9,0, this)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = GridLayoutManager(this, 1)

        recyclerView = findViewById(R.id.search_result)
        adapter = GridAdapter(emptyList(), this)  // ✅ 여기서 LoseAdapter 대신 GridAdapter 직접 사용
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3열 그리드

        backButton = findViewById(R.id.button_back)
        noSearch = findViewById(R.id.no_search)

        searchEditText = findViewById(R.id.search_text)
        searchEditText.hint = "분실물을 확인하세요."
//        searchEditText.setOnEditorActionListener{_, actionId, _ ->
//            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val query = searchEditText.text.toString().trim()
//                if (query.isNotEmpty()) {
//                    loseViewModel.searchLoseItems(query)
//                } else {
//                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
//                }
//                true
//            } else {
//                false
//            }
//        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    currentQuery = query
                    currentPage = 0
                    isLastPage = false
                    isLoading = false
                    adapter.updateItems(emptyList()) // 초기화
                    loseViewModel.searchLoseItemsPaged(query, currentPage++)
                } else {
                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading && !isLastPage) {
                    currentQuery?.let {
                        isLoading = true
                        loseViewModel.searchLoseItemsPaged(it, currentPage++)
                    }
                }
            }
        })

        observeViewModel()

        backButton.setOnClickListener{
            val intent = Intent(this, LoseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
//        loseViewModel.filterLoseList.observe(this) { list ->
//            recyclerView.visibility = RecyclerView.VISIBLE
//            noSearch.visibility = TextView.GONE
//            adapter.updateData(list, 1) // totalPages는 1로 고정
//            removeSearchFailFragment()
//        }
        loseViewModel.filterLoseList.observe(this) { list ->
            recyclerView.visibility = RecyclerView.VISIBLE
            noSearch.visibility = TextView.GONE

//            val totalPages = (list.size + 8) / 9
            adapter.updateItems(list)
            isLoading = false

            removeSearchFailFragment()
        }

        loseViewModel.isSearchResultEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                recyclerView.visibility = RecyclerView.GONE
                showSearchFailFragment()
            }
        }
    }

    private fun showSearchFailFragment() {
        val fragment = SearchFailFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, "search_fail")
            .addToBackStack(null)
            .commit()
    }

    private fun removeSearchFailFragment() {
        supportFragmentManager.findFragmentByTag("search_fail")?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    override fun onLoseItemClick(item: LoseList) {
        val intent = Intent(this, LoseActivity::class.java).apply {
            putExtra("lostItemId", item.lostItemId)
            putExtra("startFromSearch", true)
        }
        startActivity(intent)
    }
}