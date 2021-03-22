package com.akm.mmcdm

import android.content.Intent
import android.net.Uri
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*


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
                val facebookUrl = Util.getFacebookUrl(this@MainActivity, uri.toString())
                if(uri?.host.equals(facebookHostUrl)){
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(facebookUrl)
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

                val serverCertificate = error?.certificate
                val serverBundle = SslCertificate.saveState(serverCertificate)
                val appCertificate = loadSSLCertificate()
                if (TextUtils.equals(serverCertificate.toString(), appCertificate.toString())) { // First fast check
                    val appBundle = SslCertificate.saveState(appCertificate)
                    val keySet = appBundle.keySet()
                    var matches = true
                    for (key in keySet) {
                        val serverObj = serverBundle[key]
                        val appObj = appBundle[key]
                        if (serverObj is ByteArray && appObj is ByteArray) {     // key "x509-certificate"
                            if (!Arrays.equals(serverObj as ByteArray?, appObj as ByteArray?)) {
                                matches = false
                                break
                            }
                        } else if (serverObj != null && serverObj != appObj) {
                            matches = false
                            break
                        }
                    }
                    if (matches) {
                        handler?.proceed()
                        return
                    }
                }

                val message = when(error?.primaryError){
                    SslError.SSL_INVALID -> R.string.error_message_invalid_ssl_cert
                    SslError.SSL_DATE_INVALID -> R.string.error_message_date_invalid_ssl_cert
                    SslError.SSL_NOTYETVALID -> R.string.error_message_not_yet_valid_ssl_cert
                    SslError.SSL_EXPIRED -> R.string.error_message_expired_ssl_cert
                    SslError.SSL_IDMISMATCH -> R.string.error_message_id_mismatch_ssl_cert
                    SslError.SSL_UNTRUSTED -> R.string.error_message_untrusted_ssl_cert
                    else -> R.string.error_message_invalid_ssl_cert
                }
                val alertDialog = AlertDialog.Builder(this@MainActivity).apply {
                    setMessage(message)
                    setPositiveButton(
                        R.string.positive_btn
                    ) { _, _ -> handler?.proceed()}
                    setNegativeButton(
                        R.string.negative_btn
                    ) { _, _ -> handler?.cancel()}
                }.create()

                alertDialog.show()
            }
        }

        webView.loadUrl(cdm_host_url)

    }

    private fun loadSSLCertificate() : SslCertificate? {
        try {
            val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")

                val inputStream: InputStream = resources.openRawResource(R.raw.mmcdm_info)
                val certificateInput: InputStream = BufferedInputStream(inputStream)
                try {
                    val certificate: Certificate =
                        certificateFactory.generateCertificate(certificateInput)
                    if (certificate is X509Certificate) {
                        val x509Certificate: X509Certificate = certificate
                        return SslCertificate(x509Certificate)
                    } else {
                        Log.e("MainActivity", "Wrong Certificate format")
                        return null
                    }
                } catch (exception: CertificateException) {
                    Log.e("MainActivity", "Cannot read certificate")
                } finally {
                    try {
                        certificateInput.close()
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return null
                    }
                }

        } catch (e: CertificateException) {
            e.printStackTrace()
            return null
        }
        return null
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
            return
        }
        super.onBackPressed()
    }


}
