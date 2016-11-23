package com.chrisprime.primestationonecontrol.utilities

import android.annotation.SuppressLint
import android.webkit.WebView

/**
 * Created by cpaian on 11/22/16.
 */

object LayoutUtilities {
    /**
     * Initializes a WebView enabling only the minimum set of settings required for operation.

     * @param webView :webview to initialize
     */
    @SuppressLint("SetJavaScriptEnabled")
    @JvmStatic fun initWebView(webView: WebView) {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.allowFileAccess = false

        //Below hoity-toity features require SDK16:
//        settings.allowContentAccess = false
//        settings.allowFileAccessFromFileURLs = false
//        settings.allowUniversalAccessFromFileURLs = false
    }

}