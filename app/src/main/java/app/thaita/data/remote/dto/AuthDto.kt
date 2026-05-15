package app.thaita.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    @SerialName("terms_accepted") val termsAccepted: Boolean = true,
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("oauth_provider") val oauthProvider: String = "local",
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("terms_accepted") val termsAccepted: Boolean = false,
    val tier: String = "basic",
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    @SerialName("new_password") val newPassword: String,
)

@Serializable
data class FeedbackRequestDto(
    val username: String = "Anonymous",
    val email: String? = null,
    val category: String = "General",
    val message: String,
)

@Serializable
data class TourStatusDto(
    val completed: Boolean,
)

@Serializable
data class TourCompleteDto(
    val status: String,
)
