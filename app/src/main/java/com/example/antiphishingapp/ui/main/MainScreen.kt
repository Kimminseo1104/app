package com.example.antiphishingapp.ui.main

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.antiphishingapp.feature.viewmodel.AnalysisViewModel
import com.example.antiphishingapp.utils.bitmapToMultipart
import java.io.InputStream
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun MainScreen(
    navController: NavController,
    onAnalysisComplete: (com.example.antiphishingapp.feature.model.AnalysisResponse) -> Unit,
    viewModel: AnalysisViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.observeAsState(false)
    val result by viewModel.result.observeAsState()
    val error by viewModel.error.observeAsState()

    // ✅ 이미지 선택 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                if (inputStream == null) {
                    Toast.makeText(context, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
                    return@let
                }

                val bitmap = BitmapFactory.decodeStream(inputStream)
                val part = bitmapToMultipart(bitmap)
                viewModel.analyzeDocument(part)
            } catch (e: Exception) {
                Toast.makeText(context, "이미지 처리 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ 분석 완료 시 결과 화면으로 이동
    LaunchedEffect(result) {
        result?.let {
            Toast.makeText(context, "분석 완료!", Toast.LENGTH_SHORT).show()
            onAnalysisComplete(it)
            navController.navigate("analysis")
            viewModel.resetResult()
        }
    }

    // ✅ UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("이미지 분석 중...")
            }
            error != null -> {
                Text("❌ 오류: $error", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.resetResult() }) {
                    Text("다시 시도하기")
                }
            }
            else -> {
                // ✅ 기존 문서 분석 기능
                Text("📸 스미싱 의심 문서를 선택하세요", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("이미지 선택하기")
                }

                // ✅ 새로 추가된 실시간 탐지 기능
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("realtime") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("🎙 실시간 보이스피싱 탐지")
                }
            }
        }
    }
}
