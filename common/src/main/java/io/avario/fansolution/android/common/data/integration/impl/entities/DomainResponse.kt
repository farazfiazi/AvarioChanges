package io.avario.fansolution.android.common.data.integration.impl.entities

import io.avario.fansolution.android.common.data.integration.ServiceData

data class DomainResponse(
    val domain: String,
    val services: Map<String, ServiceData>
)
