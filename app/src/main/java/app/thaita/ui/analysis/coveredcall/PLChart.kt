package app.thaita.ui.analysis.coveredcall

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.PLDataPoint
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors
import app.thaita.ui.theme.ThaitaGreen
import app.thaita.ui.theme.ThaitaRed

@Composable
fun PLChart(
    data: List<PLDataPoint>,
    currentPrice: Float,
    premium: Float,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(1200))
    }

    GlassmorphismCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Covered Call P/L",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = thaitaColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) {
            if (data.isEmpty()) return@Canvas

            val progress = animationProgress.value
            val visibleCount = (data.size * progress).toInt().coerceAtLeast(1)
            val visibleData = data.take(visibleCount)

            val minX = data.first().stockPrice.toFloat()
            val maxX = data.last().stockPrice.toFloat()
            val plValues = data.map { it.plExpiration.toFloat() }
            val minY = plValues.min()
            val maxY = plValues.max()
            val yRange = (maxY - minY).coerceAtLeast(1f)

            val chartLeft = 0f
            val chartRight = size.width
            val chartTop = 16f
            val chartBottom = size.height - 16f
            val chartHeight = chartBottom - chartTop

            fun mapX(x: Float) = chartLeft + (x - minX) / (maxX - minX) * (chartRight - chartLeft)
            fun mapY(y: Float) = chartBottom - (y - minY) / yRange * chartHeight

            val zeroY = mapY(0f)

            // Draw zero line
            drawLine(
                color = thaitaColors.axisMuted.copy(alpha = 0.3f),
                start = Offset(chartLeft, zeroY),
                end = Offset(chartRight, zeroY),
                strokeWidth = 1f,
            )

            // Build P/L path
            val linePath = Path()
            val greenFillPath = Path()
            val redFillPath = Path()

            visibleData.forEachIndexed { i, point ->
                val x = mapX(point.stockPrice.toFloat())
                val y = mapY(point.plExpiration.toFloat())

                if (i == 0) {
                    linePath.moveTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                }

                // Green fill (above zero)
                if (point.plExpiration >= 0) {
                    if (i == 0 || data[i - 1].plExpiration < 0) {
                        greenFillPath.moveTo(x, zeroY)
                    }
                    greenFillPath.lineTo(x, y)
                    if (i == visibleData.lastIndex || (i < data.lastIndex && data[i + 1].plExpiration < 0)) {
                        greenFillPath.lineTo(x, zeroY)
                        greenFillPath.close()
                    }
                }

                // Red fill (below zero)
                if (point.plExpiration < 0) {
                    if (i == 0 || data[i - 1].plExpiration >= 0) {
                        redFillPath.moveTo(x, zeroY)
                    }
                    redFillPath.lineTo(x, y)
                    if (i == visibleData.lastIndex || (i < data.lastIndex && data[i + 1].plExpiration >= 0)) {
                        redFillPath.lineTo(x, zeroY)
                        redFillPath.close()
                    }
                }
            }

            // Draw green gradient fill
            drawPath(
                path = greenFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ThaitaGreen.copy(alpha = 0.4f),
                        ThaitaGreen.copy(alpha = 0f),
                    ),
                    startY = chartTop,
                    endY = zeroY,
                ),
            )

            // Draw red gradient fill
            drawPath(
                path = redFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ThaitaRed.copy(alpha = 0f),
                        ThaitaRed.copy(alpha = 0.4f),
                    ),
                    startY = zeroY,
                    endY = chartBottom,
                ),
            )

            // Draw the P/L line
            drawPath(
                path = linePath,
                color = ThaitaGreen,
                style = Stroke(width = 2.5f, cap = StrokeCap.Round),
            )

            // Current price marker
            val cpX = mapX(currentPrice)
            drawLine(
                color = thaitaColors.axisMuted.copy(alpha = 0.5f),
                start = Offset(cpX, chartTop),
                end = Offset(cpX, chartBottom),
                strokeWidth = 1f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(8f, 4f),
                ),
            )
        }
    }
}
