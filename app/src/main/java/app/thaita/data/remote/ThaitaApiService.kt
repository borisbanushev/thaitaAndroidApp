package app.thaita.data.remote

import app.thaita.data.remote.dto.*
import retrofit2.http.*

interface ThaitaApiService {

    // ── Auth ─────────────────────────────────────────────────────────────

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponseDto

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponseDto

    @GET("/api/auth/me")
    suspend fun getCurrentUser(): UserDto

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest)

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest)

    @POST("/api/auth/resend-verification")
    suspend fun resendVerification()

    // ── Companies ────────────────────────────────────────────────────────

    @GET("/api/companies")
    suspend fun getCompanies(): CompaniesResponseDto

    // ── Covered Call ─────────────────────────────────────────────────────

    @GET("/api/strikes")
    suspend fun getStrikes(@Query("ticker") ticker: String): StrikesResponseDto

    @GET("/api/expirations")
    suspend fun getExpirations(@Query("ticker") ticker: String): ExpirationsResponseDto

    @GET("/api/recommended")
    suspend fun getRecommended(@Query("ticker") ticker: String): RecommendedResponseDto

    @POST("/api/calculate-pl")
    suspend fun calculatePL(@Body request: CalculatePLRequestDto): PLResponseDto

    @POST("/api/metrics")
    suspend fun getMetrics(@Body request: MetricsRequestDto): MetricsResponseDto

    @GET("/api/stock-info")
    suspend fun getStockInfo(@Query("ticker") ticker: String): StockInfoDto

    // ── Analysis ─────────────────────────────────────────────────────────

    @GET("/api/rr-analysis")
    suspend fun getRRAnalysis(@Query("ticker") ticker: String): RRAnalysisResponseDto

    @GET("/api/daily-profit")
    suspend fun getDailyProfit(@Query("ticker") ticker: String): DailyProfitResponseDto

    @GET("/api/expected-realized-move")
    suspend fun getExpectedRealizedMove(@Query("ticker") ticker: String): ExpectedRealizedResponseDto

    @GET("/api/probability-assignment")
    suspend fun getProbabilityAssignment(@Query("ticker") ticker: String): ProbabilityAssignmentResponseDto

    @GET("/api/gamma-exposure")
    suspend fun getGammaExposure(@Query("ticker") ticker: String): GammaExposureResponseDto

    // ── Tour ─────────────────────────────────────────────────────────────

    @GET("/api/user/tour-status")
    suspend fun getTourStatus(): TourStatusDto

    @POST("/api/user/tour-complete")
    suspend fun completeTour(): TourCompleteDto

    // ── Feedback ─────────────────────────────────────────────────────────

    @POST("/api/feedback")
    suspend fun submitFeedback(@Body request: FeedbackRequestDto)
}
