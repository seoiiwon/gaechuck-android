package com.gaechuck_package.gaechuck.repository

import com.gaechuck_package.gaechuck.data.model.UserProfile

/**
 * UI-only placeholder data source for the logged-in user's profile.
 * Swap for a real member-profile API call once the backend exists.
 */
class MyPageRepository {

    private var profile = UserProfile(
        nickname = "우울한 대학생1",
        department = "컴퓨터공학부",
        grade = "3학년",
        studentId = "202012345"
    )

    fun getProfile(): UserProfile = profile

    fun updateNickname(value: String) {
        profile = profile.copy(nickname = value)
    }

    fun updateDepartment(value: String) {
        profile = profile.copy(department = value)
    }

    fun updateGrade(value: String) {
        profile = profile.copy(grade = value)
    }

    companion object {
        @Volatile
        private var instance: MyPageRepository? = null

        fun getInstance(): MyPageRepository {
            return instance ?: synchronized(this) {
                instance ?: MyPageRepository().also { instance = it }
            }
        }
    }
}
