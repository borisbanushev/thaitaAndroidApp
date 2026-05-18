package app.thaita.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Strikes & Expirations ────────────────────────────────────────────────

@Serializable
data class StrikesResponseDto(
    val strikes: List<Double>,
    @SerialName("current_price") val currentPrice: Double,
)

@Serializable
data class ExpirationDateDto(
    val days: Int,
    val date: String,
    @SerialName("date_formatted") val dateFormatted: String,
)

@Serializable
data class ExpirationsResponseDto(
    val expirations: List<ExpirationDateDto>,
)

@Serializable
data class RecommendedResponseDto(
    val strike: Double,
    @SerialName("expiration_days") val expirationDays: Int,
    val premium: Double? = null,
)

// ── P/L ──────────────────────────────────────────────────────────────────

@Serializable
data class CalculatePLRequestDto(
    val ticker: String,
    @SerialName("strike_price") val strikePrice: Double,
    @SerialName("expiration_days") val expirationDays: Int,
)

@Serializable
data class PLDataPointDto(
    @SerialName("stockPrice") val stockPrice: Double,
    @SerialName("plExpiration") val plExpiration: Double,
    @SerialName("pl5Days") val pl5Days: Double,
    @SerialName("pl10Days") val pl10Days: Double,
    @SerialName("pl20Days") val pl20Days: Double,
    val delta: Double = 0.0,
    val gamma: Double = 0.0,
    val volume: Double = 0.0,
)

@Serializable
data class PLResponseDto(
    @SerialName("pl_data") val plData: List<PLDataPointDto>,
    val premium: Double,
    @SerialName("current_price") val currentPrice: Double,
)

// ── Metrics ──────────────────────────────────────────────────────────────

@Serializable
data class MetricsRequestDto(
    val ticker: String,
    @SerialName("strike_price") val strikePrice: Double,
    @SerialName("expiration_days") val expirationDays: Int,
)

@Serializable
data class MetricInfoDto(
    val description: String? = null,
    val interpretation: String? = null,
)

@Serializable
data class MetricsResponseDto(
    val premium: Double,
    @SerialName("max_profit") val maxProfit: Double,
    @SerialName("probability_of_assignment") val probabilityOfAssignment: Double,
    @SerialName("annualized_return") val annualizedReturn: Double,
    val delta: Double,
    val gamma: Double,
    val vega: Double,
    val theta: Double,
    val info: Map<String, MetricInfoDto> = emptyMap(),
)

// ── Stock Info ────────────────────────────────────────────────────────────

@Serializable
data class StockInfoDto(
    val ticker: String,
    @SerialName("company_name") val companyName: String,
    val sector: String? = null,
    val price: Double,
)

// ── RR Analysis ──────────────────────────────────────────────────────────

@Serializable
data class RRDataPointDto(
    val ticker: String = "",
    val bid: Double,
    val delta: Double,
    @SerialName("implied_volatility") val impliedVolatility: Double,
    @SerialName("rr_coeff") val rrCoeff: Double,
    @SerialName("expiration_date") val expirationDate: String = "",
    @SerialName("strike_pct_over_price") val strikePctOverPrice: Double = 0.0,
    @SerialName("strike_price") val strikePrice: Double = 0.0,
)

@Serializable
data class RRTableRowDto(
    @SerialName("expiration_date") val expirationDate: String,
    @SerialName("strike_price") val strikePrice: Double,
    val delta: Double,
    val bid: Double,
    @SerialName("implied_volatility") val impliedVolatility: Double,
    @SerialName("rr_coeff") val rrCoeff: Double,
)

@Serializable
data class RRRegressionDto(
    val slope: Double,
    val intercept: Double,
)

@Serializable
data class RRAnalysisResponseDto(
    val data: List<RRDataPointDto>,
    @SerialName("table_data") val tableData: List<RRTableRowDto>,
    val regression: RRRegressionDto,
)

// ── Daily Profit ─────────────────────────────────────────────────────────

@Serializable
data class DailyProfitDataPointDto(
    val dte: Int,
    val date: String,
    @SerialName("date_formatted") val dateFormatted: String,
    @SerialName("profit_per_day") val profitPerDay: Double,
    @SerialName("total_calls_volume") val totalCallsVolume: Int = 0,
)

@Serializable
data class DailyProfitResponseDto(
    val data: List<DailyProfitDataPointDto>,
    val ticker: String = "",
)

// ── Expected vs Realized ─────────────────────────────────────────────────

@Serializable
data class ExpectedRealizedDataPointDto(
    val dte: Int,
    val date: String,
    @SerialName("expected_move") val expectedMove: Double,
    @SerialName("realized_move") val realizedMove: Double,
    @SerialName("is_favorable") val isFavorable: Boolean,
)

@Serializable
data class ExpectedRealizedResponseDto(
    val data: List<ExpectedRealizedDataPointDto>,
    val ticker: String = "",
)

// ── Gamma Exposure ───────────────────────────────────────────────────────

@Serializable
data class GammaBarDto(
    val strike: Double,
    @SerialName("gamma_exposure") val gammaExposure: Double,
)

@Serializable
data class GammaExposureResponseDto(
    val bars: List<GammaBarDto>,
    @SerialName("resistance_levels") val resistanceLevels: List<Double> = emptyList(),
    @SerialName("current_price") val currentPrice: Double,
    val ticker: String = "",
)

// ── Probability of Assignment ────────────────────────────────────────────

@Serializable
data class ProbabilityBarDto(
    val strike: Double,
    val probability: Double,
)

@Serializable
data class ProbabilityAssignmentResponseDto(
    val bars: List<ProbabilityBarDto>,
    @SerialName("selected_strike") val selectedStrike: Double,
    @SerialName("summary_text") val summaryText: String = "",
    val ticker: String = "",
)

// ── Companies ────────────────────────────────────────────────────────────

@Serializable
data class CompanyRecordDto(
    val ticker: String,
    val company: String,
    val sector: String,
)

@Serializable
data class CompaniesResponseDto(
    val companies: List<CompanyRecordDto>,
)

// ── Streaming Explanation Request ────────────────────────────────────────

@Serializable
data class ExplanationRequestDto(
    val ticker: String,
    @SerialName("strike_price") val strikePrice: Double? = null,
    @SerialName("expiration_days") val expirationDays: Int? = null,
    val premium: Double? = null,
    @SerialName("current_price") val currentPrice: Double? = null,
    val metrics: MetricsResponseDto? = null,
)

@Serializable
data class ChatRequestDto(
    val messages: List<ChatMessageDto>,
    val context: ChatContextDto? = null,
)

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class ChatContextDto(
    val ticker: String? = null,
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("selected_strike") val selectedStrike: Double? = null,
    @SerialName("selected_expiration") val selectedExpiration: Int? = null,
    val premium: Double? = null,
)
