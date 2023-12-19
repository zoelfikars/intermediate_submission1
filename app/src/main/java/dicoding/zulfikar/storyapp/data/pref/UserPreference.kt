package dicoding.zulfikar.storyapp.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dicoding.zulfikar.storyapp.data.models.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USERID_KEY] = user.userId
            preferences[NAMA_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
        }

    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[USERID_KEY] ?: "",
                preferences[NAMA_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USERID_KEY = stringPreferencesKey("userId")
        private val NAMA_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}