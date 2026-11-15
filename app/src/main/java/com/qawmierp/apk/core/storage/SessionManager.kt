package com.qawmierp.apk.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.qawmierp.apk.BuildConfig
import com.qawmierp.apk.core.network.dto.MadrasaDto
import com.qawmierp.apk.core.network.dto.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context,
    private val json: Json
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "qawmierp_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val _languageFlow = MutableStateFlow(getLanguage())
    val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    private val _sessionExpiredFlow = MutableStateFlow(false)
    val sessionExpiredFlow: StateFlow<Boolean> = _sessionExpiredFlow.asStateFlow()

    fun saveSession(
        accessToken: String,
        refreshToken: String,
        user: UserDto,
        madrasa: MadrasaDto?
    ) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_USER, json.encodeToString(user))
            .apply {
                if (madrasa != null) putString(KEY_MADRASA, json.encodeToString(madrasa))
            }
            .apply()
        _sessionExpiredFlow.value = false
    }

    fun updateTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUser(): UserDto? {
        val raw = prefs.getString(KEY_USER, null) ?: return null
        return runCatching { json.decodeFromString<UserDto>(raw) }.getOrNull()
    }

    fun getMadrasa(): MadrasaDto? {
        val raw = prefs.getString(KEY_MADRASA, null) ?: return null
        return runCatching { json.decodeFromString<MadrasaDto>(raw) }.getOrNull()
    }

    fun getBaseUrl(): String {
        val custom = prefs.getString(KEY_BASE_URL, null)?.trim()
        val url = if (!custom.isNullOrEmpty()) custom else BuildConfig.DEFAULT_BASE_URL
        return if (url.endsWith("/")) url else "$url/"
    }

    fun setBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, url.trim()).apply()
    }

    fun getApiKey(): String {
        val custom = prefs.getString(KEY_API_KEY, null)?.trim()
        return if (!custom.isNullOrEmpty()) custom else BuildConfig.MOBILE_API_KEY
    }

    fun setApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey.trim()).apply()
    }

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "bn") ?: "bn"

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _languageFlow.value = lang
    }

    fun clearSession(expired: Boolean = false) {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER)
            .remove(KEY_MADRASA)
            .apply()
        if (expired) _sessionExpiredFlow.value = true
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER = "user_json"
        private const val KEY_MADRASA = "madrasa_json"
        private const val KEY_BASE_URL = "custom_base_url"
        private const val KEY_API_KEY = "custom_api_key"
        private const val KEY_LANGUAGE = "app_language"
    }
}
