package io.avario.fansolution.android.common.dagger

import dagger.Component
import io.avario.fansolution.android.common.data.authentication.AuthenticationRepository
import io.avario.fansolution.android.common.data.integration.IntegrationRepository
import io.avario.fansolution.android.common.data.themes.ThemesRepository
import io.avario.fansolution.android.common.data.url.UrlRepository

@Component(modules = [DataModule::class])
interface DataComponent {

    fun urlRepository(): UrlRepository

    fun authenticationRepository(): AuthenticationRepository

    fun integrationRepository(): IntegrationRepository

    fun themesRepository(): ThemesRepository
}
