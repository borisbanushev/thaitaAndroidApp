package app.thaita.domain.model

data class ChatMessage(
    val role: String,
    val content: String,
)

data class ChatContext(
    val ticker: String? = null,
    val companyName: String? = null,
    val currentPrice: Double? = null,
    val selectedStrike: Double? = null,
    val selectedExpiration: Int? = null,
    val premium: Double? = null,
    val metrics: Metrics? = null,
    val rrAnalysis: Map<String, Any?>? = null,
    val dailyProfit: Map<String, Any?>? = null,
    val expectedRealized: Map<String, Any?>? = null,
    val gammaExposure: Map<String, Any?>? = null,
    val probabilityAssignment: Map<String, Any?>? = null,
    val recommendation: Map<String, Any?>? = null,
)

data class ChatRequest(
    val messages: List<ChatMessage>,
    val context: ChatContext? = null,
)

enum class WidgetAction {
    COVERED_CALL,
    HEDGE,
    FAIR_VALUE,
    LEAPS,
    STOCK_NEWS,
    EARNINGS,
    INSTITUTIONAL_FLOW;

    fun toApiString(): String = when (this) {
        COVERED_CALL -> "coveredcall"
        HEDGE -> "hedge"
        FAIR_VALUE -> "fairvalue"
        LEAPS -> "leaps"
        STOCK_NEWS -> "stocknews"
        EARNINGS -> "earnings"
        INSTITUTIONAL_FLOW -> "institutional_flow"
    }

    companion object {
        fun fromString(s: String): WidgetAction = when (s.lowercase()) {
            "coveredcall" -> COVERED_CALL
            "hedge" -> HEDGE
            "fairvalue" -> FAIR_VALUE
            "leaps" -> LEAPS
            "stocknews" -> STOCK_NEWS
            "earnings" -> EARNINGS
            "institutional_flow" -> INSTITUTIONAL_FLOW
            else -> COVERED_CALL
        }
    }
}

data class TextSegment(
    val text: String,
    val isGradient: Boolean = false,
    val formatting: TextFormatting? = null,
    val isImage: Boolean = false,
    val imagePath: String? = null,
)

enum class TextFormatting {
    RED_BOLD,
    GREEN_BOLD,
    YELLOW,
    YELLOW_BOLD,
    BOLD,
    BOLD_YELLOW,
    TICKER,
}
