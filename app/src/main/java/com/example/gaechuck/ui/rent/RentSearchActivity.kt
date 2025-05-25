package com.example.gaechuck.ui.rent

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.rent.adapter.RentAdapter
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel
import com.example.gaechuck.ui.util.SearchFailFragment

class RentSearchActivity: AppCompatActivity(R.layout.activity_business_search),
    RentAdapter.OnRentItemClickListener {
    private lateinit var rentViewModel: RentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RentAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton : ImageView

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

        backButton = findViewById(R.id.button_back)
        searchEditText = findViewById(R.id.search_text)
        searchEditText.hint = "물품을 입력하세요."
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    rentViewModel.searchRentItems(query)
                } else {
                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        observeViewModel()

        backButton.setOnClickListener{
            val intent = Intent(this, RentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        rentViewModel.filterRentList.observe(this) { list ->
            recyclerView.visibility = RecyclerView.VISIBLE
            adapter.updateItems(list)
            removeSearchFailFragment()
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
}