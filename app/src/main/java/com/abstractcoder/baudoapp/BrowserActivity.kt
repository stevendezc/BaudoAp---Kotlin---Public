package com.pereira.baudoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.pereira.baudoapp.databinding.ActivityBrowserBinding
import com.pereira.baudoapp.utils.CheckoutData
import com.pereira.baudoapp.utils.PaymentDialog
import com.google.gson.Gson

class BrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowserBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val incoming_url: String = bundle?.getString("incoming_url").toString()

        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        loadUrl(incoming_url)
    }

    private fun loadUrl(url: String) {
        // Set a WebViewClient to handle URL changes
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Check if the WebView has navigated to the target URL
                println("URL: $url")
                if (url?.contains("baudoap.com/") == true) {
                    println("SHOULD BE CLOSED")
                    val resultIntent = Intent()
                    val payment_id = url.substringAfter("id=").substringBefore("&env")
                    resultIntent.putExtra("payment_id", payment_id)
                    setResult(Activity.RESULT_OK, resultIntent)
                    // Close the Activity when the target URL is reached
                    finish()
                }
            }
        }
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            println("Normal webview back")
            webView.goBack()
        } else {
            super.onBackPressed()
            println("End webview back")
            setResult(Activity.RESULT_CANCELED)
            // Close the Activity when the target URL is reached
            finish()
        }
    }
}