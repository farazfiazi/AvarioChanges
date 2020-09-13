package io.avario.fansolution.android.common.dagger

import android.os.Build
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.avario.fansolution.android.common.data.HomeAssistantRetrofit
import io.avario.fansolution.android.common.data.LocalStorage
import io.avario.fansolution.android.common.data.authentication.AuthenticationRepository
import io.avario.fansolution.android.common.data.authentication.impl.AuthenticationRepositoryImpl
import io.avario.fansolution.android.common.data.authentication.impl.AuthenticationService
import io.avario.fansolution.android.common.data.integration.IntegrationRepository
import io.avario.fansolution.android.common.data.integration.impl.IntegrationRepositoryImpl
import io.avario.fansolution.android.common.data.integration.impl.IntegrationService
import io.avario.fansolution.android.common.data.themes.ThemesRepository
import io.avario.fansolution.android.common.data.themes.ThemesRepositoryImpl
import io.avario.fansolution.android.common.data.url.UrlRepository
import io.avario.fansolution.android.common.data.url.UrlRepositoryImpl
import io.avario.fansolution.android.common.data.wifi.WifiHelper
import javax.inject.Named

@Module(includes = [DataModule.Declaration::class])
class DataModule(
    private val urlStorage: LocalStorage,
    private val sessionLocalStorage: LocalStorage,
    private val integrationLocalStorage: LocalStorage,
    private val themesLocalStorage: LocalStorage,
    private val wifiHelper: WifiHelper,
    private val deviceId: String
) {

    @Provides
    fun provideAuthenticationService(homeAssistantRetrofit: HomeAssistantRetrofit): AuthenticationService =
        homeAssistantRetrofit.retrofit.create(AuthenticationService::class.java)

    @Provides
    fun providesIntegrationService(homeAssistantRetrofit: HomeAssistantRetrofit): IntegrationService =
        homeAssistantRetrofit.retrofit.create(IntegrationService::class.java)

    @Provides
    fun providesWifiHelper() = wifiHelper

    @Provides
    @Named("url")
    fun provideUrlLocalStorage() = urlStorage

    @Provides
    @Named("session")
    fun provideSessionLocalStorage() = sessionLocalStorage

    @Provides
    @Named("integration")
    fun provideIntegrationLocalStorage() = integrationLocalStorage

    @Provides
    @Named("themes")
    fun provideThemesLocalStorage() = themesLocalStorage

    @Provides
    @Named("manufacturer")
    fun provideDeviceManufacturer(): String = Build.MANUFACTURER

    @Provides
    @Named("model")
    fun provideDeviceModel(): String = Build.MODEL

    @Provides
    @Named("osVersion")
    fun provideDeviceOsVersion() = Build.VERSION.SDK_INT.toString()

    @Provides
    @Named("deviceId")
    fun provideDeviceId() = deviceId

    @Module
    interface Declaration {
        @Binds
        fun bindUrlRepositoryImpl(repository: UrlRepositoryImpl): UrlRepository

        @Binds
        fun bindAuthenticationRepositoryImpl(repository: AuthenticationRepositoryImpl): AuthenticationRepository

        @Binds
        fun bindIntegrationService(repository: IntegrationRepositoryImpl): IntegrationRepository

        @Binds
        fun bindThemesRepositoryImpl(repository: ThemesRepositoryImpl): ThemesRepository
    }
}
