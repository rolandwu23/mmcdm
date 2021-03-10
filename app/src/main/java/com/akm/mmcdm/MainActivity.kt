package com.akm.mmcdm

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    companion object {
        const val cdm_host_url = "https://mmcdm.info/"
    }

    private val facebookHostUrl = "www.facebook.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WebView.setWebContentsDebuggingEnabled(true)

        webView.settings.javaScriptEnabled = true

        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        webView.webViewClient = object: WebViewClient() {


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val uri = request?.url
                val facebook_url = Util.getFacebookUrl(this@MainActivity,uri.toString())
                if(uri?.host.equals(facebookHostUrl)){
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(facebook_url)
                    )
                    startActivity(intent)
                    return true
                }

                return false
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }
        }

        webView.loadUrl(cdm_host_url)

    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
            return
        }
        super.onBackPressed()
    }


}
