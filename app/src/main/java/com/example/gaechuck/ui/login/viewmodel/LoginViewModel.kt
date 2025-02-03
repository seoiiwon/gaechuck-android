package com.example.gaechuck.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.request.LoginRequest
import kotlinx.coroutines.launch

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
    fun login(id: String, pw: String, onResult: (Boolean) -> Unit) {
        // TODO: 로그인 로직 (예: 서버 요청 등)
        viewModelScope.launch {
            try {
                val response = ApiConnection.getRetrofitService.login(LoginRequest(id, pw))

                // 응답 코드와 메시지 출력
                Log.d("Login", "Response Code: ${response.code()}")
                Log.d("Login", "Response Body: ${response.body()}")
                Log.d("Login", "Response Error Body: ${response.errorBody()}")

                if(response.isSuccessful) {
                    val loginResponse = response.body()?.result
                    AuthManager.saveToken(loginResponse?.accessToken ?: "") // 토큰 저장
                    _loginResult.value = true
                    onResult(true)
                } else {
                    // 실패했을 때 response.errorBody() 출력
                    Log.d("Login", "Login failed: ${response.errorBody()?.string()}")
                    _loginResult.value = false
                    onResult(false)
                }
            } catch (e:Exception) {
                // 예외 메시지 출력
                Log.e("Login", "Login failed with exception: ${e.localizedMessage}", e)
                _loginResult.value = false
                onResult(false)
            }
        }

    }
}