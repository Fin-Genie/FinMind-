package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<ContentDto>,
    @Json(name = "systemInstruction") val systemInstruction: ContentDto? = null,
    @Json(name = "generationConfig") val generationConfig: GenerationConfigDto? = null
)

@JsonClass(generateAdapter = true)
data class ContentDto(
    @Json(name = "role") val role: String? = null,
    @Json(name = "parts") val parts: List<PartDto>
)

@JsonClass(generateAdapter = true)
data class PartDto(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfigDto(
    @Json(name = "temperature") val temperature: Double? = 0.7,
    @Json(name = "topP") val topP: Double? = 0.95,
    @Json(name = "topK") val topK: Int? = 40,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = 2048
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<CandidateDto>? = null,
    @Json(name = "promptFeedback") val promptFeedback: Any? = null
)

@JsonClass(generateAdapter = true)
data class CandidateDto(
    @Json(name = "content") val content: ContentDto? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)
