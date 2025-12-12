package com.proofence.shakecardapp

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.os.Build

class ShakeService : Service() {

    companion object {
        private const val NOTI_ID = 1001
        private const val GITHUB_URL = "https://shjung-kr.github.io/my-card" // ✅ 여기 바꿔!
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var shakeDetector: ShakeDetector

    // "브라우저 화면이 열려있다" 상태
    @Volatile private var webOpen = false

    private val webClosedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WebViewActivity.ACTION_WEBVIEW_CLOSED) {
                webOpen = false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Foreground 알림
        NotificationUtil.ensureChannel(this)
        startForeground(NOTI_ID, NotificationUtil.buildServiceNotification(this))

        // WebView 닫힘 이벤트 수신
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(webClosedReceiver, IntentFilter(WebViewActivity.ACTION_WEBVIEW_CLOSED), RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(webClosedReceiver, IntentFilter(WebViewActivity.ACTION_WEBVIEW_CLOSED))
        }

        // 센서 준비
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        shakeDetector = ShakeDetector(
            onShake = { toggleWeb() },
            thresholdG = 2.3f,
            cooldownMs = 900L
        )

        accelerometer?.let { sensor ->
            sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun toggleWeb() {
        if (!webOpen) {
            // 열기
            val i = Intent(this, WebViewActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(WebViewActivity.EXTRA_URL, GITHUB_URL)
            }
            startActivity(i)
            webOpen = true
        } else {
            // 닫기 (WebViewActivity에 finish() 요청)
            sendBroadcast(Intent(WebViewActivity.ACTION_CLOSE_WEBVIEW).setPackage(packageName))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { sensorManager.unregisterListener(shakeDetector) } catch (_: Exception) {}
        try { unregisterReceiver(webClosedReceiver) } catch (_: Exception) {}
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
