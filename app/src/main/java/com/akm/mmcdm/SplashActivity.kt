package com.akm.mmcdm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity(), WebViewListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preLoadWebView()
    }

    private fun preLoadWebView() {
        val handler = Handler(Looper.getMainLooper())
        handler.post{
//            PreCache.getInstance(baseContext,this).cacheUrl(MainActivity.cdm_host_url)
            CacheManager.getInstance(baseContext,this).cacheUrl(MainActivity.cdm_host_url)
        }
    }

    override fun loadFinished() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}