package io.avario.fansolution.android.common.data.themes

interface ThemesRepository {
    suspend fun getCurrentTheme(): String?

    suspend fun saveTheme(theme: String)
}
