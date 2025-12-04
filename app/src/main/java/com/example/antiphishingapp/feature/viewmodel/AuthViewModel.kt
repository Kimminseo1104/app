package com.example.antiphishingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.antiphishingapp.feature.model.UserResponse
import com.example.antiphishingapp.feature.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ApplicationContext 주입 (LoginViewModel과 패턴을 맞춤)
    private val authRepository = AuthRepository(application.applicationContext)

    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUser()   // 앱 초기 사용자 정보 로드
    }

    /** 서버에서 로그인 중인 사용자 정보 가져오기 */
    fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true

            val userInfo = authRepository.getMe()
            _user.value = userInfo

            _isLoading.value = false
        }
    }
}
