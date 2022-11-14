package it.post.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceManager(
    private val context: Context,
) {
    private val tokenKey = stringPreferencesKey("token")

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[tokenKey]
        }

    suspend fun saveToken(newToken: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = newToken
        }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }
}
