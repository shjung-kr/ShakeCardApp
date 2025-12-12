package com.proofence.shakecardapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 포그라운드 서비스 시작(항상 흔들기 감지)
        startService(Intent(this, ShakeService::class.java))
        ContextCompat.startForegroundService(this, Intent(this, ShakeService::class.java))
        // 앱 화면은 필요 없으니 바로 종료 (서비스는 계속 살아있음)
        finish()
    }
}



