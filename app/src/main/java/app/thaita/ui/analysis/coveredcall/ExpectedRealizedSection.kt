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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.ExpectedRealizedResponse
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors
import app.thaita.ui.theme.ThaitaGreen
import app.thaita.ui.theme.ThaitaRed
import app.thaita.ui.theme.ThaitaYellow

@Composable
fun ExpectedRealizedSection(
    data: ExpectedRealizedResponse,
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
            text = "Expected vs Realized Move",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = thaitaColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (data.data.isEmpty()) {
            Text(
                text = "No expected vs realized data available",
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
            val points = data.data
            val maxValue = points.maxOf {
                maxOf(it.expectedMove.toFloat(), it.realizedMove.toFloat())
            }.coerceAtLeast(0.01f)

            val barCount = points.size
            val spacing = 6f
            val barWidth = ((size.width - spacing * (barCount + 1)) / barCount).coerceAtLeast(8f)
            val chartHeight = size.height - 24f

            points.forEachIndexed { i, pt ->
                val x = spacing + i * (barWidth + spacing)

                // Expected move bar
                val expectedHeight = (pt.expectedMove.toFloat() / maxValue * chartHeight * progress)
                    .coerceAtLeast(0f)
                val expectedY = chartHeight - expectedHeight

                drawRect(
                    color = ThaitaYellow.copy(alpha = 0.6f),
                    topLeft = Offset(x, expectedY),
                    size = Size(barWidth, expectedHeight),
                )

                // Realized move dot
                val realizedY = chartHeight - (pt.realizedMove.toFloat() / maxValue * chartHeight * progress)
                val dotColor = if (pt.isFavorable) ThaitaGreen else ThaitaRed

                drawCircle(
                    color = dotColor,
                    radius = 5f,
                    center = Offset(x + barWidth / 2, realizedY),
                )
            }
        }
    }
}
