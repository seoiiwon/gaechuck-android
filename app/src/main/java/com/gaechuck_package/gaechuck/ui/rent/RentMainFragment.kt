package com.gaechuck_package.gaechuck.ui.rent

import android.content.Intent
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
import com.gaechuck_package.gaechuck.data.response.RentList
import com.gaechuck_package.gaechuck.databinding.FragmentRentMainBinding
import com.gaechuck_package.gaechuck.repository.RentRepository
import com.gaechuck_package.gaechuck.ui.rent.adapter.RentAdapter
import com.gaechuck_package.gaechuck.ui.rent.viewmodel.RentViewModel
import com.gaechuck_package.gaechuck.ui.util.DeleteDialogFragment

class RentMainFragment : Fragment(R.layout.fragment_rent_main), RentAdapter.OnRentItemClickListener {
    private lateinit var binding: FragmentRentMainBinding
    private lateinit var rentViewModel: RentViewModel
    private lateinit var rentAdapter: RentAdapter

    private var selectedCategory = "전체"
    private var allItems: List<RentList> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        rentViewModel.checkLoginStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = RentRepository()
        val viewModelFactory = RentViewModel.RentViewModelFactory(repository)
        rentViewModel = ViewModelProvider(this, viewModelFactory).get(RentViewModel::class.java)

        setupRecyclerView()
        setupSearch()
        setupCategoryChips()
        setupObservers()

        binding.writeBtn.setOnClickListener {
            val intent = Intent(activity, RentWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        rentViewModel.checkLoginStatus()
        rentViewModel.loadRentData()
        rentViewModel.RentDetailRetrofit("렌트")
    }

    private fun setupRecyclerView() {
        rentAdapter = RentAdapter(this)
        binding.rentView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = rentAdapter
        }

        binding.rentView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    rentViewModel.loadRentData()
                }
            }
        })
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
            binding.chipSports to "스포츠",
            binding.chipElectronics to "전자기기",
            binding.chipDaily to "생활용품"
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
        if (category != "전체") {
            val keywords = categoryKeywords(category)
            filtered = filtered.filter { item ->
                val name = item.rentItemName
                val cat = item.rentCategory
                (cat != null && cat.contains(category, ignoreCase = true)) ||
                    keywords.any { kw -> name.contains(kw, ignoreCase = true) }
            }
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.rentItemName.contains(query, ignoreCase = true) }
        }
        rentAdapter.submitList(filtered)
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rentView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun categoryKeywords(category: String) = when (category) {
        "스포츠" -> listOf("축구", "농구", "배구", "탁구", "족구", "공", "배드민턴", "테니스", "야구", "볼", "라켓")
        "전자기기" -> listOf("충전", "케이블", "마이크", "스피커", "카메라", "노트북", "태블릿", "이어폰", "전기")
        "생활용품" -> listOf("의자", "접이식", "천막", "텐트", "공기주입기", "풍선", "우산", "쿨러", "행거", "바구니")
        else -> emptyList()
    }

    private fun setupObservers() {
        rentViewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            binding.writeBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
            rentAdapter.updateLoginState(isLoggedIn)
            (activity as? RentActivity)?.updateToolbar(
                title = getString(R.string.bar_rent),
                showBackButton = true,
                showHomeButton = false,
                showEtcButton = false,
                showSearchButton = false,
            )
        }

        rentViewModel.rentList.observe(viewLifecycleOwner) { rentItems ->
            allItems = rentItems.toList()
            applyFilter(binding.searchEditText.text.toString(), selectedCategory)
        }
    }

    override fun OnRentItemClick(item: RentList) {
        val action = RentMainFragmentDirections.actionRentMainFragmentToRentDetailFragment(item.rentItemId)
        view?.findNavController()?.navigate(action)
    }

    override fun onEditClicked(item: RentList) {
        val intent = Intent(requireContext(), RentEditActivity::class.java).apply {
            putExtra("rentItemId", item.rentItemId)
            putExtra("rentItemName", item.rentItemName)
            putExtra("rentItemCount", item.rentItemCount)
            putStringArrayListExtra("rentItemImage", arrayListOf(item.image))
        }
        startActivity(intent)
    }

    override fun onDeleteClicked(item: RentList) {
        val dialog = DeleteDialogFragment(requireContext()) {
            rentViewModel.deleteData(item.rentItemId)
        }
        dialog.show()
    }
}
