package com.akm.mmcdm

import android.content.Context
import android.net.http.SslError
import android.util.Log
import android.webkit.*
import java.util.*

class CacheManager private constructor(context: Context,listener: WebViewListener){

    private val webView = WebView(context)
    var isLoading = false
    val queue : Queue<String>
    var updateCache = false
    var webViewListener : WebViewListener = listener

    init {
        queue = LinkedList()
        setUpWebView()
    }

    companion object : SingletonHolder<CacheManager,Context,WebViewListener>(::CacheManager)

    private fun setUpWebView() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                isLoading = true
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                view?.scrollTo(0,0)
                Log.e("Precache","Loading complete for: $url")
                isLoading = false
                if(!queue.isEmpty()){
                    cacheUrl(queue.remove())
                }else{
                    Log.e("PreCache","Loading done")
                    webViewListener.loadFinished()
                }
            }
        }
    }


    fun cacheUrl(url : String) {

        if(updateCache) webView.settings.cacheMode =
            WebSettings.LOAD_NO_CACHE else webView.settings.cacheMode =
            WebSettings.LOAD_CACHE_ELSE_NETWORK
        if(!isLoading) {
            webView.loadUrl(url)
        }else{
            queue.add(url)
        }
    }

    fun forceUpdateCache(updateCache: Boolean) : PreCache? {
        this.updateCache = updateCache
        return PreCache.preCache
    }
}