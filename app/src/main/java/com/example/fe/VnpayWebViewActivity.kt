package com.example.fe

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class VnpayWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vnpay_webview)

        val url = intent.getStringExtra("VNPAY_URL") ?: ""
        
        val toolbar = findViewById<Toolbar>(R.id.toolbarVnpay)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { 
            setResult(RESULT_CANCELED)
            finish() 
        }

        webView = findViewById(R.id.webViewVnpay)
        progressBar = findViewById(R.id.progressBarVnpay)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                
                // Senior Logic: Phát hiện URL Return nhưng cho phép WebView load để Backend xử lý
                if (url != null && url.contains("vnpay_return")) {
                    if (url.contains("vnp_ResponseCode=00")) {
                        setResult(RESULT_OK)
                    } else {
                        setResult(RESULT_CANCELED)
                    }
                    
                    // Delay một chút để Server có thời gian nhận request và xử lý DB
                    webView.postDelayed({
                        if (!isFinishing) finish()
                    }, 2000) 
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val currentUrl = request?.url.toString()
                // Cho phép load các URL bình thường, bao gồm cả return URL
                return false 
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        })

        webView.loadUrl(url)
    }
}
