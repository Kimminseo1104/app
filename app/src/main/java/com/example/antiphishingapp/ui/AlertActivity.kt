package com.example.antiphishingapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.antiphishingapp.ui.components.MessageAlertCard
import com.example.antiphishingapp.ui.components.PhoneAlertCard
import com.example.antiphishingapp.theme.AntiPhishingAppTheme

class AlertActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 문자 / 전화 타입 가져오기 (default = sms)
        val alertType = intent.getStringExtra("type") ?: "sms"

        setContent {
            AntiPhishingAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {

                    when (alertType) {

                        // 🔹 문자 알림
                        "sms" -> {
                            MessageAlertCard(
                                onCheckKeyword = {
                                    openDeepLink("myapp://sms_list")
                                },
                                onDismiss = { finish() }
                            )
                        }

                        // 🔹 전화 알림
                        "call" -> {
                            PhoneAlertCard(
                                onStartDetect = {
                                    openDeepLink("myapp://call_list")
                                },
                                onDismiss = { finish() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openDeepLink(uri: String) {
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        startActivity(deepLinkIntent)
        finish()
    }
}