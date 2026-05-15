package app.thaita.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import app.thaita.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "thaita_secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUser(user: User) {
        val userJson = json.encodeToString(UserSerializable.from(user))
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            json.decodeFromString<UserSerializable>(userJson).toDomain()
        } catch (_: Exception) {
            null
        }
    }

    fun clear() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER).apply()
    }

    val isAuthenticated: Boolean get() = getToken() != null

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER = "auth_user"
    }
}

@kotlinx.serialization.Serializable
private data class UserSerializable(
    val id: Int,
    val email: String,
    val username: String,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val oauthProvider: String = "local",
    val isVerified: Boolean = false,
    val termsAccepted: Boolean = false,
    val tier: String = "basic",
) {
    fun toDomain() = User(
        id = id,
        email = email,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        oauthProvider = oauthProvider,
        isVerified = isVerified,
        termsAccepted = termsAccepted,
        tier = tier,
    )

    companion object {
        fun from(user: User) = UserSerializable(
            id = user.id,
            email = user.email,
            username = user.username,
            fullName = user.fullName,
            avatarUrl = user.avatarUrl,
            oauthProvider = user.oauthProvider,
            isVerified = user.isVerified,
            termsAccepted = user.termsAccepted,
            tier = user.tier,
        )
    }
}
