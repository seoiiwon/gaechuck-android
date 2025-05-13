package com.example.gaechuck.ui.business

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.BusinessList
import com.example.gaechuck.databinding.FragmentBusinessMainBinding
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.adapter.BusinessAdapter
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.example.gaechuck.ui.util.SearchFailFragment

class BusinessSearchActivity: AppCompatActivity(R.layout.activity_business_search),BusinessAdapter.OnBusinessItemClickListener {
    private lateinit var businessViewModel: BusinessViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusinessAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_search)

        // viewmodel 설정
        val repository = BusinessRepository()
        val viewModelFactory = BusinessViewModel.BusinessViewModelFactory(repository)
        businessViewModel = ViewModelProvider(this, viewModelFactory).get(BusinessViewModel::class.java)

        recyclerView = findViewById(R.id.search_result)
        adapter = BusinessAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton = findViewById(R.id.button_back)

        searchEditText = findViewById(R.id.search_text)
        searchEditText.hint = "제휴 사업을 확인하세요."
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    businessViewModel.searchBusinessItems(query)
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
            val intent = Intent(this, BusinessActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        businessViewModel.filterBusinessList.observe(this) { list ->
            recyclerView.visibility = RecyclerView.VISIBLE
            adapter.updateItems(list)
            removeSearchFailFragment()
        }

        businessViewModel.isSearchResultEmpty.observe(this) { isEmpty ->
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

    override fun onBusinessItemClick(item: BusinessList) {
        val intent = Intent(this, BusinessActivity::class.java).apply {
            putExtra("coalitionId", item.coalitionId)
            putExtra("startFromSearch", true) // 구분용
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}

