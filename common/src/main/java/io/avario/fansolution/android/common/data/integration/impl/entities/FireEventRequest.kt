package io.avario.fansolution.android.common.data.integration.impl.entities

data class FireEventRequest(
    val eventType: String,
    val eventData: Map<String, Any>
)
