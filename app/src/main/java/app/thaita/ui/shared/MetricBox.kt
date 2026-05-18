package app.thaita.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.thaita.ui.theme.LocalThaitaColors

data class MetricBoxData(
    val label: String,
    val value: String,
    val infoText: String? = null,
    val valueColor: androidx.compose.ui.graphics.Color? = null,
)

@Composable
fun MetricBox(
    data: MetricBoxData,
    modifier: Modifier = Modifier,
) {
    val thaitaColors = LocalThaitaColors.current
    var showInfo by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(thaitaColors.cardBg)
            .border(1.dp, thaitaColors.glassBorder, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = data.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                ),
                color = thaitaColors.axisMuted,
            )
            if (data.infoText != null) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { showInfo = !showInfo },
                    tint = thaitaColors.axisMuted,
                )
            }
        }

        Text(
            text = data.value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            color = data.valueColor ?: thaitaColors.textPrimary,
        )

        AnimatedVisibility(visible = showInfo) {
            Text(
                text = data.infoText ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = thaitaColors.textSecondary,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
fun MetricsRow(
    metrics: List<MetricBoxData>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        metrics.forEach { metric ->
            MetricBox(
                data = metric,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
