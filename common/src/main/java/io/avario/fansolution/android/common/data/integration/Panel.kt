package io.avario.fansolution.android.common.data.integration

data class Panel(
    val component_name: String,
    val icon: String?,
    val title: String?,
    var title_localized: String?,
    val url_path: String,
    val require_admin: Boolean
)
