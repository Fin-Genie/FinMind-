package com.example.data.model

enum class NewsCategory(val displayName: String) {
    ALL("All Wire"),
    MACRO_RATES("Macro & Rates"),
    TECH_AI("Tech & AI"),
    EQUITIES("Equities"),
    COMMODITIES("Commodities"),
    GEOPOLITICS("Geopolitics"),
    CRYPTO("Digital Assets")
}

enum class NewsUrgency {
    BREAKING,
    HIGH,
    NORMAL
}

enum class MarketSentiment {
    BULLISH,
    BEARISH,
    VOLATILE,
    NEUTRAL
}

enum class ImpactDirection {
    BULLISH,
    BEARISH,
    NEUTRAL,
    MIXED
}

data class FinancialNews(
    val id: String,
    val headline: String,
    val source: String,
    val category: NewsCategory,
    val timeAgo: String,
    val urgency: NewsUrgency,
    val summary: String,
    val sentiment: MarketSentiment,
    val affectedSectors: List<String>,
    val isCustom: Boolean = false
)

data class AssetImpact(
    val ticker: String,
    val assetName: String,
    val weightPct: Double,
    val impactDirection: ImpactDirection,
    val estimatedAlphaPct: Double, // e.g. +2.4 or -1.8
    val transmissionChannel: String, // e.g. "Discount Rate Multiple Compression", "GPU CapEx Acceleration"
    val rationale: String,
    val tacticalDefense: String // e.g. "Write 30-day OTM covered calls", "Maintain full allocation"
)

data class PortfolioImpactReport(
    val id: String = System.currentTimeMillis().toString(),
    val headlineTitle: String,
    val source: String,
    val analyzedAt: Long = System.currentTimeMillis(),
    val macroRegimeSummary: String,
    val netPortfolioImpactPct: Double,
    val volatilityDelta: String,
    val assetImpacts: List<AssetImpact>,
    val cladrbExecutiveAdvice: String,
    val isAiGenerated: Boolean = true
)
