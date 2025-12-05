package com.example.antiphishingapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.antiphishingapp.theme.*
import com.example.antiphishingapp.feature.model.SuspiciousItem

@Composable
fun VoiceUploadResultScreen(
    navController: NavController,
    riskScore: Int,
    suspiciousItems: List<SuspiciousItem>,
    transcript: String
) {
    val scrollState = rememberScrollState()

    val scoreColor = calculateVoiceScoreColor(riskScore)

    val (resultText, descriptionText) = when (riskScore) {
        in 70..100 -> "보이스피싱 확률이 높습니다." to "범죄 의도가 있을 가능성이 높아요."
        in 45..69 -> "보이스피싱 확률이 있습니다." to "주의해서 확인해주세요."
        else -> "보이스피싱 확률이 낮습니다." to "주요 의심 징후는 보이지 않습니다."
    }

    Scaffold(
        containerColor = Primary100
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 1) 상단 위험도 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "보이스피싱 위험도",
                    style = MaterialTheme.typography.titleSmall,
                    color = Grayscale600,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$riskScore%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    Column {
                        Text(
                            text = resultText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Grayscale900
                            )
                        )

                        Text(
                            text = descriptionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Grayscale900
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // 2) transcript 영역
            if (transcript.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "통화 내용",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Grayscale600,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = transcript,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Grayscale900,
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Grayscale50)
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 3) 의심 항목 리스트
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {

                Text(
                    text = "보이스피싱 의심 항목",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Grayscale600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                VoiceSuspiciousItemsBox(items = suspiciousItems)

                Spacer(modifier = Modifier.height(32.dp))
            }

            // 4) 다시 시도하기
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "다른 녹음 파일로 다시 시도해볼까요?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Grayscale500,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


// 위험도 색상 계산
@Composable
fun calculateVoiceScoreColor(score: Int): Color {
    val startColor = Color(0xFF2AC269) // Green
    val midColor = Color(0xFFFFBD2D)   // Yellow
    val endColor = Color(0xFFF13842)   // Red

    return when {
        score <= 50 -> lerp(startColor, midColor, score / 50f)
        else -> lerp(midColor, endColor, (score - 50) / 50f)
    }
}

// 의심 항목 리스트 박스 UI
@Composable
fun VoiceSuspiciousItemsBox(items: List<SuspiciousItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Grayscale50)
            .padding(vertical = 16.dp)
    ) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 11.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFFF13842),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Grayscale900,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 1.dp)
                )
            }

            if (index < items.size - 1) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (items.isEmpty()) {
            Text(
                text = "탐지된 의심 항목이 없습니다.",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Grayscale500
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun VoiceUploadResultScreenPreview() {
    VoiceUploadResultScreen(
        navController = rememberNavController(),
        riskScore = 75,
        suspiciousItems = listOf(
            SuspiciousItem("금융 기관 사칭 정황이 감지되었습니다."),
            SuspiciousItem("개인정보 요구 내용이 있습니다.")
        ),
        transcript = "여보세요. 안녕하세요 고객님. 금융감독원입니다..."
    )
}
