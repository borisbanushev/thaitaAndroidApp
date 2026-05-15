package app.thaita.data.repository

import app.thaita.data.remote.StreamingService
import app.thaita.data.remote.ThaitaApiService
import app.thaita.data.remote.dto.CalculatePLRequestDto
import app.thaita.data.remote.dto.MetricsRequestDto
import app.thaita.domain.model.*
import app.thaita.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val api: ThaitaApiService,
    private val streamingService: StreamingService,
) : AnalysisRepository {

    override suspend fun getCompanies(): Result<List<CompanyRecord>> = runCatching {
        api.getCompanies().companies.map {
            CompanyRecord(ticker = it.ticker, company = it.company, sector = it.sector)
        }
    }

    override suspend fun getStrikes(ticker: String): Result<Pair<List<Double>, Double>> =
        runCatching {
            val r = api.getStrikes(ticker)
            r.strikes to r.currentPrice
        }

    override suspend fun getExpirations(ticker: String): Result<List<ExpirationDate>> =
        runCatching {
            api.getExpirations(ticker).expirations.map {
                ExpirationDate(days = it.days, date = it.date, dateFormatted = it.dateFormatted)
            }
        }

    override suspend fun getRecommended(ticker: String): Result<Triple<Double, Int, Double?>> =
        runCatching {
            val r = api.getRecommended(ticker)
            Triple(r.strike, r.expirationDays, r.premium)
        }

    override suspend fun calculatePL(
        ticker: String,
        strike: Double,
        expirationDays: Int,
    ): Result<PLResponse> = runCatching {
        val r = api.calculatePL(CalculatePLRequestDto(ticker, strike, expirationDays))
        PLResponse(
            plData = r.plData.map {
                PLDataPoint(
                    stockPrice = it.stockPrice,
                    plExpiration = it.plExpiration,
                    pl5Days = it.pl5Days,
                    pl10Days = it.pl10Days,
                    pl20Days = it.pl20Days,
                    delta = it.delta,
                    gamma = it.gamma,
                    volume = it.volume,
                )
            },
            premium = r.premium,
            currentPrice = r.currentPrice,
        )
    }

    override suspend fun getMetrics(
        ticker: String,
        strike: Double,
        expirationDays: Int,
    ): Result<Metrics> = runCatching {
        val r = api.getMetrics(MetricsRequestDto(ticker, strike, expirationDays))
        Metrics(
            premium = r.premium,
            maxProfit = r.maxProfit,
            probabilityOfAssignment = r.probabilityOfAssignment,
            annualizedReturn = r.annualizedReturn,
            delta = r.delta,
            gamma = r.gamma,
            vega = r.vega,
            theta = r.theta,
            info = r.info.mapValues { (_, v) ->
                MetricInfo(description = v.description, interpretation = v.interpretation)
            },
        )
    }

    override suspend fun getStockInfo(ticker: String): Result<StockInfo> = runCatching {
        val r = api.getStockInfo(ticker)
        StockInfo(
            ticker = r.ticker,
            companyName = r.companyName,
            sector = r.sector,
            price = r.price,
        )
    }

    override suspend fun getRRAnalysis(ticker: String): Result<RRAnalysisResponse> = runCatching {
        val r = api.getRRAnalysis(ticker)
        RRAnalysisResponse(
            data = r.data.map {
                RRDataPoint(
                    ticker = it.ticker, bid = it.bid, delta = it.delta,
                    impliedVolatility = it.impliedVolatility, rrCoeff = it.rrCoeff,
                    expirationDate = it.expirationDate,
                    strikePctOverPrice = it.strikePctOverPrice,
                    strikePrice = it.strikePrice,
                )
            },
            tableData = r.tableData.map {
                RRTableRow(
                    expirationDate = it.expirationDate, strikePrice = it.strikePrice,
                    delta = it.delta, bid = it.bid,
                    impliedVolatility = it.impliedVolatility, rrCoeff = it.rrCoeff,
                )
            },
            regression = RRRegression(slope = r.regression.slope, intercept = r.regression.intercept),
        )
    }

    override suspend fun getDailyProfit(ticker: String): Result<DailyProfitResponse> = runCatching {
        val r = api.getDailyProfit(ticker)
        DailyProfitResponse(
            data = r.data.map {
                DailyProfitDataPoint(
                    dte = it.dte, date = it.date, dateFormatted = it.dateFormatted,
                    profitPerDay = it.profitPerDay, totalCallsVolume = it.totalCallsVolume,
                )
            },
            ticker = r.ticker,
        )
    }

    override suspend fun getExpectedRealizedMove(ticker: String): Result<ExpectedRealizedResponse> =
        runCatching {
            val r = api.getExpectedRealizedMove(ticker)
            ExpectedRealizedResponse(
                data = r.data.map {
                    ExpectedRealizedDataPoint(
                        dte = it.dte, date = it.date,
                        expectedMove = it.expectedMove, realizedMove = it.realizedMove,
                        isFavorable = it.isFavorable,
                    )
                },
                ticker = r.ticker,
            )
        }

    override suspend fun getGammaExposure(ticker: String): Result<GammaExposureResponse> =
        runCatching {
            val r = api.getGammaExposure(ticker)
            GammaExposureResponse(
                bars = r.bars.map { GammaBar(strike = it.strike, gammaExposure = it.gammaExposure) },
                resistanceLevels = r.resistanceLevels,
                currentPrice = r.currentPrice,
                ticker = r.ticker,
            )
        }

    override suspend fun getProbabilityAssignment(ticker: String): Result<ProbabilityAssignmentResponse> =
        runCatching {
            val r = api.getProbabilityAssignment(ticker)
            ProbabilityAssignmentResponse(
                bars = r.bars.map { ProbabilityBar(strike = it.strike, probability = it.probability) },
                selectedStrike = r.selectedStrike,
                summaryText = r.summaryText,
                ticker = r.ticker,
            )
        }

    override fun streamExplanation(requestBody: String): Flow<String> =
        streamingService.streamExplanation(requestBody)

    override fun streamRRExplanation(requestBody: String): Flow<String> =
        streamingService.streamRRExplanation(requestBody)

    override fun streamDailyProfitExplanation(requestBody: String): Flow<String> =
        streamingService.streamDailyProfitExplanation(requestBody)

    override fun streamExpectedRealizedExplanation(requestBody: String): Flow<String> =
        streamingService.streamExpectedRealizedExplanation(requestBody)

    override fun streamProbabilityAssignmentExplanation(requestBody: String): Flow<String> =
        streamingService.streamProbabilityAssignmentExplanation(requestBody)

    override fun streamGammaExposureExplanation(requestBody: String): Flow<String> =
        streamingService.streamGammaExposureExplanation(requestBody)

    override fun streamChat(requestBody: String): Flow<String> =
        streamingService.streamChat(requestBody)
}
