package app.thaita.domain.repository

import app.thaita.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AnalysisRepository {
    suspend fun getCompanies(): Result<List<CompanyRecord>>
    suspend fun getStrikes(ticker: String): Result<Pair<List<Double>, Double>>
    suspend fun getExpirations(ticker: String): Result<List<ExpirationDate>>
    suspend fun getRecommended(ticker: String): Result<Triple<Double, Int, Double?>>
    suspend fun calculatePL(ticker: String, strike: Double, expirationDays: Int): Result<PLResponse>
    suspend fun getMetrics(ticker: String, strike: Double, expirationDays: Int): Result<Metrics>
    suspend fun getStockInfo(ticker: String): Result<StockInfo>
    suspend fun getRRAnalysis(ticker: String): Result<RRAnalysisResponse>
    suspend fun getDailyProfit(ticker: String): Result<DailyProfitResponse>
    suspend fun getExpectedRealizedMove(ticker: String): Result<ExpectedRealizedResponse>
    suspend fun getGammaExposure(ticker: String): Result<GammaExposureResponse>
    suspend fun getProbabilityAssignment(ticker: String): Result<ProbabilityAssignmentResponse>
    fun streamExplanation(requestBody: String): Flow<String>
    fun streamRRExplanation(requestBody: String): Flow<String>
    fun streamDailyProfitExplanation(requestBody: String): Flow<String>
    fun streamExpectedRealizedExplanation(requestBody: String): Flow<String>
    fun streamProbabilityAssignmentExplanation(requestBody: String): Flow<String>
    fun streamGammaExposureExplanation(requestBody: String): Flow<String>
    fun streamChat(requestBody: String): Flow<String>
}
