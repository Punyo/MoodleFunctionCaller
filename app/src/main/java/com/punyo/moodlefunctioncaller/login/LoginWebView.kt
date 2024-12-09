package com.punyo.moodlefunctioncaller.login

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Base64

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginWebView(
    modifier: Modifier = Modifier,
    onTokenReceived: (String) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                val passport = Math.random() * 1000
                settings.javaScriptEnabled = true
                webViewClient = LoginWebViewClient(onTokenReceived)
                loadUrl("https://cms7.ict.nitech.ac.jp/moodle40a/admin/tool/mobile/launch.php?service=moodle_mobile_app&passport=$passport&urlscheme=moodlemobile")
            }
        }
    )
}

class LoginWebViewClient(private val onTokenReceived: (String) -> Unit) :
    WebViewClient() {
    private val moodleMobileScheme = "moodlemobile"

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url
        val regex = Regex("token=([^&]+)")
        val matchResult = regex.find(url.toString())
        if (url?.scheme == moodleMobileScheme && matchResult != null) {
            val decodedString =
                Base64.getDecoder().decode(matchResult.groupValues.first().replace("token=", ""))
                    .toString(Charsets.UTF_8)
            val accessToken = decodedString.split(":::")[1]
            onTokenReceived(accessToken)
            return false
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}