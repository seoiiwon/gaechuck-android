package com.gaechuck_package.gaechuck.data.model

enum class ClubStatus { RECRUITING, URGENT, ALWAYS, CLOSED }

data class Club(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val orgType: String,
    val category: String,
    val summary: String,
    val status: ClubStatus,
    val beginnerFriendly: Boolean = false,
    var isFavorite: Boolean = false
)
