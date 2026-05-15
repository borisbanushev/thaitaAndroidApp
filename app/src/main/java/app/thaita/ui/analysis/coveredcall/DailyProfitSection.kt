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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.DailyProfitResponse
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors
import app.thaita.ui.theme.ThaitaGreen

@Composable
fun DailyProfitSection(
    data: DailyProfitResponse,
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
            text = "Average Daily Profit",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = thaitaColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (data.data.isEmpty()) {
            Text(
                text = "No daily profit data available",
                style = MaterialTheme.typography.bodyMedium,
                color = thaitaColors.axisMuted,
            )
            return@GlassmorphismCard
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            val progress = animProgress.value
            val points = data.data
            val maxValue = points.maxOf { it.profitPerDay.toFloat() }.coerceAtLeast(0.01f)
            val barCount = points.size
            val spacing = 4f
            val barWidth = ((size.width - spacing * (barCount + 1)) / barCount).coerceAtLeast(4f)
            val chartHeight = size.height - 20f

            points.forEachIndexed { i, pt ->
                val barHeight = (pt.profitPerDay.toFloat() / maxValue * chartHeight * progress)
                    .coerceAtLeast(0f)
                val x = spacing + i * (barWidth + spacing)
                val y = chartHeight - barHeight

                drawRect(
                    color = ThaitaGreen,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                )
            }
        }
    }
}
