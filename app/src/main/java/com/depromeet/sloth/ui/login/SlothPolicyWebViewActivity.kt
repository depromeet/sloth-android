package com.depromeet.sloth.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivitySlothPolicyWebViewBinding
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlothPolicyWebViewActivity :
    BaseActivity<ActivitySlothPolicyWebViewBinding>(R.layout.activity_sloth_policy_web_view) {

    companion object {
        fun newIntent(context: Context) = Intent(context, SlothPolicyWebViewActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() = with(binding) {
        tbSlothPolicy.setNavigationOnClickListener { finish() }

        wvWebView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(BuildConfig.POLICY_WEB_VIEW_URL)
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            binding.pbSlothPolicyContentLoading.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            with(binding) {
                pbSlothPolicyContentLoading.hide()
                etSlothPolicyAddressBar.setText(BuildConfig.POLICY_WEB_VIEW_URL)
            }
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            binding.pbSlothPolicyContentLoading.progress = newProgress //DEFAULT
        }
    }
}