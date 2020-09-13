package io.avario.fansolution.android.launch

interface LaunchPresenter {

    fun onViewReady()

    fun setSessionExpireMillis(value: Long)

    fun onFinish()
}
