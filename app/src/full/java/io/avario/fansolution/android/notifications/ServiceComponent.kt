package io.avario.fansolution.android.notifications

import dagger.Component
import io.avario.fansolution.android.common.dagger.AppComponent

@Component(dependencies = [AppComponent::class])
interface ServiceComponent {

    fun inject(service: MessagingService)

    fun inject(receiver: NotificationActionReceiver)
}
