package com.depromeet.sloth.ui.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentSlothPolicyWebViewBinding
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlothPolicyWebViewFragment :
    BaseFragment<FragmentSlothPolicyWebViewBinding>(R.layout.fragment_sloth_policy_web_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListener()
    }

    private fun initListener() {
        binding.tbSlothPolicy.setNavigationOnClickListener {
            val action = SlothPolicyWebViewFragmentDirections.actionSlothPolicyWebviewToManage()
            findNavController().safeNavigate(action)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        binding.wvWebView.apply {
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

    // WebView 를 Fragment 에서 사용시 destroy 처리 필요
    override fun onDestroyView() {
        binding.wvWebView.destroy()
        super.onDestroyView()
    }
}