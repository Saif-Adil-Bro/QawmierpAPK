package com.qawmierp.apk

import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.qawmierp.apk.core.storage.SessionManager
import com.qawmierp.apk.ui.navigation.AppNavGraph
import com.qawmierp.apk.ui.theme.QawmiErpTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lang by sessionManager.languageFlow.collectAsState()
            val sessionExpired by sessionManager.sessionExpiredFlow.collectAsState()

            val localizedActivityContext = remember(lang) {
                val locale = Locale(lang)
                Locale.setDefault(locale)
                val config = Configuration(resources.configuration).apply { setLocale(locale) }
                val resContext = createConfigurationContext(config)
                object : ContextWrapper(this@MainActivity) {
                    override fun getResources(): Resources = resContext.resources
                }
            }

            CompositionLocalProvider(LocalContext provides localizedActivityContext) {
                QawmiErpTheme {
                    AppNavGraph(sessionExpired = sessionExpired)
                }
            }
        }
    }
}
