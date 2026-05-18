package app.thaita.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.thaita.domain.model.TextFormatting
import app.thaita.domain.model.TextSegment
import app.thaita.ui.theme.*

@Composable
fun StreamingResponseBox(
    textSegments: List<TextSegment>,
    isStreaming: Boolean,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    minimal: Boolean = false,
) {
    val thaitaColors = LocalThaitaColors.current
    val shape = RoundedCornerShape(16.dp)
    val gradientBrush = Brush.linearGradient(listOf(GradientStart, GradientEnd))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                width = 3.dp,
                brush = gradientBrush,
                shape = shape,
            )
            .background(thaitaColors.cardBg)
            .animateContentSize()
            .padding(16.dp),
    ) {
        Column {
            if (onClose != null && !minimal) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = thaitaColors.axisMuted,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            val annotatedText = buildAnnotatedString {
                textSegments.forEach { segment ->
                    val style = when {
                        segment.isGradient -> SpanStyle(
                            brush = gradientBrush,
                            fontWeight = FontWeight.Bold,
                        )
                        segment.formatting == TextFormatting.RED_BOLD -> SpanStyle(
                            color = thaitaColors.red,
                            fontWeight = FontWeight.Bold,
                        )
                        segment.formatting == TextFormatting.GREEN_BOLD -> SpanStyle(
                            color = thaitaColors.green,
                            fontWeight = FontWeight.Bold,
                        )
                        segment.formatting == TextFormatting.YELLOW ||
                        segment.formatting == TextFormatting.YELLOW_BOLD -> SpanStyle(
                            color = thaitaColors.yellow,
                            fontWeight = if (segment.formatting == TextFormatting.YELLOW_BOLD) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        )
                        segment.formatting == TextFormatting.BOLD ||
                        segment.formatting == TextFormatting.BOLD_YELLOW -> SpanStyle(
                            fontWeight = FontWeight.Bold,
                        )
                        segment.formatting == TextFormatting.TICKER -> SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = thaitaColors.blue,
                        )
                        else -> SpanStyle(color = thaitaColors.textPrimary)
                    }
                    withStyle(style) {
                        append(segment.text)
                    }
                }
            }

            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            // Streaming indicator
            AnimatedVisibility(
                visible = isStreaming,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 2.dp,
                        color = thaitaColors.brandRed,
                    )
                    Text(
                        text = "Generating...",
                        style = MaterialTheme.typography.bodySmall,
                        color = thaitaColors.axisMuted,
                    )
                }
            }
        }
    }
}
