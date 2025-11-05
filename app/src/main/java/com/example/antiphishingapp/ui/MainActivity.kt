package com.example.antiphishingapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.antiphishingapp.theme.AntiPhishingAppTheme
import com.example.antiphishingapp.ui.navigation.AppNavGraph
import com.example.antiphishingapp.utils.NotificationHelper
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "이미지 접근 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV 초기화 실패")
        }

        // 문자 관련 권한 요청
        checkSmsPermission()

        // 이미지 권한 요청
        checkImagePermission()

        // 알림 권한 요청 (Android 13+)
        checkNotificationPermission()

        // 알림 채널 생성 (SmsReceiver에서 Notification 사용 가능하게)
        NotificationHelper.createChannel(this)

        setContent {
            AntiPhishingAppTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(navController)
                }
            }
        }
    }

    private fun checkImagePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("PERMISSION", "이미지 접근 권한 허용됨")
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    // 문자 권한 요청 함수
    private fun checkSmsPermission() {
        val smsPermissions = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        val notGranted = smsPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), 101)
        } else {
            Log.d("PERMISSION", "문자 관련 권한 이미 허용됨")
        }
    }

    // 알림 권한 요청 함수
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 102)
            } else {
                Log.d("PERMISSION", "알림 권한 이미 허용됨")
            }
        }
    }

}