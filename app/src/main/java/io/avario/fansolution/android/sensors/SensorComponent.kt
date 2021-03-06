package io.avario.fansolution.android.sensors

import dagger.Component
import io.avario.fansolution.android.PresenterModule
import io.avario.fansolution.android.common.dagger.AppComponent

@Component(dependencies = [AppComponent::class], modules = [PresenterModule::class])
interface SensorComponent {

    fun inject(locationSensorManager: LocationSensorManager)

    fun inject(worker: SensorWorker)

    fun inject(sensorReceiver: SensorReceiver)

    fun inject(sensorsSettingsFragment: SensorsSettingsFragment)

    fun inject(sensorDetailFragment: SensorDetailFragment)

    fun inject(activitySensorManager: ActivitySensorManager)
}
