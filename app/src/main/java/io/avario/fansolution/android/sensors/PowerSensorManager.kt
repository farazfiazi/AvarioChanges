package io.avario.fansolution.android.sensors

import android.content.Context
import android.content.Context.POWER_SERVICE
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import io.avario.fansolution.android.R

class PowerSensorManager : SensorManager {
    companion object {
        private const val TAG = "PowerSensors"
        private const val packageName = "io.homeassistant.companion.android"

        private val interactiveDevice = SensorManager.BasicSensor(
            "is_interactive",
            "binary_sensor",
            R.string.basic_sensor_name_interactive,
            R.string.sensor_description_interactive
        )
        private val doze = SensorManager.BasicSensor(
            "is_idle",
            "binary_sensor",
            R.string.basic_sensor_name_doze,
            R.string.sensor_description_doze
        )
        private val powerSave = SensorManager.BasicSensor(
            "power_save",
            "binary_sensor",
            R.string.basic_sensor_name_power_save,
            R.string.sensor_description_power_save
        )
    }

    override val enabledByDefault: Boolean
        get() = false
    override val name: Int
        get() = R.string.sensor_name_power

    override val availableSensors: List<SensorManager.BasicSensor>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listOf(interactiveDevice, doze, powerSave)
        } else {
            listOf(interactiveDevice, powerSave)
        }

    override fun requiredPermissions(): Array<String> {
        return emptyArray()
    }

    override fun requestSensorUpdate(
        context: Context
    ) {
        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        updateInteractive(context, powerManager)
        updatePowerSave(context, powerManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            updateDoze(context, powerManager)
        }
    }

    private fun updateInteractive(context: Context, powerManager: PowerManager) {

        if (!isEnabled(context, interactiveDevice.id))
            return

        val interactiveState = powerManager.isInteractive
        val icon = if (interactiveState) "mdi:cellphone" else "mdi:cellphone-off"

        onSensorUpdated(
            context,
            interactiveDevice,
            interactiveState,
            icon,
            mapOf()
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateDoze(context: Context, powerManager: PowerManager) {

        if (!isEnabled(context, doze.id))
            return

        val dozeState = powerManager.isDeviceIdleMode
        val icon = if (dozeState) "mdi:sleep" else "mdi:sleep-off"

        onSensorUpdated(
            context,
            doze,
            dozeState,
            icon,
            mapOf(
                "ignoring_battery_optizimations" to powerManager.isIgnoringBatteryOptimizations(packageName)
            )
        )
    }

    private fun updatePowerSave(context: Context, powerManager: PowerManager) {

        if (!isEnabled(context, powerSave.id))
            return

        val powerSaveState = powerManager.isPowerSaveMode
        val icon = "mdi:battery-plus"

        onSensorUpdated(
            context,
            powerSave,
            powerSaveState,
            icon,
            mapOf()
        )
    }
}
