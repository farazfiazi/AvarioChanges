package io.avario.fansolution.android.common.data.integration

data class UpdateLocation(
    val locationName: String,
    val gps: Array<Double>,
    val gpsAccuracy: Int,
    val speed: Int,
    val altitude: Int,
    val course: Int,
    val verticalAccuracy: Int
)
