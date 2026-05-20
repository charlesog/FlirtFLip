package com.example.flirtflip

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.flirtflip.ui.navigation.AppNavigation
import com.example.flirtflip.ui.theme.FlirtFlipTheme
import com.example.flirtflip.ui.theme.ThemePreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val prefs by lazy { getSharedPreferences("flirtflip_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val savedTheme = prefs.getString("theme", ThemePreference.SYSTEM.name)
            var themePreference by remember {
                mutableStateOf(
                    ThemePreference.valueOf(savedTheme ?: ThemePreference.SYSTEM.name)
                )
            }

            FlirtFlipTheme(themePreference = themePreference) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        themePreference = themePreference,
                        onThemeChange = { newTheme ->
                            themePreference = newTheme
                            prefs.edit().putString("theme", newTheme.name).apply()
                        }
                    )
                }
            }
        }
    }
}