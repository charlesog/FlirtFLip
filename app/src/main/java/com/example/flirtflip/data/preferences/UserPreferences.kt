package com.example.flirtflip.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val REMEMBER_ME      = booleanPreferencesKey("remember_me")
        val SAVED_EMAIL      = stringPreferencesKey("saved_email")
    }

    val rememberMe: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMEMBER_ME] ?: false
    }

    val savedEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SAVED_EMAIL] ?: ""
    }

    suspend fun saveLoginPrefs(email: String, rememberMe: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME] = rememberMe
            prefs[SAVED_EMAIL] = if (rememberMe) email else ""
        }
    }

    suspend fun clearLoginPrefs() {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME] = false
            prefs[SAVED_EMAIL] = ""
        }
    }
}