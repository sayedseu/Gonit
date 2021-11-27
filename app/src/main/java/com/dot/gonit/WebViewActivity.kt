package com.dot.gonit

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dot.gonit.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    companion object {
        private const val PRIVACY_POLICY_URL = "https://technoven.de/gonit/privacy_policy.html"
    }

    private lateinit var binding: ActivityWebViewBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.webToolBar)
        supportActionBar?.setTitle(R.string.policy)
        binding.webToolBar.setNavigationIcon(R.drawable.ic_back)
        binding.webToolBar.setNavigationOnClickListener { finish() }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.let { webView ->
                    url?.let { webView.loadUrl(it) }
                }
                return true
            }
        }
        binding.webView.loadUrl(PRIVACY_POLICY_URL)

    }
}