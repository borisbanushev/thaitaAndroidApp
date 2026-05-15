package app.thaita.domain.model

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val oauthProvider: String = "local",
    val isVerified: Boolean = false,
    val termsAccepted: Boolean = false,
    val tier: String = "basic",
)

data class AuthResult(
    val token: String,
    val user: User,
)
