package app.thaita.ui.analysis.coveredcall

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors

@Composable
fun StrikeSlider(
    strikes: List<Double>,
    selectedStrike: Double,
    recommendedStrike: Double,
    currentPrice: Double,
    onStrikeSelected: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current

    if (strikes.isEmpty()) return

    val selectedIndex = strikes.indexOf(selectedStrike).takeIf { it >= 0 }
        ?: strikes.indexOfFirst { it >= recommendedStrike }.takeIf { it >= 0 }
        ?: 0

    GlassmorphismCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Strike Price",
            style = MaterialTheme.typography.labelLarge,
            color = thaitaColors.axisMuted,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$${String.format("%.0f", selectedStrike)}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = thaitaColors.textPrimary,
            )
            if (selectedStrike == recommendedStrike) {
                Text(
                    text = "Recommended",
                    style = MaterialTheme.typography.labelSmall,
                    color = thaitaColors.green,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = selectedIndex.toFloat(),
            onValueChange = { idx ->
                val i = idx.toInt().coerceIn(0, strikes.lastIndex)
                onStrikeSelected(strikes[i])
            },
            valueRange = 0f..strikes.lastIndex.toFloat(),
            steps = (strikes.size - 2).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = thaitaColors.brandRed,
                activeTrackColor = thaitaColors.brandRed,
                inactiveTrackColor = thaitaColors.glassBorder,
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "$${String.format("%.0f", strikes.first())}",
                style = MaterialTheme.typography.bodySmall,
                color = thaitaColors.axisMuted,
            )
            Text(
                text = "Current: $${String.format("%.2f", currentPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = thaitaColors.axisMuted,
            )
            Text(
                text = "$${String.format("%.0f", strikes.last())}",
                style = MaterialTheme.typography.bodySmall,
                color = thaitaColors.axisMuted,
            )
        }
    }
}
