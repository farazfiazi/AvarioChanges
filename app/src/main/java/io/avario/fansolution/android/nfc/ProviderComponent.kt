package io.avario.fansolution.android.nfc

import dagger.Component
import io.avario.fansolution.android.common.dagger.AppComponent

@Component(dependencies = [AppComponent::class])
interface ProviderComponent {

    fun inject(activity: TagReaderActivity)
    fun inject(nfcEditFragment: NfcEditFragment)
}
