package io.avario.fansolution.android.sensors

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.avario.fansolution.android.common.dagger.GraphComponentAccessor
import io.avario.fansolution.android.common.data.integration.IntegrationRepository
import io.avario.fansolution.android.common.data.integration.SensorRegistration
import io.avario.fansolution.android.database.AppDatabase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SensorReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SensorReceiver"
        val MANAGERS = listOf(
            ActivitySensorManager(),
            AudioSensorManager(),
            BatterySensorManager(),
            BluetoothSensorManager(),
            DNDSensorManager(),
            GeocodeSensorManager(),
            LastRebootSensorManager(),
            LightSensorManager(),
            LocationSensorManager(),
            NetworkSensorManager(),
            NextAlarmManager(),
            PhoneStateSensorManager(),
            PowerSensorManager(),
            PressureSensorManager(),
            ProximitySensorManager(),
            StepsSensorManager(),
            StorageSensorManager()
        )

        const val ACTION_REQUEST_SENSORS_UPDATE =
            "io.homeassistant.companion.android.background.REQUEST_SENSORS_UPDATE"
    }

    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    @Inject
    lateinit var integrationUseCase: IntegrationRepository

    private val chargingActions = listOf(
        Intent.ACTION_BATTERY_LOW,
        Intent.ACTION_BATTERY_OKAY,
        Intent.ACTION_POWER_CONNECTED,
        Intent.ACTION_POWER_DISCONNECTED
    )

    override fun onReceive(context: Context, intent: Intent) {

        DaggerSensorComponent.builder()
            .appComponent((context.applicationContext as GraphComponentAccessor).appComponent)
            .build()
            .inject(this)

        when (intent.action) {
            "android.app.action.NEXT_ALARM_CLOCK_CHANGED" -> {
                val sensorDao = AppDatabase.getInstance(context).sensorDao()
                val sensor = sensorDao.get(NextAlarmManager.nextAlarm.id)
                if (sensor?.enabled != true) {
                    Log.d(TAG, "Alarm Sensor disabled, skipping sensors update")
                    return
                }
            }
            "android.bluetooth.device.action.ACL_CONNECTED",
                "android.bluetooth.device.action.ACL_DISCONNECTED" -> {
                val sensorDao = AppDatabase.getInstance(context).sensorDao()
                val sensorBtConn = sensorDao.get(BluetoothSensorManager.bluetoothConnection.id)
                if (sensorBtConn?.enabled != true) {
                    Log.d(TAG, "Bluetooth Connection Sensor disabled, skipping sensors update")
                    return
                }
            }
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val sensorDao = AppDatabase.getInstance(context).sensorDao()
                val sensorBtState = sensorDao.get(BluetoothSensorManager.bluetoothState.id)
                if (sensorBtState?.enabled != true) {
                    Log.d(TAG, "Bluetooth State Sensor disabled, skipping sensors update")
                    return
                }
            }
        }

        ioScope.launch {
            updateSensors(context, integrationUseCase)
            if (chargingActions.contains(intent.action)) {
                // Add a 5 second delay to perform another update so charging state updates completely.
                // This is necessary as the system needs a few seconds to verify the charger.
                delay(5000L)
                updateSensors(context, integrationUseCase)
            }
        }
    }

    suspend fun updateSensors(
        context: Context,
        integrationUseCase: IntegrationRepository
    ) {
        val sensorDao = AppDatabase.getInstance(context).sensorDao()
        val enabledRegistrations = mutableListOf<SensorRegistration<Any>>()

        MANAGERS.forEach { manager ->
            try {
                manager.requestSensorUpdate(context)
            } catch (e: Exception) {
                Log.e(TAG, "Issue requesting updates for ${context.getString(manager.name)}", e)
            }
            manager.availableSensors.forEach { basicSensor ->
                val fullSensor = sensorDao.getFull(basicSensor.id)
                val sensor = fullSensor?.sensor

                // Register Sensors if needed
                if (sensor?.enabled == true && !sensor.registered && !sensor.type.isBlank()) {
                    val reg = fullSensor.toSensorRegistration()
                    reg.name = context.getString(basicSensor.name)
                    try {
                        integrationUseCase.registerSensor(reg)
                        sensor.registered = true
                        sensorDao.update(sensor)
                    } catch (e: Exception) {
                        Log.e(TAG, "Issue registering sensor: ${reg.uniqueId}", e)
                    }
                }
                if (sensor?.enabled == true && sensor.registered && sensor.state != sensor.lastSentState) {
                    enabledRegistrations.add(fullSensor.toSensorRegistration())
                }
            }
        }

        if (enabledRegistrations.isNotEmpty()) {
            var success = false
            try {
                success = integrationUseCase.updateSensors(enabledRegistrations.toTypedArray())
                enabledRegistrations.forEach {
                    sensorDao.updateLastSendState(it.uniqueId, it.state.toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while updating sensors.", e)
            }

            // We failed to update a sensor, we should re register next time
            if (!success) {
                enabledRegistrations.forEach {
                    val sensor = sensorDao.get(it.uniqueId)
                    if (sensor != null) {
                        sensor.registered = false
                        sensor.lastSentState = ""
                        sensorDao.update(sensor)
                    }
                }
            }
        } else Log.d(TAG, "Nothing to update")
    }
}
