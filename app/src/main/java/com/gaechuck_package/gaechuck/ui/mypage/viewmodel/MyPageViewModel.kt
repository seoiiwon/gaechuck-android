package com.gaechuck_package.gaechuck.ui.mypage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.data.model.UserProfile
import com.gaechuck_package.gaechuck.repository.MyPageRepository

sealed class NicknameValidation {
    object Valid : NicknameValidation()
    object Invalid : NicknameValidation()
}

class MyPageViewModel(private val repository: MyPageRepository) : ViewModel() {

    private val _profile = MutableLiveData<UserProfile>()
    val profile: LiveData<UserProfile> get() = _profile

    fun loadProfile() {
        _profile.value = repository.getProfile()
    }

    fun validateNickname(value: String): NicknameValidation {
        val regex = Regex("^[가-힣a-zA-Z]{1,6}$")
        return if (regex.matches(value)) NicknameValidation.Valid else NicknameValidation.Invalid
    }

    fun saveNickname(value: String): Boolean {
        if (validateNickname(value) !is NicknameValidation.Valid) return false
        repository.updateNickname(value)
        _profile.value = repository.getProfile()
        return true
    }

    fun saveDepartment(value: String) {
        repository.updateDepartment(value)
        _profile.value = repository.getProfile()
    }

    fun saveGrade(value: String) {
        repository.updateGrade(value)
        _profile.value = repository.getProfile()
    }

    fun logout() {
        AuthManager.clearTokens()
    }

    class MyPageViewModelFactory(private val repository: MyPageRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MyPageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
