package com.proofence.shakecardapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.ComponentActivity
import android.media.MediaPlayer

class MainActivity : ComponentActivity() {

    companion object {
        private const val CARD_URL = "https://shjung-kr.github.io/my-card" // ✅ 여기가 명함 주소
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MediaPlayer.create(this, R.raw.card_slide)?.start()

        val webView: WebView = findViewById(R.id.webView)
        val btnExit: Button = findViewById(R.id.btnExit)
        val btnOpenBrowser: Button = findViewById(R.id.btnOpenBrowser)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(CARD_URL)

        btnExit.setOnClickListener {
            finish()
        }

        btnOpenBrowser.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CARD_URL)))
        }
    }
}
