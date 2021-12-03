package com.depromeet.sloth.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.depromeet.sloth.databinding.ActivitySlothPolicyWebViewBinding

class SlothPolicyWebViewActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SlothPolicyWebViewActivity::class.java)

        private const val DEFAULT_URL = "file:///android_asset/sloth_policy.html"
    }

    lateinit var binding: ActivitySlothPolicyWebViewBinding
    lateinit var source: String
    lateinit var content: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlothPolicyWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.tbSlothPolicy.setNavigationOnClickListener { finish() }

        binding.wvWebView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }
}