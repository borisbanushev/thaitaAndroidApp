package app.thaita.ui.analysis.coveredcall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thaita.domain.model.*
import app.thaita.domain.repository.AnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class CoveredCallUiState(
    val ticker: String = "",
    val strikes: List<Double> = emptyList(),
    val expirations: List<ExpirationDate> = emptyList(),
    val currentPrice: Double = 0.0,
    val selectedStrike: Double = 0.0,
    val selectedExpiration: Int = 0,
    val recommendedStrike: Double = 0.0,
    val recommendedExpiration: Int = 0,
    val plData: List<PLDataPoint> = emptyList(),
    val premium: Double = 0.0,
    val metrics: Metrics? = null,
    val stockInfo: StockInfo? = null,
    val rrAnalysis: RRAnalysisResponse? = null,
    val dailyProfit: DailyProfitResponse? = null,
    val expectedRealized: ExpectedRealizedResponse? = null,
    val gammaExposure: GammaExposureResponse? = null,
    val probabilityAssignment: ProbabilityAssignmentResponse? = null,
    val streamingSegments: List<TextSegment> = emptyList(),
    val isStreaming: Boolean = false,
    val isExplanationComplete: Boolean = false,
    val isLoading: Boolean = false,
    val hasData: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CoveredCallViewModel @Inject constructor(
    private val analysisRepository: AnalysisRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoveredCallUiState())
    val uiState: StateFlow<CoveredCallUiState> = _uiState.asStateFlow()

    private var streamingJob: Job? = null
    private val json = Json { ignoreUnknownKeys = true }

    fun loadTicker(ticker: String) {
        _uiState.value = _uiState.value.copy(
            ticker = ticker,
            isLoading = true,
            error = null,
            hasData = false,
        )

        viewModelScope.launch {
            try {
                // Load strikes
                val strikesResult = analysisRepository.getStrikes(ticker)
                strikesResult.onSuccess { (strikes, price) ->
                    _uiState.value = _uiState.value.copy(
                        strikes = strikes,
                        currentPrice = price,
                    )
                }

                // Load expirations
                val expResult = analysisRepository.getExpirations(ticker)
                expResult.onSuccess { expirations ->
                    _uiState.value = _uiState.value.copy(expirations = expirations)
                }

                // Load recommended
                val recResult = analysisRepository.getRecommended(ticker)
                recResult.onSuccess { (strike, days, _) ->
                    _uiState.value = _uiState.value.copy(
                        recommendedStrike = strike,
                        recommendedExpiration = days,
                        selectedStrike = strike,
                        selectedExpiration = days,
                    )
                    // Calculate P/L with recommended values
                    calculatePL(ticker, strike, days)
                    loadMetrics(ticker, strike, days)
                }

                // Load all analysis data in parallel
                launch { loadRRAnalysis(ticker) }
                launch { loadDailyProfit(ticker) }
                launch { loadExpectedRealized(ticker) }
                launch { loadGammaExposure(ticker) }
                launch { loadProbabilityAssignment(ticker) }
                launch { loadStockInfo(ticker) }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    private suspend fun calculatePL(ticker: String, strike: Double, days: Int) {
        analysisRepository.calculatePL(ticker, strike, days)
            .onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    plData = response.plData,
                    premium = response.premium,
                    currentPrice = response.currentPrice,
                    hasData = true,
                    isLoading = false,
                )
            }
            .onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
    }

    private suspend fun loadMetrics(ticker: String, strike: Double, days: Int) {
        analysisRepository.getMetrics(ticker, strike, days)
            .onSuccess { metrics ->
                _uiState.value = _uiState.value.copy(metrics = metrics)
            }
    }

    private suspend fun loadStockInfo(ticker: String) {
        analysisRepository.getStockInfo(ticker)
            .onSuccess { info ->
                _uiState.value = _uiState.value.copy(stockInfo = info)
            }
    }

    private suspend fun loadRRAnalysis(ticker: String) {
        analysisRepository.getRRAnalysis(ticker)
            .onSuccess { data ->
                _uiState.value = _uiState.value.copy(rrAnalysis = data)
            }
    }

    private suspend fun loadDailyProfit(ticker: String) {
        analysisRepository.getDailyProfit(ticker)
            .onSuccess { data ->
                _uiState.value = _uiState.value.copy(dailyProfit = data)
            }
    }

    private suspend fun loadExpectedRealized(ticker: String) {
        analysisRepository.getExpectedRealizedMove(ticker)
            .onSuccess { data ->
                _uiState.value = _uiState.value.copy(expectedRealized = data)
            }
    }

    private suspend fun loadGammaExposure(ticker: String) {
        analysisRepository.getGammaExposure(ticker)
            .onSuccess { data ->
                _uiState.value = _uiState.value.copy(gammaExposure = data)
            }
    }

    private suspend fun loadProbabilityAssignment(ticker: String) {
        analysisRepository.getProbabilityAssignment(ticker)
            .onSuccess { data ->
                _uiState.value = _uiState.value.copy(probabilityAssignment = data)
            }
    }

    fun onStrikeSelected(strike: Double) {
        _uiState.value = _uiState.value.copy(selectedStrike = strike)
        recalculate()
    }

    fun onExpirationSelected(days: Int) {
        _uiState.value = _uiState.value.copy(selectedExpiration = days)
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        if (state.ticker.isBlank() || state.selectedStrike == 0.0 || state.selectedExpiration == 0) return

        viewModelScope.launch {
            calculatePL(state.ticker, state.selectedStrike, state.selectedExpiration)
            loadMetrics(state.ticker, state.selectedStrike, state.selectedExpiration)
        }
    }

    fun generateExplanation() {
        val state = _uiState.value
        if (state.ticker.isBlank()) return

        streamingJob?.cancel()
        _uiState.value = _uiState.value.copy(
            streamingSegments = emptyList(),
            isStreaming = true,
            isExplanationComplete = false,
        )

        streamingJob = viewModelScope.launch {
            val requestBody = json.encodeToString(
                app.thaita.data.remote.dto.ExplanationRequestDto.serializer(),
                app.thaita.data.remote.dto.ExplanationRequestDto(
                    ticker = state.ticker,
                    strikePrice = state.selectedStrike,
                    expirationDays = state.selectedExpiration,
                    premium = state.premium,
                    currentPrice = state.currentPrice,
                    metrics = state.metrics?.let {
                        app.thaita.data.remote.dto.MetricsResponseDto(
                            premium = it.premium,
                            maxProfit = it.maxProfit,
                            probabilityOfAssignment = it.probabilityOfAssignment,
                            annualizedReturn = it.annualizedReturn,
                            delta = it.delta,
                            gamma = it.gamma,
                            vega = it.vega,
                            theta = it.theta,
                        )
                    },
                ),
            )

            val fullText = StringBuilder()
            try {
                analysisRepository.streamExplanation(requestBody).collect { chunk ->
                    fullText.append(chunk)
                    _uiState.value = _uiState.value.copy(
                        streamingSegments = parseSegments(fullText.toString()),
                    )
                }
            } catch (_: Exception) {
                // stream ended
            }
            _uiState.value = _uiState.value.copy(
                isStreaming = false,
                isExplanationComplete = true,
            )
        }
    }

    private fun parseSegments(text: String): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()
        var remaining = text
        val gradientPattern = Regex("\\[gradient\\](.*?)\\[/gradient\\]", RegexOption.DOT_MATCHES_ALL)
        val boldPattern = Regex("\\*\\*(.*?)\\*\\*")

        // Simple parser: split by [gradient] tags and ** bold markers
        while (remaining.isNotEmpty()) {
            val gradientMatch = gradientPattern.find(remaining)
            val boldMatch = boldPattern.find(remaining)

            val firstMatch = listOfNotNull(gradientMatch, boldMatch)
                .minByOrNull { it.range.first }

            if (firstMatch == null) {
                segments.add(TextSegment(text = remaining))
                break
            }

            if (firstMatch.range.first > 0) {
                segments.add(TextSegment(text = remaining.substring(0, firstMatch.range.first)))
            }

            when (firstMatch) {
                gradientMatch -> {
                    val inner = gradientMatch!!.groupValues[1]
                    segments.add(TextSegment(text = inner, isGradient = true))
                }
                boldMatch -> {
                    val inner = boldMatch!!.groupValues[1]
                    segments.add(TextSegment(text = inner, formatting = TextFormatting.BOLD))
                }
            }

            remaining = remaining.substring(firstMatch.range.last + 1)
        }

        return segments
    }
}
