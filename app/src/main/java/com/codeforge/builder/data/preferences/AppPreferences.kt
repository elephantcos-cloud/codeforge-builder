package com.codeforge.builder.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.codeforge.builder.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "codeforge_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_GITHUB_TOKEN = stringPreferencesKey(Constants.PREF_GITHUB_TOKEN)
        val KEY_GITHUB_USERNAME = stringPreferencesKey(Constants.PREF_GITHUB_USERNAME)
        val KEY_GITHUB_EMAIL = stringPreferencesKey(Constants.PREF_GITHUB_EMAIL)
        val KEY_THEME_MODE = intPreferencesKey(Constants.PREF_THEME_MODE)
        val KEY_EDITOR_FONT_SIZE = intPreferencesKey(Constants.PREF_EDITOR_FONT_SIZE)
        val KEY_EDITOR_THEME = stringPreferencesKey(Constants.PREF_EDITOR_THEME)
        val KEY_DEFAULT_PROJECT_TYPE = stringPreferencesKey(Constants.PREF_DEFAULT_PROJECT_TYPE)

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                AppPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // GitHub Token
    val githubToken: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_GITHUB_TOKEN] ?: "" }

    suspend fun setGithubToken(token: String) {
        context.dataStore.edit { it[KEY_GITHUB_TOKEN] = token }
    }

    // GitHub Username
    val githubUsername: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_GITHUB_USERNAME] ?: "" }

    suspend fun setGithubUsername(username: String) {
        context.dataStore.edit { it[KEY_GITHUB_USERNAME] = username }
    }

    // GitHub Email
    val githubEmail: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_GITHUB_EMAIL] ?: "" }

    suspend fun setGithubEmail(email: String) {
        context.dataStore.edit { it[KEY_GITHUB_EMAIL] = email }
    }

    // Theme Mode (0=System, 1=Light, 2=Dark)
    val themeMode: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_THEME_MODE] ?: 0 }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    // Editor Font Size
    val editorFontSize: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_EDITOR_FONT_SIZE] ?: Constants.DEFAULT_FONT_SIZE }

    suspend fun setEditorFontSize(size: Int) {
        context.dataStore.edit { it[KEY_EDITOR_FONT_SIZE] = size.coerceIn(Constants.MIN_FONT_SIZE, Constants.MAX_FONT_SIZE) }
    }

    // Editor Theme
    val editorTheme: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_EDITOR_THEME] ?: Constants.EDITOR_THEME_DARK }

    suspend fun setEditorTheme(theme: String) {
        context.dataStore.edit { it[KEY_EDITOR_THEME] = theme }
    }

    // Default Project Type
    val defaultProjectType: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[KEY_DEFAULT_PROJECT_TYPE] ?: Constants.PROJECT_TYPE_HTML }

    suspend fun setDefaultProjectType(type: String) {
        context.dataStore.edit { it[KEY_DEFAULT_PROJECT_TYPE] = type }
    }

    // Clear all preferences
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
