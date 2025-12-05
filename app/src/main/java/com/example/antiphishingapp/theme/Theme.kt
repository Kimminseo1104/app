// ui/theme/Theme.kt
package com.example.antiphishingapp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color


// Dark 색상 세트
private val DarkColorScheme = darkColorScheme(
    primary = Primary700,    // Primary 색상: 좀 더 어두운 색상
    secondary = Grayscale300, // Secondary 색상: 연한 그레이
    tertiary = Grayscale500   // Tertiary 색상: 그레이 중간 색상
)

// Light 색상 세트
private val LightColorScheme = lightColorScheme(
    primary = Primary700,    // Primary 색상: 좀 더 어두운 색상
    secondary = Primary300,  // Secondary 색상: 연한 보라색
    tertiary = Grayscale300   // Tertiary 색상: 그레이 중간 색상
)

@Composable
fun AntiPhishingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true   // ⭐ 검정색 아이콘으로 설정

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent, // 상태바 배경 색 → 투명 유지
            darkIcons = useDarkIcons   // 아이콘을 검정색으로!
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
