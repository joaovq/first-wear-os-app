package br.com.joaovq.data.user

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore by preferencesDataStore("user-settings")

@Keep
class UserRepository(context: Context) {
    private val userDatastore = context.datastore
    private val isNewUserPreferenceKey = booleanPreferencesKey("is-new-user")

    suspend fun setIsNewUser(value: Boolean) {
        userDatastore.edit {
            it[isNewUserPreferenceKey] = value
        }
    }

    fun getIsNewUser(): Flow<Boolean> =
        userDatastore.data.map { it[isNewUserPreferenceKey] ?: true }

}