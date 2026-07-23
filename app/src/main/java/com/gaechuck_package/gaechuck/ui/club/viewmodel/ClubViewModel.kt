package com.gaechuck_package.gaechuck.ui.club.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaechuck_package.gaechuck.data.model.Club
import com.gaechuck_package.gaechuck.data.model.ClubDetail
import com.gaechuck_package.gaechuck.data.model.ClubStatus
import com.gaechuck_package.gaechuck.repository.ClubRepository
import kotlinx.coroutines.launch

class ClubViewModel(private val repository: ClubRepository) : ViewModel() {

    private val _clubList = MutableLiveData<List<Club>>()
    val clubList: LiveData<List<Club>> get() = _clubList

    private val _favorites = MutableLiveData<List<Club>>()
    val favorites: LiveData<List<Club>> get() = _favorites

    private val _clubDetail = MutableLiveData<ClubDetail?>()
    val clubDetail: LiveData<ClubDetail?> get() = _clubDetail

    private var allClubs: List<Club> = emptyList()

    var selectedCategory: String = "전체"
    var recruitingOnly: Boolean = false
    var sortOption: SortOption = SortOption.NAME

    enum class SortOption(val label: String) {
        NAME("가나다순"),
        RECENT("최신순")
    }

    private fun List<Club>.sorted(): List<Club> = when (sortOption) {
        SortOption.NAME -> sortedBy { it.name }
        SortOption.RECENT -> sortedByDescending { it.id }
    }

    fun loadClubs() {
        viewModelScope.launch {
            allClubs = repository.getClubs()
            applyFilters()
        }
    }

    fun applyFilters() {
        var filtered = allClubs
        if (selectedCategory != "전체") {
            filtered = filtered.filter { it.category == selectedCategory }
        }
        if (recruitingOnly) {
            filtered = filtered.filter { it.status == ClubStatus.RECRUITING || it.status == ClubStatus.URGENT }
        }
        _clubList.value = filtered.sorted()
    }

    fun search(query: String) {
        var filtered = allClubs
        if (selectedCategory != "전체") {
            filtered = filtered.filter { it.category == selectedCategory }
        }
        if (recruitingOnly) {
            filtered = filtered.filter { it.status == ClubStatus.RECRUITING || it.status == ClubStatus.URGENT }
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            }
        }
        _clubList.value = filtered.sorted()
    }

    fun loadClubDetail(clubId: Int) {
        viewModelScope.launch {
            _clubDetail.value = repository.getClubDetail(clubId)
        }
    }

    fun toggleFavorite(clubId: Int) {
        val isFavorite = repository.toggleFavorite(clubId)
        _clubDetail.value?.let { detail ->
            if (detail.id == clubId) {
                _clubDetail.value = detail.copy(isFavorite = isFavorite)
            }
        }
        viewModelScope.launch {
            allClubs = repository.getClubs()
            applyFilters()
        }
    }

    fun loadFavorites() {
        _favorites.value = repository.getFavorites().sorted()
    }

    class ClubViewModelFactory(private val repository: ClubRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClubViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ClubViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
