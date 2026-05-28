package com.example.flirtflip.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
    private val SAVED_EMAIL_KEY = stringPreferencesKey("saved_email")
    private val AGE_MIN_KEY = floatPreferencesKey("age_min")
    private val AGE_MAX_KEY = floatPreferencesKey("age_max")

    private fun welcomeSeenKey(userId: String) =
        booleanPreferencesKey("welcome_seen_$userId")

    val rememberMe: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMEMBER_ME_KEY] ?: false
    }

    val savedEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SAVED_EMAIL_KEY] ?: ""
    }

    val ageMin: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[AGE_MIN_KEY] ?: 18f
    }

    val ageMax: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[AGE_MAX_KEY] ?: 50f
    }

    suspend fun hasSeenWelcome(userId: String): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[welcomeSeenKey(userId)] ?: false
        }.first()
    }

    suspend fun markWelcomeSeen(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[welcomeSeenKey(userId)] = true
        }
    }

    suspend fun saveRememberMe(value: Boolean, email: String) {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME_KEY] = value
            prefs[SAVED_EMAIL_KEY] = if (value) email else ""
        }
    }

    suspend fun saveAgeRange(min: Float, max: Float) {
        context.dataStore.edit { prefs ->
            prefs[AGE_MIN_KEY] = min
            prefs[AGE_MAX_KEY] = max
        }
    }
}