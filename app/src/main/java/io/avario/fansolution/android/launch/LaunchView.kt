package io.avario.fansolution.android.launch

interface LaunchView {
    fun displayWebview()

    fun displayOnBoarding(sessionConnected: Boolean)
}
