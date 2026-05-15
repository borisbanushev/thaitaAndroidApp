package app.thaita.data.remote

import app.thaita.data.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import javax.inject.Inject

class StreamingService @Inject constructor(
    private val client: OkHttpClient,
    private val tokenManager: TokenManager,
    private val baseUrl: String,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun streamExplanation(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/generate-explanation", requestBody)

    fun streamRRExplanation(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/generate-rr-explanation", requestBody)

    fun streamDailyProfitExplanation(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/generate-daily-profit-explanation", requestBody)

    fun streamExpectedRealizedExplanation(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/generate-expected-realized-explanation", requestBody)

    fun streamProbabilityAssignmentExplanation(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/generate-probability-assignment-explanation", requestBody)

    fun streamGammaExposureExplanation(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/generate-gamma-exposure-explanation", requestBody)

    fun streamChat(requestBody: String): Flow<String> =
        streamFromEndpoint("$baseUrl/api/chat", requestBody)

    private fun streamFromEndpoint(url: String, body: String): Flow<String> = flow {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBuilder = Request.Builder()
            .url(url)
            .post(body.toRequestBody(mediaType))

        tokenManager.getToken()?.let { token ->
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: ${response.message}")
        }

        val reader: BufferedReader = response.body?.charStream()?.buffered()
            ?: throw Exception("Empty response body")

        reader.use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                val trimmed = line?.trim() ?: continue
                if (trimmed.isEmpty()) continue
                try {
                    val jsonElement = json.parseToJsonElement(trimmed)
                    val text = jsonElement.jsonObject["text"]?.jsonPrimitive?.content
                    if (text != null) {
                        emit(text)
                    }
                } catch (_: Exception) {
                    // skip unparseable lines
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
