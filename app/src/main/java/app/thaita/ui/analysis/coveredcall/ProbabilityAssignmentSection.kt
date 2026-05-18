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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.ProbabilityAssignmentResponse
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.*

@Composable
fun ProbabilityAssignmentSection(
    data: ProbabilityAssignmentResponse,
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
            text = "Probability of Assignment",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = thaitaColors.textPrimary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descriptive text with highlighted "Probability of Assignment"
        val annotatedText = buildAnnotatedString {
            withStyle(SpanStyle(color = ThaitaYellow, fontWeight = FontWeight.Bold)) {
                append("Probability of Assignment")
            }
            withStyle(SpanStyle(color = thaitaColors.textSecondary)) {
                append(" shows us how the call premium is affected by the current volatility. Lower probability allows us to keep the stock while collecting premium.")
            }
        }

        Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (data.bars.isEmpty()) {
            Text(
                text = "No probability data available",
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
            val bars = data.bars
            val barCount = bars.size
            val spacing = 4f
            val barWidth = ((size.width - spacing * (barCount + 1)) / barCount).coerceAtLeast(6f)
            val chartHeight = size.height - 20f
            val maxProb = 100f

            bars.forEachIndexed { i, bar ->
                val barHeight = (bar.probability.toFloat() / maxProb * chartHeight * progress)
                    .coerceAtLeast(0f)
                val x = spacing + i * (barWidth + spacing)
                val y = chartHeight - barHeight

                val isSelected = bar.strike == data.selectedStrike
                val color = if (isSelected) ThaitaBrandRed else ThaitaGreen.copy(alpha = 0.7f)

                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                )
            }
        }
    }
}
