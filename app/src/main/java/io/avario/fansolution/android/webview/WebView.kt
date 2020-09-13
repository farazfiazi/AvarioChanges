package io.avario.fansolution.android.webview

import android.net.http.SslError

interface WebView {

    fun loadUrl(url: String)

    fun setStatusBarAndNavigationBarColor(color: Int)

    fun setExternalAuth(script: String)

    fun openOnBoarding()

    fun showError(isAuthenticationError: Boolean = false, error: SslError? = null, description: String? = null)
}
