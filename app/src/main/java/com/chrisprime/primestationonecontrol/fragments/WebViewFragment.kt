package com.chrisprime.primestationonecontrol.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.utilities.LayoutUtilities
import kotlinx.android.synthetic.main.fragment_webview.*
import timber.log.Timber

/**
 * A generic use v4 Fragment encapsulating a WebView and ProgressBar.
 */
class WebViewFragment : Fragment() {

    val title: String
        get() = arguments.getString(EXTRA_TITLE, "")

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater!!.inflate(R.layout.fragment_webview, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LayoutUtilities.initWebView(fragment_webview_webview!!)

        fragment_webview_webview.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                if (fragment_webview_webview != null && fragment_webview_progressbar != null) {
                    fragment_webview_webview!!.visibility = View.GONE
                    fragment_webview_progressbar!!.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                // these could be null if back button pressed before loading finishes causing onDestroyView
                if (fragment_webview_webview != null && fragment_webview_progressbar != null) {
                    fragment_webview_webview!!.visibility = View.VISIBLE
                    fragment_webview_progressbar!!.visibility = View.GONE
                }
            }
        })

        val url = arguments.getString(EXTRA_URL)
        Timber.d(".onViewCreated(): Loading provided URL %s", url)
        fragment_webview_webview.loadUrl(url)
    }

    fun onBackPressed(): Boolean {
        var webviewHandledBack = false
        if (!arguments.getBoolean(EXTRA_BACK_EXITS) && fragment_webview_webview!!.canGoBack()) {
            fragment_webview_webview!!.goBack()
            webviewHandledBack = true
        }
        return webviewHandledBack
    }

    companion object {

        val EXTRA_TITLE = ".WebViewFragment.EXTRA_TITLE"
        val EXTRA_URL = ".WebViewFragment.EXTRA_URL"
        val EXTRA_BACK_EXITS = ".WebViewFragment.EXTRA_BACK_EXITS"

        @JvmOverloads fun newInstance(title: String, url: String, backExits: Boolean = false): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_URL, url)
            args.putBoolean(EXTRA_BACK_EXITS, backExits)
            fragment.arguments = args
            return fragment
        }
    }
}
