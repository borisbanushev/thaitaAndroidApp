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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.GammaExposureResponse
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors
import app.thaita.ui.theme.ThaitaGreen
import app.thaita.ui.theme.ThaitaRed

@Composable
fun GammaExposureSection(
    data: GammaExposureResponse,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(1000))
    }

    GlassmorphismCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Gamma Exposure",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = thaitaColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (data.bars.isEmpty()) {
            Text(
                text = "No gamma exposure data available",
                style = MaterialTheme.typography.bodyMedium,
                color = thaitaColors.axisMuted,
            )
            return@GlassmorphismCard
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            val progress = animProgress.value
            val bars = data.bars
            val maxGamma = bars.maxOf { it.gammaExposure.toFloat() }.coerceAtLeast(0.01f)
            val minStrike = bars.minOf { it.strike.toFloat() }
            val maxStrike = bars.maxOf { it.strike.toFloat() }
            val strikeRange = (maxStrike - minStrike).coerceAtLeast(1f)

            val barCount = bars.size
            val spacing = 3f
            val barWidth = ((size.width - spacing * (barCount + 1)) / barCount).coerceAtLeast(4f)
            val chartHeight = size.height - 20f

            bars.forEachIndexed { i, bar ->
                val barHeight = (bar.gammaExposure.toFloat() / maxGamma * chartHeight * progress)
                    .coerceAtLeast(0f)
                val x = spacing + i * (barWidth + spacing)
                val y = chartHeight - barHeight

                drawRect(
                    color = ThaitaGreen.copy(alpha = 0.7f),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                )
            }

            // Resistance levels (vertical red lines)
            data.resistanceLevels.forEach { level ->
                val normalized = (level.toFloat() - minStrike) / strikeRange
                val x = normalized * size.width

                drawLine(
                    color = ThaitaRed,
                    start = Offset(x, 0f),
                    end = Offset(x, chartHeight),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f)),
                )
            }
        }
    }
}
