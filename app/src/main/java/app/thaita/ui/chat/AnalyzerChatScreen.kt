package app.thaita.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.thaita.domain.model.WidgetAction
import app.thaita.ui.analysis.coveredcall.CoveredCallScreen
import app.thaita.ui.shared.GradientText
import app.thaita.ui.theme.GradientEnd
import app.thaita.ui.theme.GradientStart
import app.thaita.ui.theme.LocalThaitaColors

data class UseCaseItem(
    val action: WidgetAction,
    val label: String,
)

val USE_CASES = listOf(
    UseCaseItem(WidgetAction.COVERED_CALL, "earn safe income from the stock you already own"),
    UseCaseItem(WidgetAction.HEDGE, "protect your portfolio against market volatility"),
    UseCaseItem(WidgetAction.FAIR_VALUE, "evaluate if a company is currently undervalued"),
    UseCaseItem(WidgetAction.LEAPS, "profit from a stock upside without buying shares"),
    UseCaseItem(WidgetAction.STOCK_NEWS, "get the latest news for a company"),
    UseCaseItem(WidgetAction.EARNINGS, "profit from quarterly earnings announcements"),
    UseCaseItem(WidgetAction.INSTITUTIONAL_FLOW, "see where large institutions trade a company"),
)

@Composable
fun AnalyzerChatScreen(
    onLogout: () -> Unit,
    viewModel: AnalyzerChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val thaitaColors = LocalThaitaColors.current
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            FloatingNavBar(
                onNewChat = viewModel::handleNewChat,
                onUserClick = onLogout,
                userName = state.user?.username,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Greeting with typewriter effect
            if (state.greetingSegments.isNotEmpty()) {
                val annotatedText = buildAnnotatedString {
                    state.greetingSegments.forEach { segment ->
                        val style = when {
                            segment.isGradient -> SpanStyle(
                                brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                                fontWeight = FontWeight.Bold,
                            )
                            segment.formatting == app.thaita.domain.model.TextFormatting.GREEN_BOLD ->
                                SpanStyle(
                                    color = thaitaColors.green,
                                    fontWeight = FontWeight.Bold,
                                )
                            else -> SpanStyle(
                                color = thaitaColors.textPrimary,
                            )
                        }
                        withStyle(style) { append(segment.text) }
                    }
                }

                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            // Use case links (only if no action selected yet)
            AnimatedVisibility(
                visible = state.isGreetingComplete && state.selectedAction == null,
                enter = fadeIn() + expandVertically(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Only show Covered Call for this first implementation
                    USE_CASES.filter { it.action == WidgetAction.COVERED_CALL }.forEach { useCase ->
                        UseCaseLink(
                            label = useCase.label,
                            onClick = { viewModel.selectAction(useCase.action) },
                        )
                    }
                    // Show others as coming soon
                    USE_CASES.filter { it.action != WidgetAction.COVERED_CALL }.forEach { useCase ->
                        UseCaseLinkDisabled(label = useCase.label)
                    }
                }
            }

            // Analysis widget area
            AnimatedVisibility(
                visible = state.selectedAction != null,
                enter = fadeIn() + expandVertically(),
            ) {
                when (state.selectedAction) {
                    WidgetAction.COVERED_CALL -> {
                        CoveredCallScreen(
                            contextTicker = state.contextTicker,
                            companies = state.companies,
                            onTickerSelected = viewModel::onTickerSelected,
                        )
                    }
                    else -> {
                        // Placeholder for future use cases
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        ) {
                            Text(
                                text = "Coming soon!",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }

            // Follow-up chat section
            if (state.selectedAction != null && state.contextTicker != null) {
                FollowUpChatSection(
                    messages = state.followUpMessages,
                    isStreaming = state.isFollowUpStreaming,
                    onSendMessage = { text ->
                        viewModel.sendFollowUpMessage(
                            text,
                            app.thaita.domain.model.ChatContext(
                                ticker = state.contextTicker,
                            ),
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun UseCaseLink(
    label: String,
    onClick: () -> Unit,
) {
    val thaitaColors = LocalThaitaColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = thaitaColors.cardBg,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "I want to ",
                style = MaterialTheme.typography.bodyMedium,
                color = thaitaColors.textSecondary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                ),
            )
        }
    }
}

@Composable
private fun UseCaseLinkDisabled(label: String) {
    val thaitaColors = LocalThaitaColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = thaitaColors.cardBg.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "I want to $label",
                style = MaterialTheme.typography.bodyMedium,
                color = thaitaColors.axisMuted,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Soon",
                style = MaterialTheme.typography.labelSmall,
                color = thaitaColors.axisMuted,
            )
        }
    }
}

@Composable
private fun FollowUpChatSection(
    messages: List<app.thaita.domain.model.ChatMessage>,
    isStreaming: Boolean,
    onSendMessage: (String) -> Unit,
) {
    val thaitaColors = LocalThaitaColors.current
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Divider(color = thaitaColors.glassBorder)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ask a follow-up question",
            style = MaterialTheme.typography.titleSmall,
            color = thaitaColors.textSecondary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Messages
        messages.forEach { msg ->
            val isUser = msg.role == "user"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) {
                        thaitaColors.textPrimary.copy(alpha = 0.05f)
                    } else {
                        thaitaColors.cardBg
                    },
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = msg.content,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = thaitaColors.textPrimary,
                )
            }
        }

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your question...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isStreaming,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (text.isNotBlank()) {
                            focusManager.clearFocus()
                            onSendMessage(text.trim())
                            text = ""
                        }
                    },
                ),
            )
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        focusManager.clearFocus()
                        onSendMessage(text.trim())
                        text = ""
                    }
                },
                enabled = text.isNotBlank() && !isStreaming,
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank()) thaitaColors.brandRed else thaitaColors.axisMuted,
                )
            }
        }
    }
}
