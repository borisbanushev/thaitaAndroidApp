package app.thaita.domain.repository

import app.thaita.domain.model.AuthResult
import app.thaita.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(email: String, username: String, password: String): Result<AuthResult>
    suspend fun getCurrentUser(): Result<User>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
    suspend fun resendVerification(): Result<Unit>
    fun logout()
    fun isAuthenticated(): Boolean
    fun getCachedUser(): User?
}
