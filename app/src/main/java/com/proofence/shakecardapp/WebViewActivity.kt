package com.proofence.shakecardapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import android.os.Build
class WebViewActivity : ComponentActivity() {

    companion object {
        const val ACTION_CLOSE_WEBVIEW = "com.proofence.shakecardapp.ACTION_CLOSE_WEBVIEW"
        const val ACTION_WEBVIEW_CLOSED = "com.proofence.shakecardapp.ACTION_WEBVIEW_CLOSED"
        const val EXTRA_URL = "extra_url"
    }

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CLOSE_WEBVIEW) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(EXTRA_URL) ?: "https://github.com"

        val webView = WebView(this)
        setContentView(webView)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(closeReceiver, IntentFilter(ACTION_CLOSE_WEBVIEW), RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(closeReceiver, IntentFilter(ACTION_CLOSE_WEBVIEW))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(closeReceiver) } catch (_: Exception) {}

        // 서비스에게 "브라우저 화면 닫힘" 알림
        sendBroadcast(Intent(ACTION_WEBVIEW_CLOSED).setPackage(packageName))
    }
}
