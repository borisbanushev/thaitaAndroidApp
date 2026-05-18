package app.thaita.data.repository

import app.thaita.data.local.TokenManager
import app.thaita.data.remote.ThaitaApiService
import app.thaita.data.remote.dto.LoginRequest
import app.thaita.data.remote.dto.RegisterRequest
import app.thaita.data.remote.dto.ForgotPasswordRequest
import app.thaita.data.remote.dto.ResetPasswordRequest
import app.thaita.domain.model.AuthResult
import app.thaita.domain.model.User
import app.thaita.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ThaitaApiService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResult> =
        runCatching {
            val response = api.login(LoginRequest(email, password))
            val user = response.user.toDomain()
            tokenManager.saveToken(response.token)
            tokenManager.saveUser(user)
            AuthResult(token = response.token, user = user)
        }

    override suspend fun register(
        email: String,
        username: String,
        password: String,
    ): Result<AuthResult> = runCatching {
        val response = api.register(RegisterRequest(email, username, password))
        val user = response.user.toDomain()
        tokenManager.saveToken(response.token)
        tokenManager.saveUser(user)
        AuthResult(token = response.token, user = user)
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        api.getCurrentUser().toDomain()
    }

    override suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        api.forgotPassword(ForgotPasswordRequest(email))
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> =
        runCatching {
            api.resetPassword(ResetPasswordRequest(token, newPassword))
        }

    override suspend fun resendVerification(): Result<Unit> = runCatching {
        api.resendVerification()
    }

    override fun logout() {
        tokenManager.clear()
    }

    override fun isAuthenticated(): Boolean = tokenManager.isAuthenticated

    override fun getCachedUser(): User? = tokenManager.getUser()
}

private fun app.thaita.data.remote.dto.UserDto.toDomain() = User(
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
