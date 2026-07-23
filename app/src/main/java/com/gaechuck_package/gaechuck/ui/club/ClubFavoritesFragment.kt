package com.gaechuck_package.gaechuck.ui.club

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentClubFavoritesBinding
import com.gaechuck_package.gaechuck.repository.ClubRepository
import com.gaechuck_package.gaechuck.ui.club.adapter.ClubListAdapter
import com.gaechuck_package.gaechuck.ui.club.viewmodel.ClubViewModel

class ClubFavoritesFragment : Fragment(R.layout.fragment_club_favorites) {

    private lateinit var binding: FragmentClubFavoritesBinding
    private lateinit var viewModel: ClubViewModel
    private lateinit var adapter: ClubListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClubFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ClubViewModel.ClubViewModelFactory(ClubRepository.getInstance())
        viewModel = ViewModelProvider(this, viewModelFactory)[ClubViewModel::class.java]

        (activity as? ClubActivity)?.apply {
            setFavoriteButtonVisible(false, onClick = null)
            setToolbarTitle("찜 목록")
        }

        adapter = ClubListAdapter(
            emptyList(),
            onItemClick = { club ->
                val action = ClubFavoritesFragmentDirections
                    .actionClubFavoritesFragmentToClubDetailFragment(club.id)
                view.findNavController().navigate(action)
            },
            onFavoriteClick = { club ->
                viewModel.toggleFavorite(club.id)
                viewModel.loadFavorites()
            }
        )
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ClubFavoritesFragment.adapter
        }

        binding.sortDropdown.text = "정렬 : ${viewModel.sortOption.label}"
        binding.sortDropdown.setOnClickListener { anchor ->
            val popup = android.widget.PopupMenu(requireContext(), anchor)
            ClubViewModel.SortOption.entries.forEach { option -> popup.menu.add(option.label) }
            popup.setOnMenuItemClickListener { menuItem ->
                val selected = ClubViewModel.SortOption.entries.first { it.label == menuItem.title }
                viewModel.sortOption = selected
                binding.sortDropdown.text = "정렬 : ${selected.label}"
                viewModel.loadFavorites()
                true
            }
            popup.show()
        }

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.updateItems(favorites)
            binding.totalCount.text = "총 ${favorites.size}개"
            binding.emptyState.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
            binding.favoritesRecyclerView.visibility = if (favorites.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        (activity as? ClubActivity)?.apply {
            setToolbarTitle("찜 목록")
            setFavoriteButtonVisible(false, onClick = null)
            setShareButtonVisible(false, null)
        }
        viewModel.loadFavorites()
    }
}
