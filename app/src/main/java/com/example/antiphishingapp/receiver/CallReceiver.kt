package com.example.antiphishingapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.antiphishingapp.feature.realtime.RealtimeCallService
import com.example.antiphishingapp.ui.AlertActivity

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        Log.d("CallReceiver", "📡 onReceive: state=$state")

        when (state) {

            // 🔔 전화 울림 감지 (사용자가 받기 전)
            TelephonyManager.EXTRA_STATE_RINGING -> {
                Log.d("CallReceiver", "📳 전화 울리는 중 — AlertActivity 실행")

                val popupIntent = Intent(context, AlertActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("type", "call")  // 전화 알림
                }
                context.startActivity(popupIntent)
            }

            // 📞 통화 연결됨 (사용자가 받음)
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d("CallReceiver", "📞 통화 연결됨 — STT 서비스 시작")

                // 서비스 시작
                val serviceIntent = Intent(context, RealtimeCallService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }

            // 📴 통화 종료됨
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.d("CallReceiver", "📴 통화 종료됨 — 서비스 종료")
                val stopIntent = Intent(context, RealtimeCallService::class.java)
                context.stopService(stopIntent)
            }
        }
    }
}
