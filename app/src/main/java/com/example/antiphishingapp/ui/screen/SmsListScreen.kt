package com.example.antiphishingapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.antiphishingapp.R
import com.example.antiphishingapp.theme.*

data class SmsMessage(
    val sender: String,
    val content: String,
    val time: String,
    val isRisky: Boolean
)

private val phishingKeywords = listOf(
    "안전계좌","보안계좌","현금 전달","대포통장","계좌 이체","송금 요청",
    "개인정보 확인","비밀번호 입력","인증번호 전송","송금","이체","입금","구속","형사처벌","압류","고소","체포",
    "영장","계좌번호","비밀번호","인증번호","OTP","보안카드","검찰","검찰청","경찰","경찰청","금융감독원","금감원",
    "법원","국세청","관세청","우체국","은행","카드사","통신사","긴급","즉시","24시간 이내","오늘 중","피의자",
    "명의 도용","개인정보 유출","사건 번호","출석요구서","소환장","전화 주세요","연락 바랍니다","클릭","링크",
    "앱 설치","프로그램 설치","대출","저금리","신용","한도","승인","연체","채무","미납","미수","정지","해지",
    "택배","배송","상품권","쿠폰","당첨","경품","무료","계좌","벌금","확인요망","확인하세요","조회","인증"
)

private val rawMessages = listOf(   //스크롤 확인용
    Triple("국제 발신", "결제 완료. 미결제시 즉시 문의. 현금 전달 바랍니다.", "PM 10:30"),
    Triple("은행", "의심스러운 계좌 이체가 감지되었습니다.", "PM 08:30"),
    Triple("엄석대", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "계좌번호 알려줘 밥 사줘 대출 저금리 신용 한도", "AM 00:30"),
    Triple("김철수", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "계좌번호 알려줘 밥 사줘 대출 저금리 신용 한도", "AM 00:30"),
    Triple("김철필", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "계좌번호 알려줘 밥 사줘 대출 저금리 신용 한도", "AM 00:30"),
    Triple("에도가와 코난", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "범인 잡았어 살인사건이야 대출 저금리 신용 한도", "AM 00:30"),
    Triple("마동탁", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "계좌번호 알려줘 밥 사줘 대출 저금리 신용 한도", "AM 00:30"),
    Triple("함필규", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "계좌번호 알려줘 밥 사줘 대출 저금리 신용 한도", "AM 00:30"),
    Triple("카마도 탄지로", "오늘 저녁에 뭐해? 시간 괜찮으면 긴급 보자.\n" +
            "계좌번호 알려줘 무잔 대출 저금리 신용 한도", "AM 00:30")
)

private val sampleSmsMessages = rawMessages.map { (sender, content, time) ->
    SmsMessage(
        sender = sender,
        content = content,
        time = time,
        isRisky = phishingKeywords.any { content.contains(it) }
    )
}

@Composable
fun SmsListScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary100)
            .padding(start = 24.dp, end = 27.dp)
    ) {
        Spacer(modifier = Modifier.height(67.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(20.dp))
        FilterBar()
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleSmsMessages) { message ->
                SmsCard(message = message)
            }
        }
    }
}

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        singleLine = true,
        textStyle = AppTypography.bodyLarge.copy(color = Primary800),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .background(
                        color = Primary200,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mag),
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Primary800)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = "원하는 내역을 검색하세요.",
                            style = AppTypography.bodyLarge,
                            color = Primary800
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun FilterBar() {
    val filterTextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(id = R.drawable.filter),
            contentDescription = "Filter Icon",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(Grayscale900)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "필터",
            style = filterTextStyle,
            color = Color(0xFF757575)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최신순",
                style = filterTextStyle,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(id = R.drawable.poly),
                contentDescription = "Sort order",
                modifier = Modifier.size(width = 9.dp, height = 6.dp),
                colorFilter = ColorFilter.tint(Color(0xFFD9D9D9))
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun SmsCard(message: SmsMessage) {
    val cardTextStyle = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Grayscale100,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.pic01),
            contentDescription = "Message Icon",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(Primary900)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.sender,
                    style = cardTextStyle,
                    color = Grayscale900
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = message.time,
                    style = cardTextStyle,
                    color = Grayscale900,
                    textAlign = TextAlign.Right
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            HighlightedText(
                text = message.content,
                isRisky = message.isRisky,
                style = cardTextStyle.copy(color = Grayscale900)
            )
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    isRisky: Boolean,
    style: TextStyle,
) {
    if (!isRisky) {
        Text(text = text, style = style)
        return
    }

    val annotatedString = buildAnnotatedString {
        val regex = Regex(phishingKeywords.joinToString("|"))
        val matches = regex.findAll(text)
        var lastIndex = 0

        for (match in matches) {
            append(text.substring(lastIndex, match.range.first))
            withStyle(style = SpanStyle(color = Primary900)) {
                append(match.value)
            }
            lastIndex = match.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    Text(text = annotatedString, style = style)
}

@Preview(showBackground = true)
@Composable
fun SmsListScreenPreview() {
    AntiPhishingAppTheme {
        SmsListScreen()
    }
}