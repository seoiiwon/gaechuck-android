package com.gaechuck_package.gaechuck.ui.lose

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.LoseList
import com.gaechuck_package.gaechuck.databinding.FragmentLoseMainBinding
import com.gaechuck_package.gaechuck.repository.LoseRepository
import com.gaechuck_package.gaechuck.ui.lose.adapter.LoseListAdapter
import com.gaechuck_package.gaechuck.ui.lose.viewmodel.LoseViewModel

class LoseMainFragment : Fragment(R.layout.fragment_lose_main) {
    private lateinit var binding: FragmentLoseMainBinding
    private lateinit var viewModel: LoseViewModel
    private lateinit var loseListAdapter: LoseListAdapter

    private var selectedCategory = "전체"
    private var allItems: List<LoseList> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoseMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkLoginStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        setupRecyclerView()
        setupSearch()
        setupCategoryChips()
        setupObservers()

        viewModel.checkLoginStatus()
        viewModel.loadLoseData(0)
        viewModel.LoseDetailRetrofit("분실물")
    }

    private fun setupRecyclerView() {
        loseListAdapter = LoseListAdapter(emptyList()) { item ->
            val action = LoseMainFragmentDirections.actionLoseMainFragmentRoLoseDetailFragment(item.lostItemId)
            view?.findNavController()?.navigate(action)
        }
        binding.loseRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = loseListAdapter
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrEmpty()
                binding.searchClearBtn.visibility = if (hasText) View.VISIBLE else View.GONE
                applyFilter(s?.toString() ?: "", selectedCategory)
            }
        })

        binding.searchClearBtn.setOnClickListener {
            binding.searchEditText.setText("")
        }
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipAll to "전체",
            binding.chipElectronics to "전자기기",
            binding.chipWallet to "지갑·카드",
            binding.chipClothes to "의류",
            binding.chipOthers to "기타"
        )
        chips.forEach { (chip, category) ->
            chip.setOnClickListener {
                selectedCategory = category
                updateChipUi(chips, category)
                applyFilter(binding.searchEditText.text.toString(), category)
            }
        }
    }

    private fun updateChipUi(chips: List<Pair<TextView, String>>, selected: String) {
        chips.forEach { (chip, category) ->
            if (category == selected) {
                chip.setBackgroundResource(R.drawable.bg_lose_chip_selected)
                chip.setTextColor(Color.parseColor("#005478"))
            } else {
                chip.setBackgroundResource(R.drawable.bg_lose_chip_unselected)
                chip.setTextColor(Color.parseColor("#999999"))
            }
        }
    }

    private fun applyFilter(query: String, category: String) {
        var filtered = allItems
        if (category != "전체" && category != "기타") {
            val keywords = categoryKeywords(category)
            filtered = filtered.filter { item ->
                keywords.any { kw -> item.title.contains(kw, ignoreCase = true) }
            }
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
        }
        loseListAdapter.updateItems(filtered)
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.loseRecyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun categoryKeywords(category: String) = when (category) {
        "전자기기" -> listOf("핸드폰", "폰", "노트북", "아이패드", "이어폰", "갤럭시", "아이폰", "충전기", "태블릿", "맥북", "카메라")
        "지갑·카드" -> listOf("지갑", "카드", "학생증", "신분증", "통장")
        "의류" -> listOf("옷", "자켓", "점퍼", "모자", "신발", "가방", "코트", "패딩", "우산", "후드")
        else -> emptyList()
    }

    private fun setupObservers() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            (activity as? LoseActivity)?.updateToolbar(
                title = getString(R.string.bar_lose),
                showBackButton = true,
                showHomeButton = false,
                showEtcButton = false,
                showSearchButton = false,
                showWriteButton = isLoggedIn
            )
        }

        viewModel.loseList.observe(viewLifecycleOwner) { loseList ->
            allItems = loseList
            applyFilter(binding.searchEditText.text.toString(), selectedCategory)
        }
    }
}
