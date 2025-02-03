package com.example.gaechuck.ui.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _isFormValid = MutableLiveData<Boolean>()
    val isFormValid : LiveData<Boolean>
        get() = _isFormValid

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult : LiveData<Boolean>
        get() = _loginResult

    // 로그인 폼 유효성 체크
    fun onCredentialsChanged(id: String, pw: String) {
        _isFormValid.value = id.isNotEmpty() && pw.isNotEmpty()
    }

    // 로그인 시도
    fun login(id: String, pw: String) {
        // TODO: 로그인 로직 (예: 서버 요청 등)
        // 여기서는 예시로 로그인 성공을 true로 설정
        _loginResult.value = true  // 성공적인 로그인 시 true
    }
}