package com.gaechuck_package.gaechuck.ui.club

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentClubMainBinding
import com.gaechuck_package.gaechuck.repository.ClubRepository
import com.gaechuck_package.gaechuck.ui.club.adapter.ClubListAdapter
import com.gaechuck_package.gaechuck.ui.club.viewmodel.ClubViewModel

class ClubMainFragment : Fragment(R.layout.fragment_club_main) {

    private lateinit var binding: FragmentClubMainBinding
    private lateinit var viewModel: ClubViewModel
    private lateinit var adapter: ClubListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClubMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ClubViewModel.ClubViewModelFactory(ClubRepository.getInstance())
        viewModel = ViewModelProvider(this, viewModelFactory)[ClubViewModel::class.java]

        (activity as? ClubActivity)?.setToolbarTitle("동아리 찾기")
        (activity as? ClubActivity)?.setFavoriteButtonVisible(true) {
            view.findNavController().navigate(R.id.action_clubMainFragment_to_clubFavoritesFragment)
        }

        setupRecyclerView()
        setupSearch()
        setupCategoryChips()
        setupToggle()
        setupSort()
        setupObservers()

        viewModel.loadClubs()
    }

    override fun onResume() {
        super.onResume()
        (activity as? ClubActivity)?.setToolbarTitle("동아리 찾기")
        (activity as? ClubActivity)?.setShareButtonVisible(false, null)
        (activity as? ClubActivity)?.setFavoriteButtonVisible(true) {
            view?.findNavController()?.navigate(R.id.action_clubMainFragment_to_clubFavoritesFragment)
        }
        viewModel.applyFilters()
    }

    private fun setupRecyclerView() {
        adapter = ClubListAdapter(
            emptyList(),
            onItemClick = { club ->
                val action = ClubMainFragmentDirections.actionClubMainFragmentToClubDetailFragment(club.id)
                view?.findNavController()?.navigate(action)
            },
            onFavoriteClick = { club -> viewModel.toggleFavorite(club.id) }
        )
        binding.clubRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ClubMainFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrEmpty()
                binding.searchClearBtn.visibility = if (hasText) View.VISIBLE else View.GONE
                viewModel.search(s?.toString() ?: "")
            }
        })
        binding.searchClearBtn.setOnClickListener { binding.searchEditText.setText("") }
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipAll to "전체",
            binding.chipSports to "스포츠",
            binding.chipAcademic to "학술/스터디",
            binding.chipCulture to "문화/예술",
            binding.chipVolunteer to "봉사",
            binding.chipReligion to "종교",
            binding.chipCareer to "취업/창업",
            binding.chipHobby to "친목/취미"
        )
        chips.forEach { (chip, category) ->
            chip.setOnClickListener {
                viewModel.selectedCategory = category
                updateChipUi(chips, category)
                viewModel.search(binding.searchEditText.text.toString())
            }
        }
        updateChipUi(chips, viewModel.selectedCategory)
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

    private fun setupToggle() {
        binding.toggleRecruitingOnly.isChecked = viewModel.recruitingOnly
        binding.toggleRecruitingOnly.setOnCheckedChangeListener { _, isChecked ->
            viewModel.recruitingOnly = isChecked
            viewModel.search(binding.searchEditText.text.toString())
        }
    }

    private fun setupSort() {
        binding.sortDropdown.text = "정렬 : ${viewModel.sortOption.label}"
        binding.sortDropdown.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(requireContext(), view)
            ClubViewModel.SortOption.entries.forEach { option ->
                popup.menu.add(option.label)
            }
            popup.setOnMenuItemClickListener { menuItem ->
                val selected = ClubViewModel.SortOption.entries.first { it.label == menuItem.title }
                viewModel.sortOption = selected
                binding.sortDropdown.text = "정렬 : ${selected.label}"
                viewModel.search(binding.searchEditText.text.toString())
                true
            }
            popup.show()
        }
    }

    private fun setupObservers() {
        viewModel.clubList.observe(viewLifecycleOwner) { clubs ->
            adapter.updateItems(clubs)
            binding.emptyState.visibility = if (clubs.isEmpty()) View.VISIBLE else View.GONE
            binding.clubRecyclerView.visibility = if (clubs.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
