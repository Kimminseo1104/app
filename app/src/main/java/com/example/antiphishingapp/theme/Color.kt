package com.example.antiphishingapp.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Updated Primary Color Set
val Primary100 = Color(0xFFFCFBFF) // 배경색
val Primary200 = Color(0xFFF0EDFE)
val Primary300 = Color(0xFFE6E2FF) // 비활성 버튼용 제일 연한 색상
val Primary600 = Color(0xFFD7D0FC)
val Primary700 = Color(0xFFBEB3FA)
val Primary800 = Color(0xFF7971FE)
val Primary900 = Color(0xFF5A40F3) // 강조 및 활성 버튼용 제일 진한 색상


// Grayscale Color Set
val Grayscale900 = Color(0xFF2C2C2C)
val Grayscale800 = Color(0xFF343A40)
val Grayscale700 = Color(0xFF495057)
val Grayscale600 = Color(0xFF868E96)
val Grayscale500 = Color(0xFFADB5BD)  // Default grayscale
val Grayscale400 = Color(0xFFCED4DA)
val Grayscale300 = Color(0xFFDEE2E6)
val Grayscale200 = Color(0xFFE9ECEF)
val Grayscale100 = Color(0xFFF1F3F5)
val Grayscale50  = Color(0xFFF8F9FA)
val Grayscale30  = Color(0xFFFFFFFF)

// Gradient A Colors (Red → Blue)
val GradientA_Start = Color(0xFFF13842)  // 0%
val GradientA_End = Color(0xFF3A94F3)    // 100%

// GradientBrush for A
val GradientA_Brush = Brush.horizontalGradient(
    colors = listOf(
        GradientA_Start,
        GradientA_End
    )
)

// Gradient B Colors (Red → Yellow → Green)
val GradientB_Start = Color(0xFFF13842)   // 0%
val GradientB_Mid = Color(0xFFFFBD2D)     // 50%
val GradientB_End = Color(0xFF2AC269)     // 100%

// GradientBrush for B
val GradientB_Brush = Brush.horizontalGradient(
    colorStops = arrayOf(
        0.0f to GradientB_Start,
        0.5f to GradientB_Mid,
        1.0f to GradientB_End
    )
)

