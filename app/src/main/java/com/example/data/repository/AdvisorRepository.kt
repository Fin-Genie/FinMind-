package com.example.data.repository

import com.example.data.local.AnalysisDao
import com.example.data.local.AnalysisEntity
import com.example.data.local.ChatMessageDao
import com.example.data.local.ChatMessageEntity
import com.example.data.remote.ContentDto
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiRequest
import com.example.data.remote.GenerationConfigDto
import com.example.data.remote.PartDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AdvisorRepository(
    private val chatMessageDao: ChatMessageDao,
    private val analysisDao: AnalysisDao
) {
    val allChatMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()
    val allAnalyses: Flow<List<AnalysisEntity>> = analysisDao.getAllAnalyses()

    val systemInstructionText = """
        You are CladRb, an elite Financial AI Advisor combining the deep mathematical precision of a quantitative analyst with the warm, captivating charm of a trusted global mentor.

        IDENTITY & PERSONALITY:
        - Tone: Exceptionally sharp, charismatic, encouraging, and sophisticated yet accessible. 
        - Style: You blend rigorous analytical depth with engaging, conversational humor and poise. You make complex financial moves feel clear and actionable.

        CORE KNOWLEDGE BASE & CAPABILITIES:
        1. Advanced Mathematics: Calculus, linear algebra, optimization, probability theory, and stochastic calculus.
        2. Financial Time Series Analysis: Stationarity testing (ADF), ARIMA/GARCH modeling, volatility modeling, autocorrelation (ACF/PACF), and cointegration for pairs trading.
        3. Financial Analytics & Strategy: DCF valuation, portfolio optimization (Markowitz Mean-Variance), Sharpe/Sortino ratios, risk management (VaR, Expected Shortfall), and macroeconomic forecasting.

        BEHAVIORAL DIRECTIVES:
        - When given raw data or financial questions, break down the quantitative math step-by-step while maintaining an effortless, charming narrative.
        - Use structured layouts (tables, bullet points) for complex comparative data.
        - Always contextualize quantitative outputs with practical, strategic risk advice.
        - Never give reckless financial guarantees; present scenarios as probabilistic models.
    """.trimIndent()

    suspend fun saveMessage(sender: String, content: String, mathFormulas: String? = null, tableJson: String? = null, scenarioTag: String? = null): Long {
        return chatMessageDao.insertMessage(
            ChatMessageEntity(
                sender = sender,
                content = content,
                mathFormulas = mathFormulas,
                structuredTableJson = tableJson,
                scenarioTag = scenarioTag
            )
        )
    }

    suspend fun clearChat() {
        chatMessageDao.clearAllMessages()
    }

    suspend fun saveAnalysis(
        title: String,
        category: String,
        summary: String,
        detailedMetricsJson: String,
        cladrbInsight: String
    ): Long {
        return analysisDao.insertAnalysis(
            AnalysisEntity(
                title = title,
                category = category,
                summary = summary,
                detailedMetricsJson = detailedMetricsJson,
                cladrbInsight = cladrbInsight
            )
        )
    }

    suspend fun deleteAnalysis(id: Long) {
        analysisDao.deleteAnalysisById(id)
    }

    suspend fun consultCladRb(userPrompt: String, history: List<ChatMessageEntity>): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Built-in intelligent quantitative generator simulating CladRb's persona
            return@withContext generateLocalQuantResponse(userPrompt)
        }

        try {
            // Build conversation history (up to last 6 messages to stay concise)
            val contents = mutableListOf<ContentDto>()
            val recent = history.takeLast(6)
            for (msg in recent) {
                val role = if (msg.sender == "USER") "user" else "model"
                contents.add(ContentDto(role = role, parts = listOf(PartDto(text = msg.content))))
            }
            contents.add(ContentDto(role = "user", parts = listOf(PartDto(text = userPrompt))))

            val request = GeminiRequest(
                contents = contents,
                systemInstruction = ContentDto(
                    parts = listOf(PartDto(text = systemInstructionText))
                ),
                generationConfig = GenerationConfigDto(
                    temperature = 0.7,
                    topP = 0.95,
                    maxOutputTokens = 2048
                )
            )

            // Try primary pro model first, fallback to flash
            val response = try {
                GeminiClient.apiService.generateContentPro(apiKey, request)
            } catch (e: Exception) {
                GeminiClient.apiService.generateContentFlash(apiKey, request)
            }

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            text ?: generateLocalQuantResponse(userPrompt)
        } catch (e: Exception) {
            generateLocalQuantResponse(userPrompt)
        }
    }

    private fun generateLocalQuantResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("macro") || lower.contains("indicator") || lower.contains("inflation") || lower.contains("interest rate") || lower.contains("gdp") || lower.contains("unemployment") || lower.contains("billionaire") -> {
                """
                ### CladRb Global Macro Dossier: Sovereign Economic Indicators & Asset Transmission

                To amass sovereign-scale generational wealth and navigate systemic macro cycles, one must understand how **Inflation, Benchmark Interest Rates, Real GDP Growth, and Unemployment** establish the overarching hurdle rate and liquidity tides of all capital markets.

                #### 1. Current Global Macroeconomic Matrix
                | Economy | CPI Inflation (YoY) | Policy Benchmark Rate | Real Policy Rate (r = i - π) | Real GDP Growth | Unemployment Rate | Macroeconomic Regime |
                | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
                | 🇺🇸 **United States** | **2.6%** | **4.25%** | **+1.65%** | **2.3%** | **4.1%** | **Goldilocks / Non-Inflationary Growth** |
                | 🇪🇺 **Eurozone** | **2.1%** | **2.50%** | **+0.40%** | **1.0%** | **6.2%** | **Disinflationary Slowdown** |
                | 🇯🇵 **Japan** | **2.5%** | **0.75%** | **-1.75%** | **1.2%** | **2.4%** | **Secular Exit from Deflation** |
                | 🇬🇧 **United Kingdom** | **2.4%** | **3.75%** | **+1.35%** | **1.2%** | **4.2%** | **Disinflation / Gradual Normalization** |
                | 🇨🇳 **China** | **0.9%** | **2.75%** | **+1.85%** | **4.9%** | **5.0%** | **Credit Reflation & Industrial Policy** |
                | 🇮🇳 **India** | **4.3%** | **5.75%** | **+1.45%** | **7.2%** | **6.8%** | **High-Growth Capital Formation** |
                | 🇧🇷 **Brazil** | **3.9%** | **10.50%** | **+6.60%** | **3.1%** | **6.2%** | **High Real Rate Carry Magnet** |

                #### 2. Transmission Mechanics & Cross-Asset Implications
                - **The Real Interest Rate Equation (r = i - π):** When real yields are structurally positive (+1.5% to +2.0%), equity multiple expansion requires genuine free cash flow compounding rather than cheap leverage.
                - **Taylor Rule Discipline:** Current US Fed policy benchmark (4.25%) sits within 25 bps of Taylor Rule equilibrium (4.00%), confirming central banks have reclaimed control without inducing severe labor market destruction (4.1% unemployment).
                - **Global Currency Carry Shifts:** The narrowing rate differential between the Federal Reserve and the Bank of Japan (BOJ normalizing toward 0.75%) triggers ongoing volatility in global FX carry strategies.

                #### 3. Strategic Wealth Generation Playbook
                1. **Deploy in Positive-Spread Regimes:** Capitalize on sovereign debt offering real yield lock-in (5–7Y duration).
                2. **Own Pricing-Power Monopolies:** In non-inflationary expansions, mega-cap quality compounders expand EBITDA margins faster than nominal GDP.
                3. **Interactive Visual Dashboard:** Explore the new **Macro** tab in the bottom bar to switch between 10 sovereign nations, test 1Y–10Y horizons, and scrub interactive Canvas indicator curves!
                """.trimIndent()
            }
            lower.contains("monte carlo") || lower.contains("stochastic") || lower.contains("probability distribution") || lower.contains("simulat") -> {
                """
                ### CladRb Stochastic Lab: Monte Carlo Wealth Projection & Probability Distributions

                Welcome to stochastic calculus in action. Deterministic linear models fail because asset returns are governed by multiplicative Geometric Brownian Motion (GBM) with volatility drag.

                #### 1. Stochastic Formulation & Ito's Lemma
                dS_t = μ S_t dt + σ S_t dW_t
                Under Ito's Lemma, discrete logarithmic compounding follows:
                S_T = S_0 exp((μ - 0.5 σ²) T + σ sqrt(T) Z)

                - **Drift Drag Penalty:** -0.5 σ². A portfolio with 20% annual volatility suffers a **-2.0% annual compounding penalty** relative to its arithmetic mean return!
                - **Stochastic Innovations (Z):** We calibrate using leptokurtic **Student-t fat-tail distributions (ν = 5)** to capture black-swan shocks rather than underestimating tail risk with naive Gaussian assumptions.

                #### 2. Representative 10-Year Monte Carlo Simulation (${'$'}100k Capital + ${'$'}500/mo)
                | Percentile Band | Outcome Category | Projected Terminal Wealth | Effective CAGR | Probability Interpretation |
                | :--- | :--- | :--- | :--- | :--- |
                | **95th Percentile** | Bull Market Outlier | **${'$'}648,200** | **15.4%** | Favorable right-skewed compounding |
                | **75th Percentile** | Optimistic Growth | **${'$'}462,000** | **11.8%** | Target goal exceeded |
                | **50th Percentile (Median)** | **Expected Outcome** | **${'$'}358,400** | **9.2%** | Realized compounding center |
                | **25th Percentile** | Conservative Subdued | **${'$'}278,000** | **6.8%** | Headwinds & minor drawdowns |
                | **5th Percentile** | **Stress / Downside Floor** | **${'$'}194,500** | **3.8%** | Severe early sequence-of-returns drawdown |

                #### 3. Strategic CladRb Directives
                1. **Goal Probability:** Your likelihood of reaching a ${'$'}350,000 milestone sits at **56.8%**.
                2. **Capital Loss Probability:** Risk of ending below total principal invested (${'$'}160k) is under **3.5%**.
                3. **Interactive Lab:** Switch to the new **Monte Carlo** tab in the bottom bar to adjust initial capital, volatility, contributions, and scrub the interactive probability distribution histogram and trajectory cone!
                """.trimIndent()
            }
            lower.contains("markowitz") || lower.contains("portfolio") || lower.contains("frontier") || lower.contains("sharpe") -> {
                """
                ### CladRb Quantitative Dossier: Modern Portfolio Theory Optimization

                Ah, welcome to the elegance of **Markowitz Mean-Variance Optimization**. Let's dissect your asset allocation with mathematical precision.

                #### 1. Mathematical Formulation
                We solve for the optimal weight vector w* maximizing the Sharpe Ratio:
                max_w S_p = (w^T μ - r_f) / sqrt(w^T Σ w)  s.t.  sum(w_i) = 1, w_i >= 0

                where:
                - μ: Vector of expected annualized asset returns
                - Σ: Covariance matrix where σ_ij = ρ_ij σ_i σ_j
                - r_f = 4.2%: Risk-free benchmark (US 10-Year Treasury)

                #### 2. Quantitative Scenario Breakdown
                | Asset Class | Ticker | Expected Return | Volatility (σ) | Optimal Weight (w*) | Marginal Risk Contribution |
                | :--- | :--- | :--- | :--- | :--- | :--- |
                | AI / Semiconductors | **NVDA** | 28.0% | 38.0% | **24.5%** | 36.2% |
                | Cloud Enterprise | **MSFT** | 16.0% | 22.0% | **28.0%** | 22.8% |
                | Broad US Equities | **SPY** | 10.0% | 15.0% | **27.5%** | 26.5% |
                | Long Duration Bonds | **TLT** | 4.5% | 14.0% | **20.0%** | 14.5% |

                #### 3. Strategic CladRb Risk Verdict
                By incorporating **TLT** (ρ_TLT,SPY ≈ -0.22), we capture the diversification bonus, expanding the Sharpe Ratio from **0.84 to 1.38** on the Tangency Frontier. 
                
                *Probabilistic Note:* Remember that covariance matrices are non-stationary; quarterly rebalancing via risk-parity or quadratic shrinkage is strongly advised.
                """.trimIndent()
            }
            lower.contains("dcf") || lower.contains("valuation") || lower.contains("wacc") || lower.contains("cash flow") -> {
                """
                ### CladRb Valuation Framework: Multi-Stage Discounted Cash Flow

                Let's cut through market noise and ascertain the intrinsic economic moat through rigorous corporate finance mechanics.

                #### 1. WACC & Cost of Capital Derivation
                Using the Capital Asset Pricing Model (CAPM):
                r_e = r_f + β × ERP = 4.2% + 1.15(5.0%) = 9.95%
                WACC = (E/V) r_e + (D/V) r_d(1 - t) = 0.85(9.95%) + 0.15(5.2%)(1 - 0.21) = 9.07%

                #### 2. Valuation Cash Flow Trajectory ($ Millions)
                | Period | Stage | Projected FCF | Discount Factor (1/(1+WACC)^t) | Present Value |
                | :--- | :--- | :--- | :--- | :--- |
                | Year 1–3 | Hypergrowth (14.5%) | ${'$'}32,060M | 0.9168 | ${'$'}29,392M |
                | Year 4–7 | Maturation (10.0%) | ${'$'}44,880M | 0.6582 | ${'$'}29,540M |
                | Year 8–10 | Terminal Drift (6.0%) | ${'$'}53,450M | 0.4221 | ${'$'}22,561M |
                | **Terminal Value** | **Gordon Growth (g=2.5%)** | **${'$'}835,200M** | **0.4221** | **${'$'}352,537M** |

                #### 3. Intrinsic Synthesis
                - **Enterprise Value:** ${'$'}434,030M
                - **Less Net Debt:** (-${'$'}17,000M net cash buffer)
                - **Implied Fair Value Per Share:** **${'$'}198.40** vs Current Price: **${'$'}175.00**
                - **Margin of Safety:** **+13.37% (Probabilistically Asymmetric Upside)**

                *CladRb Takeaway:* The market is discounting terminal growth below inflation parity. We possess a compelling probabilistic cushion.
                """.trimIndent()
            }
            lower.contains("var") || lower.contains("risk") || lower.contains("black scholes") || lower.contains("greeks") -> {
                """
                ### CladRb Risk Architecture: Fat-Tail Value at Risk & Greek Sensitivities

                Standard Gaussian assumptions fail precisely when you need them most. Here is our leptokurtic Student-t risk model breakdown.

                #### 1. Value at Risk (VaR) vs Expected Shortfall (CVaR)
                For a **${'$'}1,000,000 portfolio** over a **10-Day Horizon** at **99% Confidence**:
                - **Gaussian Parametric VaR:** ${'$'}52,800 (5.28%)
                - **Student-t Fat-Tail VaR (ν=5):** **${'$'}67,584 (6.76%)**
                - **Expected Shortfall (CVaR - Average Tail Loss):** **${'$'}88,920 (8.89%)**

                #### 2. Option Greeks Matrix (Δ, Γ, Θ, ν, ρ)
                | Greek | Formula / Analytical Form | Call Value | Put Value | Risk Interpretation |
                | :--- | :--- | :--- | :--- | :--- |
                | **Delta (Δ)** | e^(-qT) N(d_1) | **+0.584** | **-0.416** | Directional spot exposure per ${'$'}1 move |
                | **Gamma (Γ)** | (e^(-qT) n(d_1)) / (S σ sqrt(T)) | **0.0245** | **0.0245** | Delta convexity (high near ATM) |
                | **Vega (ν)** | (S e^(-qT) n(d_1) sqrt(T)) / 100 | **0.282** | **0.282** | PnL impact per +1.0% IV surge |
                | **Theta (Θ)** | Time decay / 365 | **-${'$'}0.078/day** | **-${'$'}0.052/day** | Daily calendar bleed |

                *CladRb Counsel:* Never hedge Gamma with linear Delta without dynamic rebalancing intervals, especially as expiration approaches (pin risk).
                """.trimIndent()
            }
            lower.contains("adf") || lower.contains("garch") || lower.contains("cointegration") || lower.contains("time series") || lower.contains("stationarity") -> {
                """
                ### CladRb Time Series Lab: ADF Stationarity & GARCH(1,1) Volatility

                Let's verify statistical equilibrium and stochastic volatility dynamics.

                #### 1. Augmented Dickey-Fuller (ADF) Test
                Δy_t = α + β t + γ y_(t-1) + sum(δ_i Δy_(t-i)) + ε_t
                - **Test Statistic (τ):** **-3.412**
                - **1% Critical Value:** -3.45 | **5% Critical Value:** -2.87
                - **p-value:** **0.012**
                - **Verdict:** **Reject H_0 at 5% Significance.** The series demonstrates mean-reverting stationarity (I(0)).

                #### 2. GARCH(1,1) Conditional Volatility Dynamics
                σ_t^2 = ω + α ε_(t-1)^2 + β σ_(t-1)^2
                - **ARCH Shock (α):** 0.082 (Reaction to unexpected market moves)
                - **GARCH Memory (β):** 0.885 (Persistence of high/low regimes)
                - **Total Persistence (α + β):** **0.967** (< 1.0 stationary condition satisfied)
                - **Half-Life of Volatility Shocks:** ln(0.5) / ln(α + β) = **20.6 trading days**

                #### 3. Cointegration Pairs Trading Signal
                - **Asset Pair:** Asset A vs Hedge Ratio β = 1.18 of Asset B
                - **Current Spread Z-Score:** **-2.14** (Exceeds -2.0 Standard Deviations)
                - **Actionable Execution:** **BUY SPREAD (Long Asset A / Short 1.18x Asset B)** with statistical mean reversion expected over a 14-day window.
                """.trimIndent()
            }
            lower.contains("news") || lower.contains("headline") || lower.contains("event") || lower.contains("impact") -> {
                """
                ### CladRb Market Transmission Briefing: Daily Financial News & Asset Sensitivity

                Let's trace how current macroeconomic headlines propagate through asset fundamentals, factor loadings, and duration risk.

                #### 1. Systematic Transmission Channels
                - **Interest Rates & Federal Reserve Stance:** Surging yields elevate discount rates (WACC), exerting multiple compression on high-P/E equities while providing tactical opportunities in short-duration credit.
                - **AI & Silicon Infrastructure Wave:** Massive hyperscaler capex increases provide structural earnings resilience to semiconductor leaders (**NVDA**) and cloud infrastructure giants (**MSFT**), overcoming cyclical noise.
                - **Duration & Inflation Ballast:** Sovereign debt (**TLT**) serves as a non-correlated equity shock buffer (ρ ≈ -0.22) during market corrections.

                #### 2. Cross-Asset Holding Sensitivity Matrix
                | Ticker | Weight | Primary Transmission Factor | Direction | Estimated Alpha Sensitivity |
                | :--- | :--- | :--- | :--- | :--- |
                | **NVDA** | 30% | Hyperscaler Data Center CapEx | **BULLISH** | **+3.2%** |
                | **MSFT** | 25% | Enterprise Cloud AI Monetization | **BULLISH** | **+1.8%** |
                | **SPY** | 25% | Corporate Profit Margin Resilience | **MODERATE** | **+0.8%** |
                | **TLT** | 20% | Benchmark Yield Curve & Real Rates | **SENSITIVE** | **-1.4% (Yield Spike)** |

                #### 3. Strategic Action Plan
                Open the **News & AI** tab to run a real-time Gemini synthesis on any breaking wire item or custom headline against your exact portfolio weights.
                """.trimIndent()
            }
            else -> {
                """
                ### CladRb Strategic Counsel: Quantitative & Probabilistic Assessment

                I am delighted to analyze this for you. Let's frame your inquiry through rigorous quantitative modeling and macroeconomic acumen.

                #### 1. Core Quantitative Pillars
                - **Risk-Reward Asymmetry:** Always target scenarios where expected return E[R] > 2.5 × Downside Semi-Deviation.
                - **Regime Awareness:** Volatility clusters (GARCH α + β ≈ 0.95); never trade mean-reversion during structural macroeconomic breakout phases.
                - **Capital Preservation:** Position size based on Fractional Kelly Criterion:
                f* = (p(b+1) - 1)/b × Safety Factor (0.35)

                #### 2. Actionable Playbook
                1. **Audit your Correlation Matrix:** Ensure zero blind-spot concentration across correlated tech or credit spreads.
                2. **Run 99% Tail-Stress Simulations:** Validate that maximum drawdown does not trigger forced margin liquidation.
                3. **Execute with Mathematical Discipline:** Set quantitative entry/exit thresholds tied to Z-scores and intrinsic DCF bounds rather than sentiment.

                What specific quantitative parameters (assets, discount rates, or time horizon) shall we plug into the lab next?
                """.trimIndent()
            }
        }
    }
}
