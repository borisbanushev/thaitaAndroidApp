package app.thaita.domain.model

data class PLDataPoint(
    val stockPrice: Double,
    val plExpiration: Double,
    val pl5Days: Double,
    val pl10Days: Double,
    val pl20Days: Double,
    val delta: Double,
    val gamma: Double,
    val volume: Double,
)

data class PLResponse(
    val plData: List<PLDataPoint>,
    val premium: Double,
    val currentPrice: Double,
)

data class ExpirationDate(
    val days: Int,
    val date: String,
    val dateFormatted: String,
)

data class MetricInfo(
    val description: String? = null,
    val interpretation: String? = null,
)

data class Metrics(
    val premium: Double,
    val maxProfit: Double,
    val probabilityOfAssignment: Double,
    val annualizedReturn: Double,
    val delta: Double,
    val gamma: Double,
    val vega: Double,
    val theta: Double,
    val info: Map<String, MetricInfo> = emptyMap(),
)

data class StockInfo(
    val ticker: String,
    val companyName: String,
    val sector: String? = null,
    val price: Double,
)

data class RRDataPoint(
    val ticker: String = "",
    val bid: Double,
    val delta: Double,
    val impliedVolatility: Double,
    val rrCoeff: Double,
    val expirationDate: String = "",
    val strikePctOverPrice: Double = 0.0,
    val strikePrice: Double = 0.0,
)

data class RRTableRow(
    val expirationDate: String,
    val strikePrice: Double,
    val delta: Double,
    val bid: Double,
    val impliedVolatility: Double,
    val rrCoeff: Double,
)

data class RRRegression(
    val slope: Double,
    val intercept: Double,
)

data class RRAnalysisResponse(
    val data: List<RRDataPoint>,
    val tableData: List<RRTableRow>,
    val regression: RRRegression,
)

data class DailyProfitDataPoint(
    val dte: Int,
    val date: String,
    val dateFormatted: String,
    val profitPerDay: Double,
    val totalCallsVolume: Int,
)

data class DailyProfitResponse(
    val data: List<DailyProfitDataPoint>,
    val ticker: String = "",
)

data class ExpectedRealizedDataPoint(
    val dte: Int,
    val date: String,
    val expectedMove: Double,
    val realizedMove: Double,
    val isFavorable: Boolean,
)

data class ExpectedRealizedResponse(
    val data: List<ExpectedRealizedDataPoint>,
    val ticker: String = "",
)

data class GammaBar(
    val strike: Double,
    val gammaExposure: Double,
)

data class GammaExposureResponse(
    val bars: List<GammaBar>,
    val resistanceLevels: List<Double>,
    val currentPrice: Double,
    val ticker: String = "",
)

data class ProbabilityBar(
    val strike: Double,
    val probability: Double,
)

data class ProbabilityAssignmentResponse(
    val bars: List<ProbabilityBar>,
    val selectedStrike: Double,
    val summaryText: String,
    val ticker: String = "",
)

data class CompanyRecord(
    val ticker: String,
    val company: String,
    val sector: String,
)
