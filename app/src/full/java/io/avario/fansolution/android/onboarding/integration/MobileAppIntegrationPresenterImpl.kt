package io.avario.fansolution.android.onboarding.integration

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import io.avario.fansolution.android.common.data.integration.DeviceRegistration
import io.avario.fansolution.android.common.data.integration.IntegrationRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class MobileAppIntegrationPresenterImpl @Inject constructor(
    view: MobileAppIntegrationView,
    integrationUseCase: IntegrationRepository
) : MobileAppIntegrationPresenterBase(
    view, integrationUseCase
) {
    override suspend fun createRegistration(simple: Boolean): DeviceRegistration {
        val registration = super.createRegistration(simple)

        if (!simple) {
            try {
                val instanceId = FirebaseInstanceId.getInstance().instanceId.await()
                registration.pushToken = instanceId.token
            } catch (e: Exception) {
                Log.e(TAG, "Unable to get firebase token.", e)
                throw e
            }
        }

        return registration
    }
}
