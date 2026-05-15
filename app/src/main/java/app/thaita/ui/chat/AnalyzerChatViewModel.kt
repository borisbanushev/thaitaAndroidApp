package app.thaita.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thaita.domain.model.*
import app.thaita.domain.repository.AnalysisRepository
import app.thaita.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyzerChatUiState(
    val greetingSegments: List<TextSegment> = emptyList(),
    val isGreetingComplete: Boolean = false,
    val selectedAction: WidgetAction? = null,
    val contextTicker: String? = null,
    val user: User? = null,
    val companies: List<CompanyRecord> = emptyList(),
    val followUpMessages: List<ChatMessage> = emptyList(),
    val isFollowUpStreaming: Boolean = false,
)

@HiltViewModel
class AnalyzerChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val analysisRepository: AnalysisRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyzerChatUiState())
    val uiState: StateFlow<AnalyzerChatUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        loadCompanies()
        startGreeting()
    }

    private fun loadUser() {
        val cached = authRepository.getCachedUser()
        _uiState.value = _uiState.value.copy(user = cached)
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            analysisRepository.getCompanies().onSuccess { companies ->
                _uiState.value = _uiState.value.copy(companies = companies)
            }
        }
    }

    fun startGreeting() {
        viewModelScope.launch {
            val name = _uiState.value.user?.username ?: _uiState.value.user?.fullName ?: "there"
            val greeting = buildGreetingText(name)
            val segments = mutableListOf<TextSegment>()

            greeting.forEach { segment ->
                // Simulate typewriter: add chars progressively
                val chars = segment.text.toCharArray()
                val currentSegments = segments.toMutableList()
                var partialText = ""
                for (char in chars) {
                    partialText += char
                    val updated = currentSegments.toMutableList()
                    updated.add(segment.copy(text = partialText))
                    _uiState.value = _uiState.value.copy(greetingSegments = updated)
                    delay(15) // typewriter speed
                }
                segments.add(segment)
                _uiState.value = _uiState.value.copy(greetingSegments = segments.toList())
            }
            _uiState.value = _uiState.value.copy(isGreetingComplete = true)
        }
    }

    private fun buildGreetingText(name: String): List<TextSegment> = listOf(
        TextSegment(text = "Hi $name! I'm "),
        TextSegment(text = "thaita", isGradient = true),
        TextSegment(text = ", your options strategy assistant. I help you generate "),
        TextSegment(text = "safe, recurring income", formatting = TextFormatting.GREEN_BOLD),
        TextSegment(text = " from your existing stock holdings.\n\nWhat would you like to do?\n\n"),
    )

    fun selectAction(action: WidgetAction) {
        _uiState.value = _uiState.value.copy(selectedAction = action)
    }

    fun onTickerSelected(ticker: String) {
        _uiState.value = _uiState.value.copy(contextTicker = ticker)
    }

    fun handleNewChat() {
        _uiState.value = _uiState.value.copy(
            selectedAction = null,
            contextTicker = null,
            greetingSegments = emptyList(),
            isGreetingComplete = false,
            followUpMessages = emptyList(),
        )
        startGreeting()
    }

    fun sendFollowUpMessage(text: String, context: ChatContext?) {
        val current = _uiState.value.followUpMessages.toMutableList()
        current.add(ChatMessage(role = "user", content = text))
        _uiState.value = _uiState.value.copy(
            followUpMessages = current,
            isFollowUpStreaming = true,
        )

        viewModelScope.launch {
            val request = kotlinx.serialization.json.Json.encodeToString(
                app.thaita.data.remote.dto.ChatRequestDto.serializer(),
                app.thaita.data.remote.dto.ChatRequestDto(
                    messages = current.map {
                        app.thaita.data.remote.dto.ChatMessageDto(role = it.role, content = it.content)
                    },
                    context = context?.let {
                        app.thaita.data.remote.dto.ChatContextDto(
                            ticker = it.ticker,
                            companyName = it.companyName,
                            currentPrice = it.currentPrice,
                            selectedStrike = it.selectedStrike,
                            selectedExpiration = it.selectedExpiration,
                            premium = it.premium,
                        )
                    },
                ),
            )

            val sb = StringBuilder()
            try {
                analysisRepository.streamChat(request).collect { chunk ->
                    sb.append(chunk)
                    val msgs = _uiState.value.followUpMessages.toMutableList()
                    // Replace or add assistant message
                    val lastIdx = msgs.indexOfLast { it.role == "assistant" }
                    if (lastIdx >= 0 && lastIdx == msgs.lastIndex) {
                        msgs[lastIdx] = ChatMessage(role = "assistant", content = sb.toString())
                    } else {
                        msgs.add(ChatMessage(role = "assistant", content = sb.toString()))
                    }
                    _uiState.value = _uiState.value.copy(followUpMessages = msgs)
                }
            } catch (_: Exception) {
                // stream ended or error
            }
            _uiState.value = _uiState.value.copy(isFollowUpStreaming = false)
        }
    }
}
