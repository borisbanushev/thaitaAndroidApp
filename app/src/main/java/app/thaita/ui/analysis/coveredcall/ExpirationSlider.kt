package app.thaita.ui.analysis.coveredcall

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.ExpirationDate
import app.thaita.ui.shared.GlassmorphismCard
import app.thaita.ui.theme.LocalThaitaColors

@Composable
fun ExpirationSlider(
    expirations: List<ExpirationDate>,
    selectedExpiration: Int,
    recommendedExpiration: Int,
    onExpirationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current

    if (expirations.isEmpty()) return

    val selectedIndex = expirations.indexOfFirst { it.days == selectedExpiration }
        .takeIf { it >= 0 } ?: 0

    val selectedDate = expirations.getOrNull(selectedIndex)

    GlassmorphismCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Expiration Date",
            style = MaterialTheme.typography.labelLarge,
            color = thaitaColors.axisMuted,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = selectedDate?.dateFormatted ?: "${selectedExpiration} days",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = thaitaColors.textPrimary,
                )
                Text(
                    text = "${selectedExpiration} days to expiration",
                    style = MaterialTheme.typography.bodySmall,
                    color = thaitaColors.axisMuted,
                )
            }
            if (selectedExpiration == recommendedExpiration) {
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
                val i = idx.toInt().coerceIn(0, expirations.lastIndex)
                onExpirationSelected(expirations[i].days)
            },
            valueRange = 0f..expirations.lastIndex.toFloat(),
            steps = (expirations.size - 2).coerceAtLeast(0),
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
                text = expirations.first().dateFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = thaitaColors.axisMuted,
            )
            Text(
                text = expirations.last().dateFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = thaitaColors.axisMuted,
            )
        }
    }
}
