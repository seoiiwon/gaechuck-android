package com.gaechuck_package.gaechuck.data.model

data class UserProfile(
    var nickname: String,
    var department: String,
    var grade: String,
    val studentId: String,
    val profileImageUrl: String? = null
)
