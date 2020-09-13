package io.avario.fansolution.android.database.sensor

import androidx.room.Embedded
import androidx.room.Relation
import io.avario.fansolution.android.common.data.integration.SensorRegistration

data class SensorWithAttributes(
    @Embedded
    val sensor: Sensor,
    @Relation(
        parentColumn = "id",
        entityColumn = "sensor_id"
    )
    val attributes: List<Attribute>
) {
    fun toSensorRegistration(): SensorRegistration<Any> {
        val attributes = attributes.map {
            val attributeValue = when (it.valueType) {
                "boolean" -> it.value.toBoolean()
                "float" -> it.value.toFloat()
                "int" -> it.value.toInt()
                "string" -> it.value
                else -> throw IllegalArgumentException("Attribute: ${it.name} is of unknown type: ${it.valueType}")
            }
            it.name to attributeValue
        }.toMap()
        val state = when (sensor.stateType) {
            "" -> ""
            "boolean" -> sensor.state.toBoolean()
            "float" -> sensor.state.toFloat()
            "int" -> sensor.state.toInt()
            "string" -> sensor.state
            else -> throw IllegalArgumentException("State is of unknown type: ${sensor.stateType}")
        }
        return SensorRegistration(
            sensor.id,
            state,
            sensor.type,
            sensor.icon,
            attributes,
            sensor.name,
            sensor.deviceClass,
            sensor.unitOfMeasurement
        )
    }
}
