package io.avario.fansolution.android.onboarding.integration

interface MobileAppIntegrationPresenter {
    fun onRegistrationAttempt(simple: Boolean)
    fun onFinish()
}
