package app.thaita.ui.analysis.coveredcall

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.RRAnalysisResponse
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors
import app.thaita.ui.theme.ThaitaGreen

@Composable
fun RRAnalysisSection(
    data: RRAnalysisResponse,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current

    GlassmorphismCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Risk-weighted Return Analysis",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = thaitaColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Table
        if (data.tableData.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TableHeader("Strike", Modifier.width(80.dp))
                    TableHeader("RR Coeff", Modifier.width(80.dp))
                    TableHeader("Delta", Modifier.width(70.dp))
                    TableHeader("Expiration", Modifier.width(100.dp))
                    TableHeader("Bid", Modifier.width(60.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                data.tableData.take(5).forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        TableCell("$${String.format("%.0f", row.strikePrice)}", Modifier.width(80.dp))
                        TableCell(String.format("%.3f", row.rrCoeff), Modifier.width(80.dp))
                        TableCell(String.format("%.3f", row.delta), Modifier.width(70.dp))
                        TableCell(row.expirationDate, Modifier.width(100.dp))
                        TableCell("$${String.format("%.2f", row.bid)}", Modifier.width(60.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scatter plot
        RRScatterChart(data = data)
    }
}

@Composable
private fun TableHeader(text: String, modifier: Modifier = Modifier) {
    val thaitaColors = LocalThaitaColors.current
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = thaitaColors.axisMuted,
        modifier = modifier,
    )
}

@Composable
private fun TableCell(text: String, modifier: Modifier = Modifier) {
    val thaitaColors = LocalThaitaColors.current
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = thaitaColors.textPrimary,
        modifier = modifier,
    )
}

@Composable
fun RRScatterChart(
    data: RRAnalysisResponse,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(800))
    }

    if (data.data.isEmpty()) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
    ) {
        val progress = animProgress.value
        val points = data.data

        val minX = points.minOf { it.rrCoeff.toFloat() }
        val maxX = points.maxOf { it.rrCoeff.toFloat() }
        val minY = points.minOf { it.bid.toFloat() }
        val maxY = points.maxOf { it.bid.toFloat() }

        val xRange = (maxX - minX).coerceAtLeast(0.001f)
        val yRange = (maxY - minY).coerceAtLeast(0.01f)

        val pad = 12f
        val cw = size.width - pad * 2
        val ch = size.height - pad * 2

        fun mapX(v: Float) = pad + (v - minX) / xRange * cw
        fun mapY(v: Float) = pad + ch - (v - minY) / yRange * ch

        // Regression line
        val regX1 = minX
        val regX2 = maxX
        val regY1 = (data.regression.slope * regX1 + data.regression.intercept).toFloat()
        val regY2 = (data.regression.slope * regX2 + data.regression.intercept).toFloat()

        drawLine(
            color = ThaitaGreen,
            start = Offset(mapX(regX1), mapY(regY1)),
            end = Offset(mapX(regX2) * progress, mapY(regY2)),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )

        // Points
        points.forEachIndexed { i, pt ->
            if (i.toFloat() / points.size <= progress) {
                val x = mapX(pt.rrCoeff.toFloat())
                val y = mapY(pt.bid.toFloat())

                // Color by moneyness (plasma-like)
                val moneyness = pt.strikePctOverPrice.toFloat()
                val pointColor = when {
                    moneyness < -5 -> Color(0xFF0D0887)
                    moneyness < 0 -> Color(0xFF7201A8)
                    moneyness < 5 -> Color(0xFFBD3786)
                    moneyness < 10 -> Color(0xFFED7953)
                    else -> Color(0xFFFDCA26)
                }

                drawCircle(
                    color = pointColor,
                    radius = 4f,
                    center = Offset(x, y),
                )
            }
        }
    }
}
