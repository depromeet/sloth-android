package com.depromeet.sloth.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.depromeet.sloth.R


class SlothPolicyWebViewFragment : Fragment() {

    companion object {
        private const val DEFAULT_URL = "https://docs.google.com/document/d/1iggsm5M-Y9QzQLMWO6lA73_AzMyLpIo_FVqAl5HTcYM/edit?usp=sharing"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sloth_policy_web_view, container, false)
        val wvSlothPolicy = view.findViewById<WebView>(R.id.wv_sloth_policy)
        val etWebViewAddressBar = view.findViewById<EditText>(R.id.et_web_view_address_bar)
        val srlWebView = view.findViewById<SwipeRefreshLayout>(R.id.srl_web_view)
        val ivWebViewClose = view.findViewById<ImageView>(R.id.iv_web_view_close)

        wvSlothPolicy.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }

        etWebViewAddressBar.setOnEditorActionListener { view, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                /*ttps를 입력을 안해도 자동으로 붙혀주도록*/
                val loadingUrl = view.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)){
                    /*true -> 앞에 http 또는 https 가 붙어있는 것*/
                    wvSlothPolicy.loadUrl(loadingUrl)
                } else {
                    wvSlothPolicy.loadUrl("http://$loadingUrl")
                }

                wvSlothPolicy.loadUrl(view.text.toString())
            }

            /*키보드를 내리기 위함*/
            return@setOnEditorActionListener false
        }


        srlWebView.setOnRefreshListener {
            wvSlothPolicy.reload()
        }
        ivWebViewClose.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        // Inflate the layout for this fragment
        return view
    }
}