package com.depromeet.sloth.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.depromeet.sloth.databinding.ActivitySlothPolicyWebViewBinding

class SlothPolicyWebViewActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SlothPolicyWebViewActivity::class.java)

        private const val DEFAULT_URL = "https://yonezu-kenshi-3068.notion.site/c9edcf0b426941b4844a196407c0cc06"
    }

    lateinit var binding: ActivitySlothPolicyWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlothPolicyWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() = with(binding) {
        wvWebView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            binding.pbSlothPolicyContentLoading.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            with(binding) {
                pbSlothPolicyContentLoading.hide()
                etSlothPolicyAddressBar.setText(DEFAULT_URL)
            }
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            binding.pbSlothPolicyContentLoading.progress = newProgress //DEFAULT
        }
    }

}