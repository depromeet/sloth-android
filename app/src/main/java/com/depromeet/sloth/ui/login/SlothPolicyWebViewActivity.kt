package com.depromeet.sloth.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import com.depromeet.sloth.databinding.ActivitySlothPolicyWebViewBinding

class SlothPolicyWebViewActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SlothPolicyWebViewActivity::class.java)

        private const val DEFAULT_URL = "https://docs.google.com/document/d/1iggsm5M-Y9QzQLMWO6lA73_AzMyLpIo_FVqAl5HTcYM/edit?usp=sharing"
    }

    lateinit var binding: ActivitySlothPolicyWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlothPolicyWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        bindViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        binding.wvWebView.apply{
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }

    }

    private fun bindViews() {

        binding.etWebViewAddressBar.setOnEditorActionListener { view, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                /*ttps를 입력을 안해도 자동으로 붙혀주도록*/
                val loadingUrl = view.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)){
                    /*true -> 앞에 http 또는 https 가 붙어있는 것*/
                    binding.wvWebView.loadUrl(loadingUrl)
                } else {
                    binding.wvWebView.loadUrl("http://$loadingUrl")
                }

                binding.wvWebView.loadUrl(view.text.toString())
            }

            /*키보드를 내리기 위함*/
            return@setOnEditorActionListener false
        }

        with(binding) {
            rlWebView.setOnRefreshListener {
                wvWebView.reload()
            }
            ivWebViewClose.setOnClickListener {
                finish()
            }
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.pbWebView.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            with(binding) {
                rlWebView.isRefreshing = false
                pbWebView.hide()

                etWebViewAddressBar.setText(url)
            }
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            binding.pbWebView.progress = newProgress //DEFAULT
        }
    }

    override fun onBackPressed() {
        /*웹뷰 내에서 뒤로 갈 수 있는지를 확인*/
        if (binding.wvWebView.canGoBack()) {
            binding.wvWebView.goBack()
        } else {
            /*웹뷰 내에 뒤로 갈 수 있는 페이지가 없으면, 앱 뒤로가기*/
            super.onBackPressed()
        }
    }

}