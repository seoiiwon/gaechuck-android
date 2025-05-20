package com.example.gaechuck.ui.lose

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.LoseList
import com.example.gaechuck.repository.LoseRepository
import com.example.gaechuck.ui.lose.adapter.LoseAdapter
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.example.gaechuck.ui.util.GridSpacingItemDecoration
import com.example.gaechuck.ui.util.SearchFailFragment

class LoseSearchActivity : AppCompatActivity(R.layout.activity_lose_search), LoseAdapter.OnLoseItemClickListener {
    private lateinit var loseViewModel: LoseViewModel
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: LoseAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lose_search)

        // viewmodel 설정
        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        loseViewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        recyclerView = findViewById(R.id.search_result)
        adapter = LoseAdapter(emptyList(), 9,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, 24, true))
        backButton = findViewById(R.id.button_back)

        searchEditText = findViewById(R.id.search_text)
        searchEditText.hint = "분실물을 확인하세요."
        searchEditText.setOnEditorActionListener{_, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    loseViewModel.searchLoseItems(query)
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
            val intent = Intent(this, LoseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        loseViewModel.filterLoseList.observe(this) { list ->
            if (list.isNotEmpty()) {
                recyclerView.visibility = RecyclerView.VISIBLE
                adapter.updateData(list)
                removeSearchFailFragment()
            } else {
                recyclerView.visibility = RecyclerView.GONE
                showSearchFailFragment()
            }
        }
    }

    private fun showSearchFailFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SearchFailFragment(), "search_fail")
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