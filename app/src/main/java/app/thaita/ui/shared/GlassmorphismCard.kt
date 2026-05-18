package app.thaita.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.thaita.ui.theme.LocalThaitaColors

@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val thaitaColors = LocalThaitaColors.current
    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .shadow(8.dp, shape)
            .clip(shape)
            .background(thaitaColors.glassBg)
            .border(1.dp, thaitaColors.glassBorder, shape)
            .padding(16.dp),
        content = content,
    )
}
