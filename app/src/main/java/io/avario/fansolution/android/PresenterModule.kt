package io.avario.fansolution.android

import dagger.Binds
import dagger.Module
import dagger.Provides
import io.avario.fansolution.android.launch.LaunchPresenter
import io.avario.fansolution.android.launch.LaunchPresenterImpl
import io.avario.fansolution.android.launch.LaunchView
import io.avario.fansolution.android.onboarding.authentication.AuthenticationPresenter
import io.avario.fansolution.android.onboarding.authentication.AuthenticationPresenterImpl
import io.avario.fansolution.android.onboarding.authentication.AuthenticationView
import io.avario.fansolution.android.onboarding.discovery.DiscoveryPresenter
import io.avario.fansolution.android.onboarding.discovery.DiscoveryPresenterImpl
import io.avario.fansolution.android.onboarding.discovery.DiscoveryView
import io.avario.fansolution.android.onboarding.integration.MobileAppIntegrationPresenter
import io.avario.fansolution.android.onboarding.integration.MobileAppIntegrationPresenterImpl
import io.avario.fansolution.android.onboarding.integration.MobileAppIntegrationView
import io.avario.fansolution.android.onboarding.manual.ManualSetupPresenter
import io.avario.fansolution.android.onboarding.manual.ManualSetupPresenterImpl
import io.avario.fansolution.android.onboarding.manual.ManualSetupView
import io.avario.fansolution.android.settings.SettingsPresenter
import io.avario.fansolution.android.settings.SettingsPresenterImpl
import io.avario.fansolution.android.settings.SettingsView
import io.avario.fansolution.android.webview.WebView
import io.avario.fansolution.android.webview.WebViewPresenter
import io.avario.fansolution.android.webview.WebViewPresenterImpl

@Module(includes = [PresenterModule.Declaration::class])
class PresenterModule {

    private lateinit var launchView: LaunchView
    private lateinit var discoveryView: DiscoveryView
    private lateinit var authenticationView: AuthenticationView
    private lateinit var manualSetupView: ManualSetupView
    private lateinit var mobileAppIntegrationView: MobileAppIntegrationView
    private lateinit var settingsView: SettingsView
    private lateinit var webView: WebView

    constructor(launchView: LaunchView) {
        this.launchView = launchView
    }

    constructor(discoveryView: DiscoveryView) {
        this.discoveryView = discoveryView
    }

    constructor(authenticationView: AuthenticationView) {
        this.authenticationView = authenticationView
    }

    constructor(manualSetupView: ManualSetupView) {
        this.manualSetupView = manualSetupView
    }

    constructor(mobileAppIntegrationView: MobileAppIntegrationView) {
        this.mobileAppIntegrationView = mobileAppIntegrationView
    }

    constructor(settingsView: SettingsView) {
        this.settingsView = settingsView
    }

    constructor(webView: WebView) {
        this.webView = webView
    }

    @Provides
    fun provideLaunchView() = launchView

    @Provides
    fun providesDiscoveryView() = discoveryView

    @Provides
    fun provideAuthenticationView() = authenticationView

    @Provides
    fun provideManualSetupView() = manualSetupView

    @Provides
    fun provideMobileAppIntegrationView() = mobileAppIntegrationView

    @Provides
    fun provideSettingsView() = settingsView

    @Provides
    fun provideWebView() = webView

    @Module
    interface Declaration {

        @Binds
        fun bindLaunchPresenter(presenter: LaunchPresenterImpl): LaunchPresenter

        @Binds
        fun bindDiscoveryPresenter(presenter: DiscoveryPresenterImpl): DiscoveryPresenter

        @Binds
        fun bindAuthenticationPresenterImpl(presenter: AuthenticationPresenterImpl): AuthenticationPresenter

        @Binds
        fun bindManualSetupPresenter(presenter: ManualSetupPresenterImpl): ManualSetupPresenter

        @Binds
        fun bindMobileAppPresenter(presenter: MobileAppIntegrationPresenterImpl): MobileAppIntegrationPresenter

        @Binds
        fun bindSettingsPresenter(presenter: SettingsPresenterImpl): SettingsPresenter

        @Binds
        fun bindWebViewPresenterImpl(presenter: WebViewPresenterImpl): WebViewPresenter
    }
}
