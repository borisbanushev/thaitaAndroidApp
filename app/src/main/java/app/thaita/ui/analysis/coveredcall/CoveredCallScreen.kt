package app.thaita.ui.analysis.coveredcall

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.thaita.domain.model.CompanyRecord
import app.thaita.ui.shared.*
import app.thaita.ui.theme.LocalThaitaColors

@Composable
fun CoveredCallScreen(
    contextTicker: String?,
    companies: List<CompanyRecord>,
    onTickerSelected: (String) -> Unit,
    viewModel: CoveredCallViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val thaitaColors = LocalThaitaColors.current
    var tickerInput by remember { mutableStateOf(contextTicker ?: "") }

    LaunchedEffect(contextTicker) {
        if (contextTicker != null && contextTicker != state.ticker) {
            tickerInput = contextTicker
            viewModel.loadTicker(contextTicker)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Ticker input
        TickerAutocomplete(
            value = tickerInput,
            onValueChange = { tickerInput = it },
            onTickerSubmit = { ticker ->
                onTickerSelected(ticker)
                viewModel.loadTicker(ticker)
            },
            companies = companies,
        )

        // Loading indicator
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = thaitaColors.brandRed)
            }
        }

        // Error
        state.error?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = thaitaColors.red.copy(alpha = 0.1f),
                ),
            ) {
                Text(
                    text = error,
                    color = thaitaColors.red,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        // Main analysis content
        AnimatedVisibility(
            visible = state.hasData,
            enter = fadeIn() + expandVertically(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Stock info header
                state.stockInfo?.let { info ->
                    GlassmorphismCard {
                        Text(
                            text = "${info.companyName} (${info.ticker})",
                            style = MaterialTheme.typography.headlineMedium,
                            color = thaitaColors.textPrimary,
                        )
                        Text(
                            text = "$${String.format("%.2f", info.price)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = thaitaColors.green,
                        )
                    }
                }

                // Sliders
                if (state.strikes.isNotEmpty() && state.expirations.isNotEmpty()) {
                    StrikeSlider(
                        strikes = state.strikes,
                        selectedStrike = state.selectedStrike,
                        recommendedStrike = state.recommendedStrike,
                        currentPrice = state.currentPrice,
                        onStrikeSelected = viewModel::onStrikeSelected,
                    )

                    ExpirationSlider(
                        expirations = state.expirations,
                        selectedExpiration = state.selectedExpiration,
                        recommendedExpiration = state.recommendedExpiration,
                        onExpirationSelected = viewModel::onExpirationSelected,
                    )
                }

                // Top metrics: Premium, Max Profit, Prob of Assignment, Annualized Return
                state.metrics?.let { m ->
                    MetricsRow(
                        metrics = listOf(
                            MetricBoxData(
                                label = "PREMIUM",
                                value = "$${String.format("%.2f", m.premium)}",
                                infoText = m.info["premium"]?.description,
                            ),
                            MetricBoxData(
                                label = "MAX PROFIT",
                                value = "$${String.format("%.2f", m.maxProfit)}",
                                infoText = m.info["max_profit"]?.description,
                                valueColor = thaitaColors.green,
                            ),
                            MetricBoxData(
                                label = "PROB. ASSIGN",
                                value = "${String.format("%.1f", m.probabilityOfAssignment)}%",
                                infoText = m.info["probability_of_assignment"]?.description,
                            ),
                            MetricBoxData(
                                label = "ANN. RETURN",
                                value = "${String.format("%.1f", m.annualizedReturn)}%",
                                infoText = m.info["annualized_return"]?.description,
                                valueColor = thaitaColors.green,
                            ),
                        ),
                    )
                }

                // P/L Chart
                if (state.plData.isNotEmpty()) {
                    PLChart(
                        data = state.plData,
                        currentPrice = state.currentPrice.toFloat(),
                        premium = state.premium.toFloat(),
                    )
                }

                // Bottom metrics: Delta, Gamma, Vega, Theta
                state.metrics?.let { m ->
                    MetricsRow(
                        metrics = listOf(
                            MetricBoxData(
                                label = "DELTA",
                                value = String.format("%.4f", m.delta),
                                infoText = m.info["delta"]?.description,
                            ),
                            MetricBoxData(
                                label = "GAMMA",
                                value = String.format("%.4f", m.gamma),
                                infoText = m.info["gamma"]?.description,
                            ),
                            MetricBoxData(
                                label = "VEGA",
                                value = String.format("%.4f", m.vega),
                                infoText = m.info["vega"]?.description,
                            ),
                            MetricBoxData(
                                label = "THETA",
                                value = String.format("%.4f", m.theta),
                                infoText = m.info["theta"]?.description,
                            ),
                        ),
                    )
                }

                // "thaita generate" button
                ThaitaGenerateButton(
                    onClick = viewModel::generateExplanation,
                    isLoading = state.isStreaming,
                )

                // Streaming explanation
                if (state.streamingSegments.isNotEmpty()) {
                    StreamingResponseBox(
                        textSegments = state.streamingSegments,
                        isStreaming = state.isStreaming,
                        onClose = {},
                    )
                }

                // RR Analysis
                state.rrAnalysis?.let { rr ->
                    RRAnalysisSection(data = rr)
                }

                // Daily Profit
                state.dailyProfit?.let { dp ->
                    DailyProfitSection(data = dp)
                }

                // Expected vs Realized
                state.expectedRealized?.let { er ->
                    ExpectedRealizedSection(data = er)
                }

                // Gamma Exposure
                state.gammaExposure?.let { ge ->
                    GammaExposureSection(data = ge)
                }

                // Probability of Assignment
                state.probabilityAssignment?.let { pa ->
                    ProbabilityAssignmentSection(data = pa)
                }
            }
        }
    }
}
