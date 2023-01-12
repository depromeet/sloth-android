package com.depromeet.sloth.presentation.screen.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentSlothPolicyWebViewBinding
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SlothPolicyWebViewFragment :
    BaseFragment<FragmentSlothPolicyWebViewBinding>(R.layout.fragment_sloth_policy_web_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        binding.wvWebView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            // 자바 스크립트 허용
            settings.javaScriptEnabled = true
            // 로컬 스토리지 허용
            settings.domStorageEnabled = true
            loadUrl(BuildConfig.POLICY_WEB_VIEW_URL)
        }
    }

    private fun initListener() {
        binding.tbSlothPolicy.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            binding.pbSlothPolicyContentLoading.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            binding.pbSlothPolicyContentLoading.hide()
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.pbSlothPolicyContentLoading.progress = newProgress
        }
    }

    // WebView 를 Fragment 에서 사용시 destroy 처리 필요
    override fun onDestroyView() {
        binding.wvWebView.destroy()
        super.onDestroyView()
    }
}