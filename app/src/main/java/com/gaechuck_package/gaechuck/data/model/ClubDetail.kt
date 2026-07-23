package com.gaechuck_package.gaechuck.data.model

data class ClubDetail(
    val id: Int,
    val name: String,
    val coverImageUrl: String?,
    val orgType: String,
    val category: String,
    val summary: String,
    val status: ClubStatus,
    val beginnerFriendly: Boolean = false,
    val introduction: String,
    val meetingInfo: String? = null,
    val fee: String? = null,
    val preparation: String? = null,
    val contactLabel: String? = null,
    val contactUrl: String? = null,
    val applyUrl: String? = null,
    val recruitSchedule: List<ClubScheduleItem>,
    val galleryImages: List<String>,
    var isFavorite: Boolean = false
)
