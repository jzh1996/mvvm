package com.jzh.mvvm.webView

import android.content.Context
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.jzh.mvvm.utils.StringUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.util.regex.Pattern

/**
 * 参考文章：https://mp.weixin.qq.com/s/gs2bojFLBB4IAWMyN9lfnw
 * Created by jzh on 2020-12-29.
 */
class JianShuWebClient : BaseWebClient() {

    private val rex = "(<style data-vue-ssr-id=[\\s\\S]*?>)([\\s\\S]*]?)(<\\/style>)"

    private val bodyRex = "<body class=\"([\\ss\\S]*?)\""

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val urlStr = url ?: ""
        if (urlStr.startsWith(WebViewActivity.JIAN_SHU)) {
            val response = mGet(url ?: "")
            val res = darkBody(replaceCss(response, view!!.context))
            val input = ByteArrayInputStream(res.toByteArray())
            return WebResourceResponse("text/html", "utf-8", input)
        }
        return super.shouldInterceptRequest(view, url)
    }

    private fun darkBody(res: String): String {
        val pattern = Pattern.compile(bodyRex)
        val m = pattern.matcher(res)
        return res
        //return if (m.find()) {
        //    val s = "<body class=\"reader-night-mode normal-size\""
        //    res.replace(bodyRex.toRegex(), s)
        //} else res
    }

    private fun replaceCss(res: String, context: Context): String {
        val pattern = Pattern.compile(rex)
        val m = pattern.matcher(res)
        return if (m.find()) {
            val css = StringUtil.getString(context.assets.open("jianshu/jianshu.css"))
            val sb = StringBuilder()
            sb.append(m.group(1))
            sb.append(css)
            sb.append(m.group(3))
            res.replace(rex.toRegex(), sb.toString())
        } else {
            res
        }
    }

    fun mGet(url: String): String {
        val client = OkHttpClient.Builder()
            .build()
        val request = Request.Builder()
            .url(url)
            .header(
                "user-agent",
                "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3887.7 Mobile Safari/537.36"
            )
            .build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

}