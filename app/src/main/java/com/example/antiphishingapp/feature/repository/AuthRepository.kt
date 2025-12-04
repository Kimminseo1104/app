package com.example.antiphishingapp.feature.repository

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.antiphishingapp.feature.model.TokenResponse
import com.example.antiphishingapp.feature.model.UserResponse
import com.example.antiphishingapp.network.ApiClient

/**
 * AuthRepository
 * ------------------------
 * 사용자 인증 토큰 (Access Token, Refresh Token)을 안전하게 저장하고 관리하는 Repository.
 * Context를 생성자로 받아 의존성을 주입받습니다.
 */
class AuthRepository(private val context: Context) {

    private val PREFS_NAME = "secure_auth_prefs"
    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_IS_AUTO_LOGIN = "is_auto_login"

    private val securePrefs by lazy {
        // MasterKey 생성 (Android KeyStore 사용)
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // EncryptedSharedPreferences 생성 및 초기화
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // 네이버 State 저장 등 일반 설정을 위한 일반 SharedPreferences
    // 네이버 state 저장을 위해 사용 (민감 정보가 아니므로 일반 SharedPreferences 사용)
    private val appPrefs by lazy {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    // 로그인 성공 후 서버로부터 반환받은 토큰 및 자동 로그인 여부 저장
    fun saveTokens(tokenResponse: TokenResponse, isAutoLogin: Boolean) {
        securePrefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, tokenResponse.accessToken)
            putString(KEY_REFRESH_TOKEN, tokenResponse.refreshToken)
            putBoolean(KEY_IS_AUTO_LOGIN, isAutoLogin) // 자동 로그인 설정 저장
            apply()
        }
    }

    // 저장된 Access Token을 반환
    fun getAccessToken(): String? {
        // 토큰이 없으면 null 반환
        return securePrefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // 저장된 Refresh Token을 반환
    fun getRefreshToken(): String? {
        return securePrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // 토큰이 유효한지, 자동 로그인이 설정되어 있는지 확인
    // 앱 시작 시 Splash 화면 등에서 이 함수를 호출하여 자동 로그인 여부를 판단
    fun isAuthenticated(): Boolean {
        val hasToken = getAccessToken() != null
        val isAutoLogin = securePrefs.getBoolean(KEY_IS_AUTO_LOGIN, false)

        // 토큰이 있어도 사용자가 자동 로그인을 체크하지 않았다면 false 반환
        return hasToken && isAutoLogin
    }

    // 저장된 모든 토큰을 삭제
    fun clearTokens() {
        securePrefs.edit().clear().apply()
    }

    // 로그인한 사용자의 정보를 가져옴
    suspend fun getMe(): UserResponse? {
        val token = getAccessToken() ?: return null

        val response = ApiClient.apiService.getMe("Bearer $token")

        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }


    // 네이버 State 및 일반 Key-Value 관리 메서드 (SocialLoginViewModel에서 사용)

    // 1. SharedPreferences에 Key-Value 저장 (SocialLoginViewModel.getNaverAuthUrl에서 state 저장 시 사용)
    fun saveValue(key: String, value: String) {
        appPrefs.edit().putString(key, value).apply()
    }

    // 2. SharedPreferences에서 Key-Value 조회 (SocialLoginViewModel.handleCallbackUri에서 state 검증 시 사용)
    fun getValue(key: String): String? {
        return appPrefs.getString(key, null)
    }

    // 3. SharedPreferences에서 Key-Value 삭제
    fun clearValue(key: String) {
        appPrefs.edit().remove(key).apply()
    }

    // 소셜 인증 코드를 서버에 보내 JWT 토큰으로 교환
    // (이 함수는 SocialLoginViewModel에서 호출됩니다.)
    suspend fun exchangeCodeForToken(
        provider: String,
        code: String,
        state: String?,
        isAutoLogin: Boolean
    ): TokenResponse {

        val response = when (provider) {
            "kakao" -> {
                // 카카오 API 호출
                ApiClient.apiService.exchangeKakaoCode(code)
            }

            "naver" -> {
                // 네이버 API 호출 (state 필수)
                if (state == null) throw IllegalArgumentException("Naver login requires state.")
                ApiClient.apiService.exchangeNaverCode(code, state)
            }

            else -> throw IllegalArgumentException("Unsupported social provider: $provider")
        }

        if (response.isSuccessful) {
            val tokens = response.body() ?: throw Exception("API returned empty body.")
            saveTokens(tokens, true) // 획득한 토큰을 저장
            return tokens
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown server error"
            throw Exception("Token exchange failed ($provider): ${response.code()} - $errorBody")
        }
    }
}