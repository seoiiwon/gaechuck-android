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
import com.gaechuck_package.gaechuck.ui.lose.adapter.LoseListAdapter
import com.gaechuck_package.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.gaechuck_package.gaechuck.ui.util.SearchFailFragment

class LoseSearchActivity : AppCompatActivity(R.layout.activity_lose_search) {
    private lateinit var loseViewModel: LoseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoseListAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton: ImageView
    private lateinit var noSearch: TextView

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false
    private var currentQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lose_search)

        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        loseViewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        recyclerView = findViewById(R.id.search_result)
        adapter = LoseListAdapter(emptyList()) { item -> navigateToDetail(item) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        backButton = findViewById(R.id.button_back)
        noSearch = findViewById(R.id.no_search)
        searchEditText = findViewById(R.id.search_text)
        searchEditText.hint = "분실물을 검색하세요."

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    currentQuery = query
                    currentPage = 0
                    isLastPage = false
                    isLoading = false
                    adapter.updateItems(emptyList())
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

        backButton.setOnClickListener {
            val intent = Intent(this, LoseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToDetail(item: LoseList) {
        val intent = Intent(this, LoseActivity::class.java).apply {
            putExtra("lostItemId", item.lostItemId)
            putExtra("startFromSearch", true)
        }
        startActivity(intent)
    }

    private fun observeViewModel() {
        loseViewModel.filterLoseList.observe(this) { list ->
            recyclerView.visibility = RecyclerView.VISIBLE
            noSearch.visibility = TextView.GONE
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
}
