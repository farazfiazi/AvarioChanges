package io.avario.fansolution.android

import dagger.Component
import io.avario.fansolution.android.common.dagger.AppComponent
import io.avario.fansolution.android.launch.LaunchActivity
import io.avario.fansolution.android.onboarding.authentication.AuthenticationFragment
import io.avario.fansolution.android.onboarding.discovery.DiscoveryFragment
import io.avario.fansolution.android.onboarding.integration.MobileAppIntegrationFragment
import io.avario.fansolution.android.onboarding.manual.ManualSetupFragment
import io.avario.fansolution.android.sensors.SensorsSettingsFragment
import io.avario.fansolution.android.settings.SettingsActivity
import io.avario.fansolution.android.settings.SettingsFragment
import io.avario.fansolution.android.settings.ssid.SsidDialogFragment
import io.avario.fansolution.android.webview.WebViewActivity

@Component(dependencies = [AppComponent::class], modules = [PresenterModule::class])
interface PresenterComponent {

    fun inject(activity: LaunchActivity)

    fun inject(fragment: DiscoveryFragment)

    fun inject(fragment: AuthenticationFragment)

    fun inject(fragment: ManualSetupFragment)

    fun inject(fragment: MobileAppIntegrationFragment)

    fun inject(activity: SettingsActivity)

    fun inject(fragment: SettingsFragment)

    fun inject(activity: WebViewActivity)

    fun inject(dialog: SsidDialogFragment)

    fun inject(fragment: SensorsSettingsFragment)
}
