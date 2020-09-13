package io.avario.fansolution.android.onboarding.discovery

import java.net.URL

data class HomeAssistantInstance(
    val name: String,
    val url: URL,
    val version: String
)
