package com.example.gaechuck.ui.rent

import VerticalItemDecorate
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.rent.adapter.RentAdapter
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel
import com.example.gaechuck.ui.util.DeleteDialogFragment
import com.example.gaechuck.ui.util.SearchFailFragment

class RentSearchActivity: AppCompatActivity(R.layout.activity_business_search),
    RentAdapter.OnRentItemClickListener {
    private lateinit var rentViewModel: RentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RentAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton : ImageView

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false
    private var currentQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_search)

        // viewmodel 설정
        val repository = RentRepository()
        val viewModelFactory = RentViewModel.RentViewModelFactory(repository)
        rentViewModel = ViewModelProvider(this, viewModelFactory).get(RentViewModel::class.java)

        recyclerView = findViewById(R.id.search_result)
        adapter = RentAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val itemDecoration = VerticalItemDecorate(20)
        recyclerView.addItemDecoration(itemDecoration)

        backButton = findViewById(R.id.button_back)
        searchEditText = findViewById(R.id.search_text)
        searchEditText.hint = "물품을 입력하세요."

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    currentQuery = query
                    currentPage = 0
                    isLastPage = false
                    isLoading = false
                    adapter.updateItems(emptyList()) // UI 초기화
                    rentViewModel.searchRentItemsPaged(query, currentPage++)
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
                        rentViewModel.searchRentItemsPaged(it, currentPage++)
                    }
                }
            }
        })

        observeViewModel()



        backButton.setOnClickListener{
            val intent = Intent(this, RentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
//        rentViewModel.filterRentList.observe(this) { list ->
//            recyclerView.visibility = RecyclerView.VISIBLE
//            adapter.updateItems(list)
//            removeSearchFailFragment()
//        }

        rentViewModel.filterRentList.observe(this) { list ->
            recyclerView.visibility = RecyclerView.VISIBLE
            adapter.updateItems(list)
            isLoading = false
            removeSearchFailFragment()

            // 만약 한 페이지 9개라면
            if (list.size < currentPage * 9) {
                isLastPage = true
            }
        }

        rentViewModel.isSearchResultEmpty.observe(this) { isEmpty ->
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

    override fun OnRentItemClick(item: RentList) {
        val intent = Intent(this, RentActivity::class.java).apply {
            putExtra("rentItemId", item.rentItemId)
            putExtra("startFromSearch", true) // 구분용
        }
        startActivity(intent)

    }

    override fun onEditClicked(item: RentList) {
        // 검색 화면에서는 수정 기능을 사용하지 않으므로 비워둠 또는 로그만
        Toast.makeText(this, "수정 기능은 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClicked(item: RentList) {
        // 검색 화면에서는 삭제 기능도 제한
        Toast.makeText(this, "삭제 기능은 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
    }
}