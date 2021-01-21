package com.jzh.mvvm.webView

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebView
import com.just.agentweb.WebViewClient
import com.jzh.mvvm.R
import com.jzh.mvvm.base.BaseActivity
import com.jzh.mvvm.constant.Constant
import com.jzh.mvvm.utils.SettingUtil
import com.jzh.mvvm.utils.getAgentWebView
import com.jzh.mvvm.utils.toast
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * https://github.com/Justson/AgentWeb
 * Created by jzh on 2020-12-28.
 */
class WebViewActivity : BaseActivity() {

    private var mAgentWeb: AgentWeb? = null
    private lateinit var mWebClient: WebViewClient
    private var shareTitle: String = ""
    private var shareUrl: String = ""
    private var shareId: Int = -1

    companion object {

        const val JIAN_SHU = "https://www.jianshu.com"

        fun start(context: Context?, id: Int, title: String, url: String, bundle: Bundle? = null) {
            Intent(context, WebViewActivity::class.java).run {
                putExtra(Constant.CONTENT_ID_KEY, id)
                putExtra(Constant.CONTENT_TITLE_KEY, title)
                putExtra(Constant.CONTENT_URL_KEY, url)
                context?.startActivity(this, bundle)
            }
        }

        fun start(context: Context?, url: String) {
            start(context, -1, "", url)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_web_view

    override fun initData() {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        intent.extras?.let {
            shareId = it.getInt(Constant.CONTENT_ID_KEY, -1)
            shareTitle = it.getString(Constant.CONTENT_TITLE_KEY, "")
            shareUrl = it.getString(Constant.CONTENT_URL_KEY, "")
        }
        setTop(shareTitle, R.drawable.points)
        toolbar_subtitle_image.setOnClickListener {
            toast("分享")
        }
        initWebView()
    }

    private fun initWebView() {
        val webView = AgentWebView(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -1)
        mWebClient = if (shareUrl.startsWith(JIAN_SHU)) JianShuWebClient() else BaseWebClient()
        mAgentWeb = getAgentWebView(
            shareUrl, this, ll_webView, layoutParams,
            webView,
            mWebClient,
            mWebChromeClient,
            SettingUtil.getColor()
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AgentWebView.setWebContentsDebuggingEnabled(true)
        }
        mAgentWeb?.webCreator?.webView?.apply {
            overScrollMode = WebView.OVER_SCROLL_NEVER
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }

    private val mWebChromeClient = object : com.just.agentweb.WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            toolbar_title.text = title
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

    override fun onBackPressed() {
        mAgentWeb?.let {
            if (!it.back()) {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (mAgentWeb?.handleKeyEvent(keyCode, event)!!) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

    override fun startHttp() {}
}