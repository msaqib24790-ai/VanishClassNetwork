
package com.vanish.classnetwork.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.identityDataStore by preferencesDataStore(
    name = "student_identity"
)

data class StudentIdentity(
    val deviceId: String,
    val displayName: String
)

class StudentIdentityRepository(
    private val context: Context
) {

    companion object {
        private val DEVICE_ID = stringPreferencesKey("device_id")
        private val DISPLAY_NAME = stringPreferencesKey("display_name")
    }

    val studentIdentity: Flow<StudentIdentity?> =
        context.identityDataStore.data.map { preferences ->

            val deviceId = preferences[DEVICE_ID]
            val displayName = preferences[DISPLAY_NAME]

            if (deviceId.isNullOrBlank() || displayName.isNullOrBlank()) {
                null
            } else {
                StudentIdentity(
                    deviceId = deviceId,
                    displayName = displayName
                )
            }
        }

    suspend fun saveIdentity(displayName: String): StudentIdentity {

        val cleanName = displayName.trim()

        require(cleanName.length in 2..40) {
            "Display name must contain 2 to 40 characters."
        }

        val preferences = context.identityDataStore.edit { settings ->

            val deviceId = settings[DEVICE_ID]
                ?.takeIf { it.isNotBlank() }
                ?: UUID.randomUUID().toString()

            settings[DEVICE_ID] = deviceId
            settings[DISPLAY_NAME] = cleanName
        }

        return StudentIdentity(
            deviceId = requireNotNull(preferences[DEVICE_ID]),
            displayName = requireNotNull(preferences[DISPLAY_NAME])
        )
    }

    suspend fun getOrCreateDeviceId(): String {

        var generatedId = ""

        context.identityDataStore.edit { settings ->

            val existingId = settings[DEVICE_ID]

            generatedId = existingId
                ?.takeIf { it.isNotBlank() }
                ?: UUID.randomUUID().toString()

            settings[DEVICE_ID] = generatedId
        }

        return generatedId
    }

    suspend fun clearIdentity() {
        context.identityDataStore.edit { settings ->
            settings.remove(DISPLAY_NAME)
            settings.remove(DEVICE_ID)
        }
    }
}
