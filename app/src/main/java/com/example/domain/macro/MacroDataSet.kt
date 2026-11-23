package com.example.domain.macro

object MacroDataSet {

    val allCountries: List<CountryMacroProfile> by lazy {
        listOf(
            createUnitedStates(),
            createEurozone(),
            createJapan(),
            createUnitedKingdom(),
            createChina(),
            createGermany(),
            createIndia(),
            createCanada(),
            createAustralia(),
            createBrazil()
        )
    }

    fun getCountryById(id: String): CountryMacroProfile {
        return allCountries.find { it.id.equals(id, ignoreCase = true) } ?: allCountries.first()
    }

    // 🇺🇸 UNITED STATES
    private fun createUnitedStates(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()

        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 3.1), MacroDataPoint("Dec '25", 3.0),
                MacroDataPoint("Jan '26", 2.9), MacroDataPoint("Feb '26", 2.9),
                MacroDataPoint("Mar '26", 2.8), MacroDataPoint("Apr '26", 2.7),
                MacroDataPoint("May '26", 2.7), MacroDataPoint("Jun '26", 2.8),
                MacroDataPoint("Jul '26", 2.7), MacroDataPoint("Aug '26", 2.6),
                MacroDataPoint("Sep '26", 2.5), MacroDataPoint("Oct '26", 2.6)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 5.25), MacroDataPoint("Dec '25", 5.00),
                MacroDataPoint("Jan '26", 5.00), MacroDataPoint("Feb '26", 4.75),
                MacroDataPoint("Mar '26", 4.75), MacroDataPoint("Apr '26", 4.75),
                MacroDataPoint("May '26", 4.50), MacroDataPoint("Jun '26", 4.50),
                MacroDataPoint("Jul '26", 4.50), MacroDataPoint("Aug '26", 4.25),
                MacroDataPoint("Sep '26", 4.25), MacroDataPoint("Oct '26", 4.25)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 2.6), MacroDataPoint("Q1 '26", 2.3),
                MacroDataPoint("Q2 '26", 2.4), MacroDataPoint("Q3 '26", 2.2),
                MacroDataPoint("Q4 '26", 2.3)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 4.0), MacroDataPoint("Dec '25", 4.0),
                MacroDataPoint("Jan '26", 4.1), MacroDataPoint("Feb '26", 4.1),
                MacroDataPoint("Mar '26", 4.2), MacroDataPoint("Apr '26", 4.1),
                MacroDataPoint("May '26", 4.2), MacroDataPoint("Jun '26", 4.3),
                MacroDataPoint("Jul '26", 4.2), MacroDataPoint("Aug '26", 4.2),
                MacroDataPoint("Sep '26", 4.1), MacroDataPoint("Oct '26", 4.1)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 3.4), MacroDataPoint("Q1 '24", 3.2),
                MacroDataPoint("Q2 '24", 3.0), MacroDataPoint("Q3 '24", 2.8),
                MacroDataPoint("Q4 '24", 2.7), MacroDataPoint("Q1 '25", 2.8),
                MacroDataPoint("Q2 '25", 2.9), MacroDataPoint("Q3 '25", 3.0),
                MacroDataPoint("Q4 '25", 3.0), MacroDataPoint("Q1 '26", 2.8),
                MacroDataPoint("Q2 '26", 2.7), MacroDataPoint("Q3 '26", 2.6)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 5.50), MacroDataPoint("Q1 '24", 5.50),
                MacroDataPoint("Q2 '24", 5.50), MacroDataPoint("Q3 '24", 5.00),
                MacroDataPoint("Q4 '24", 4.75), MacroDataPoint("Q1 '25", 4.75),
                MacroDataPoint("Q2 '25", 5.00), MacroDataPoint("Q3 '25", 5.25),
                MacroDataPoint("Q4 '25", 5.00), MacroDataPoint("Q1 '26", 4.75),
                MacroDataPoint("Q2 '26", 4.50), MacroDataPoint("Q3 '26", 4.25)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 2.9), MacroDataPoint("Q1 '24", 1.6),
                MacroDataPoint("Q2 '24", 3.0), MacroDataPoint("Q3 '24", 2.8),
                MacroDataPoint("Q4 '24", 2.5), MacroDataPoint("Q1 '25", 2.1),
                MacroDataPoint("Q2 '25", 2.4), MacroDataPoint("Q3 '25", 2.7),
                MacroDataPoint("Q4 '25", 2.6), MacroDataPoint("Q1 '26", 2.3),
                MacroDataPoint("Q2 '26", 2.4), MacroDataPoint("Q3 '26", 2.3)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 3.7), MacroDataPoint("Q1 '24", 3.8),
                MacroDataPoint("Q2 '24", 4.1), MacroDataPoint("Q3 '24", 4.1),
                MacroDataPoint("Q4 '24", 4.2), MacroDataPoint("Q1 '25", 4.0),
                MacroDataPoint("Q2 '25", 4.0), MacroDataPoint("Q3 '25", 4.1),
                MacroDataPoint("Q4 '25", 4.0), MacroDataPoint("Q1 '26", 4.2),
                MacroDataPoint("Q2 '26", 4.2), MacroDataPoint("Q3 '26", 4.1)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 4.7), MacroDataPoint("2022", 8.0),
                MacroDataPoint("2023", 4.1), MacroDataPoint("2024", 2.9),
                MacroDataPoint("2025", 2.9), MacroDataPoint("2026", 2.6)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 0.25), MacroDataPoint("2022", 4.50),
                MacroDataPoint("2023", 5.50), MacroDataPoint("2024", 4.75),
                MacroDataPoint("2025", 5.00), MacroDataPoint("2026", 4.25)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 5.9), MacroDataPoint("2022", 1.9),
                MacroDataPoint("2023", 2.5), MacroDataPoint("2024", 2.8),
                MacroDataPoint("2025", 2.5), MacroDataPoint("2026", 2.3)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 5.4), MacroDataPoint("2022", 3.6),
                MacroDataPoint("2023", 3.6), MacroDataPoint("2024", 4.1),
                MacroDataPoint("2025", 4.0), MacroDataPoint("2026", 4.1)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 1.3), MacroDataPoint("2017", 2.1), MacroDataPoint("2018", 2.4),
                MacroDataPoint("2019", 1.8), MacroDataPoint("2020", 1.2), MacroDataPoint("2021", 4.7),
                MacroDataPoint("2022", 8.0), MacroDataPoint("2023", 4.1), MacroDataPoint("2024", 2.9),
                MacroDataPoint("2025", 2.9), MacroDataPoint("2026", 2.6)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 0.50), MacroDataPoint("2017", 1.25), MacroDataPoint("2018", 2.25),
                MacroDataPoint("2019", 1.75), MacroDataPoint("2020", 0.25), MacroDataPoint("2021", 0.25),
                MacroDataPoint("2022", 4.50), MacroDataPoint("2023", 5.50), MacroDataPoint("2024", 4.75),
                MacroDataPoint("2025", 5.00), MacroDataPoint("2026", 4.25)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 1.7), MacroDataPoint("2017", 2.3), MacroDataPoint("2018", 2.9),
                MacroDataPoint("2019", 2.3), MacroDataPoint("2020", -2.8), MacroDataPoint("2021", 5.9),
                MacroDataPoint("2022", 1.9), MacroDataPoint("2023", 2.5), MacroDataPoint("2024", 2.8),
                MacroDataPoint("2025", 2.5), MacroDataPoint("2026", 2.3)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 4.9), MacroDataPoint("2017", 4.4), MacroDataPoint("2018", 3.9),
                MacroDataPoint("2019", 3.7), MacroDataPoint("2020", 8.1), MacroDataPoint("2021", 5.4),
                MacroDataPoint("2022", 3.6), MacroDataPoint("2023", 3.6), MacroDataPoint("2024", 4.1),
                MacroDataPoint("2025", 4.0), MacroDataPoint("2026", 4.1)
            )
        )

        return CountryMacroProfile(
            id = "US",
            name = "United States",
            flag = "🇺🇸",
            centralBank = "Federal Reserve",
            currency = "USD",
            targetInflation = 2.0,
            neutralRateEstimate = 2.75,
            debtToGdp = 122.3,
            tenYearYield = 4.18,
            twoYearYield = 3.92,
            seriesByHorizon = series
        )
    }

    // 🇪🇺 EUROZONE
    private fun createEurozone(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 2.4), MacroDataPoint("Dec '25", 2.4),
                MacroDataPoint("Jan '26", 2.3), MacroDataPoint("Feb '26", 2.2),
                MacroDataPoint("Mar '26", 2.2), MacroDataPoint("Apr '26", 2.1),
                MacroDataPoint("May '26", 2.0), MacroDataPoint("Jun '26", 2.1),
                MacroDataPoint("Jul '26", 2.2), MacroDataPoint("Aug '26", 2.1),
                MacroDataPoint("Sep '26", 2.0), MacroDataPoint("Oct '26", 2.1)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 3.50), MacroDataPoint("Dec '25", 3.25),
                MacroDataPoint("Jan '26", 3.25), MacroDataPoint("Feb '26", 3.00),
                MacroDataPoint("Mar '26", 3.00), MacroDataPoint("Apr '26", 2.75),
                MacroDataPoint("May '26", 2.75), MacroDataPoint("Jun '26", 2.75),
                MacroDataPoint("Jul '26", 2.50), MacroDataPoint("Aug '26", 2.50),
                MacroDataPoint("Sep '26", 2.50), MacroDataPoint("Oct '26", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 0.7), MacroDataPoint("Q1 '26", 0.8),
                MacroDataPoint("Q2 '26", 0.9), MacroDataPoint("Q3 '26", 1.0),
                MacroDataPoint("Q4 '26", 1.1)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 6.4), MacroDataPoint("Dec '25", 6.4),
                MacroDataPoint("Jan '26", 6.3), MacroDataPoint("Feb '26", 6.3),
                MacroDataPoint("Mar '26", 6.3), MacroDataPoint("Apr '26", 6.2),
                MacroDataPoint("May '26", 6.2), MacroDataPoint("Jun '26", 6.3),
                MacroDataPoint("Jul '26", 6.3), MacroDataPoint("Aug '26", 6.2),
                MacroDataPoint("Sep '26", 6.2), MacroDataPoint("Oct '26", 6.2)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 2.9), MacroDataPoint("Q1 '24", 2.6),
                MacroDataPoint("Q2 '24", 2.5), MacroDataPoint("Q3 '24", 2.2),
                MacroDataPoint("Q4 '24", 2.3), MacroDataPoint("Q1 '25", 2.4),
                MacroDataPoint("Q2 '25", 2.3), MacroDataPoint("Q3 '25", 2.4),
                MacroDataPoint("Q4 '25", 2.4), MacroDataPoint("Q1 '26", 2.2),
                MacroDataPoint("Q2 '26", 2.1), MacroDataPoint("Q3 '26", 2.1)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 4.00), MacroDataPoint("Q1 '24", 4.00),
                MacroDataPoint("Q2 '24", 3.75), MacroDataPoint("Q3 '24", 3.50),
                MacroDataPoint("Q4 '24", 3.25), MacroDataPoint("Q1 '25", 3.50),
                MacroDataPoint("Q2 '25", 3.50), MacroDataPoint("Q3 '25", 3.50),
                MacroDataPoint("Q4 '25", 3.25), MacroDataPoint("Q1 '26", 3.00),
                MacroDataPoint("Q2 '26", 2.75), MacroDataPoint("Q3 '26", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 0.1), MacroDataPoint("Q1 '24", 0.3),
                MacroDataPoint("Q2 '24", 0.3), MacroDataPoint("Q3 '24", 0.4),
                MacroDataPoint("Q4 '24", 0.5), MacroDataPoint("Q1 '25", 0.6),
                MacroDataPoint("Q2 '25", 0.7), MacroDataPoint("Q3 '25", 0.8),
                MacroDataPoint("Q4 '25", 0.7), MacroDataPoint("Q1 '26", 0.8),
                MacroDataPoint("Q2 '26", 0.9), MacroDataPoint("Q3 '26", 1.0)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 6.5), MacroDataPoint("Q1 '24", 6.5),
                MacroDataPoint("Q2 '24", 6.5), MacroDataPoint("Q3 '24", 6.3),
                MacroDataPoint("Q4 '24", 6.3), MacroDataPoint("Q1 '25", 6.4),
                MacroDataPoint("Q2 '25", 6.4), MacroDataPoint("Q3 '25", 6.4),
                MacroDataPoint("Q4 '25", 6.4), MacroDataPoint("Q1 '26", 6.3),
                MacroDataPoint("Q2 '26", 6.2), MacroDataPoint("Q3 '26", 6.2)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 2.6), MacroDataPoint("2022", 8.4),
                MacroDataPoint("2023", 5.4), MacroDataPoint("2024", 2.4),
                MacroDataPoint("2025", 2.4), MacroDataPoint("2026", 2.1)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", -0.50), MacroDataPoint("2022", 2.00),
                MacroDataPoint("2023", 4.00), MacroDataPoint("2024", 3.25),
                MacroDataPoint("2025", 3.50), MacroDataPoint("2026", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 5.9), MacroDataPoint("2022", 3.5),
                MacroDataPoint("2023", 0.5), MacroDataPoint("2024", 0.8),
                MacroDataPoint("2025", 0.8), MacroDataPoint("2026", 1.0)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 7.7), MacroDataPoint("2022", 6.7),
                MacroDataPoint("2023", 6.5), MacroDataPoint("2024", 6.4),
                MacroDataPoint("2025", 6.4), MacroDataPoint("2026", 6.2)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 0.2), MacroDataPoint("2017", 1.5), MacroDataPoint("2018", 1.8),
                MacroDataPoint("2019", 1.2), MacroDataPoint("2020", 0.3), MacroDataPoint("2021", 2.6),
                MacroDataPoint("2022", 8.4), MacroDataPoint("2023", 5.4), MacroDataPoint("2024", 2.4),
                MacroDataPoint("2025", 2.4), MacroDataPoint("2026", 2.1)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", -0.40), MacroDataPoint("2017", -0.40), MacroDataPoint("2018", -0.40),
                MacroDataPoint("2019", -0.50), MacroDataPoint("2020", -0.50), MacroDataPoint("2021", -0.50),
                MacroDataPoint("2022", 2.00), MacroDataPoint("2023", 4.00), MacroDataPoint("2024", 3.25),
                MacroDataPoint("2025", 3.50), MacroDataPoint("2026", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 2.0), MacroDataPoint("2017", 2.6), MacroDataPoint("2018", 1.8),
                MacroDataPoint("2019", 1.6), MacroDataPoint("2020", -6.1), MacroDataPoint("2021", 5.9),
                MacroDataPoint("2022", 3.5), MacroDataPoint("2023", 0.5), MacroDataPoint("2024", 0.8),
                MacroDataPoint("2025", 0.8), MacroDataPoint("2026", 1.0)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 10.0), MacroDataPoint("2017", 9.1), MacroDataPoint("2018", 8.2),
                MacroDataPoint("2019", 7.5), MacroDataPoint("2020", 7.9), MacroDataPoint("2021", 7.7),
                MacroDataPoint("2022", 6.7), MacroDataPoint("2023", 6.5), MacroDataPoint("2024", 6.4),
                MacroDataPoint("2025", 6.4), MacroDataPoint("2026", 6.2)
            )
        )

        return CountryMacroProfile(
            id = "EU",
            name = "Eurozone",
            flag = "🇪🇺",
            centralBank = "European Central Bank",
            currency = "EUR",
            targetInflation = 2.0,
            neutralRateEstimate = 2.00,
            debtToGdp = 88.6,
            tenYearYield = 2.45,
            twoYearYield = 2.22,
            seriesByHorizon = series
        )
    }

    // 🇯🇵 JAPAN
    private fun createJapan(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 2.6), MacroDataPoint("Dec '25", 2.7),
                MacroDataPoint("Jan '26", 2.8), MacroDataPoint("Feb '26", 2.8),
                MacroDataPoint("Mar '26", 2.7), MacroDataPoint("Apr '26", 2.6),
                MacroDataPoint("May '26", 2.5), MacroDataPoint("Jun '26", 2.6),
                MacroDataPoint("Jul '26", 2.7), MacroDataPoint("Aug '26", 2.6),
                MacroDataPoint("Sep '26", 2.5), MacroDataPoint("Oct '26", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 0.25), MacroDataPoint("Dec '25", 0.25),
                MacroDataPoint("Jan '26", 0.50), MacroDataPoint("Feb '26", 0.50),
                MacroDataPoint("Mar '26", 0.50), MacroDataPoint("Apr '26", 0.50),
                MacroDataPoint("May '26", 0.50), MacroDataPoint("Jun '26", 0.75),
                MacroDataPoint("Jul '26", 0.75), MacroDataPoint("Aug '26", 0.75),
                MacroDataPoint("Sep '26", 0.75), MacroDataPoint("Oct '26", 0.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 1.0), MacroDataPoint("Q1 '26", 1.2),
                MacroDataPoint("Q2 '26", 1.1), MacroDataPoint("Q3 '26", 1.3),
                MacroDataPoint("Q4 '26", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 2.5), MacroDataPoint("Dec '25", 2.5),
                MacroDataPoint("Jan '26", 2.5), MacroDataPoint("Feb '26", 2.4),
                MacroDataPoint("Mar '26", 2.4), MacroDataPoint("Apr '26", 2.5),
                MacroDataPoint("May '26", 2.5), MacroDataPoint("Jun '26", 2.4),
                MacroDataPoint("Jul '26", 2.4), MacroDataPoint("Aug '26", 2.4),
                MacroDataPoint("Sep '26", 2.4), MacroDataPoint("Oct '26", 2.4)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 2.8), MacroDataPoint("Q1 '24", 2.7),
                MacroDataPoint("Q2 '24", 2.8), MacroDataPoint("Q3 '24", 2.5),
                MacroDataPoint("Q4 '24", 2.6), MacroDataPoint("Q1 '25", 2.7),
                MacroDataPoint("Q2 '25", 2.8), MacroDataPoint("Q3 '25", 2.7),
                MacroDataPoint("Q4 '25", 2.7), MacroDataPoint("Q1 '26", 2.8),
                MacroDataPoint("Q2 '26", 2.6), MacroDataPoint("Q3 '26", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", -0.10), MacroDataPoint("Q1 '24", 0.10),
                MacroDataPoint("Q2 '24", 0.10), MacroDataPoint("Q3 '24", 0.25),
                MacroDataPoint("Q4 '24", 0.25), MacroDataPoint("Q1 '25", 0.25),
                MacroDataPoint("Q2 '25", 0.25), MacroDataPoint("Q3 '25", 0.25),
                MacroDataPoint("Q4 '25", 0.25), MacroDataPoint("Q1 '26", 0.50),
                MacroDataPoint("Q2 '26", 0.50), MacroDataPoint("Q3 '26", 0.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 1.2), MacroDataPoint("Q1 '24", -0.9),
                MacroDataPoint("Q2 '24", 2.2), MacroDataPoint("Q3 '24", 0.9),
                MacroDataPoint("Q4 '24", 1.1), MacroDataPoint("Q1 '25", 1.0),
                MacroDataPoint("Q2 '25", 1.1), MacroDataPoint("Q3 '25", 1.2),
                MacroDataPoint("Q4 '25", 1.0), MacroDataPoint("Q1 '26", 1.2),
                MacroDataPoint("Q2 '26", 1.1), MacroDataPoint("Q3 '26", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 2.5), MacroDataPoint("Q1 '24", 2.6),
                MacroDataPoint("Q2 '24", 2.5), MacroDataPoint("Q3 '24", 2.4),
                MacroDataPoint("Q4 '24", 2.5), MacroDataPoint("Q1 '25", 2.5),
                MacroDataPoint("Q2 '25", 2.5), MacroDataPoint("Q3 '25", 2.5),
                MacroDataPoint("Q4 '25", 2.5), MacroDataPoint("Q1 '26", 2.4),
                MacroDataPoint("Q2 '26", 2.5), MacroDataPoint("Q3 '26", 2.4)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", -0.2), MacroDataPoint("2022", 2.5),
                MacroDataPoint("2023", 3.2), MacroDataPoint("2024", 2.7),
                MacroDataPoint("2025", 2.7), MacroDataPoint("2026", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", -0.10), MacroDataPoint("2022", -0.10),
                MacroDataPoint("2023", -0.10), MacroDataPoint("2024", 0.25),
                MacroDataPoint("2025", 0.25), MacroDataPoint("2026", 0.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 2.6), MacroDataPoint("2022", 1.0),
                MacroDataPoint("2023", 1.9), MacroDataPoint("2024", 0.5),
                MacroDataPoint("2025", 1.1), MacroDataPoint("2026", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 2.8), MacroDataPoint("2022", 2.6),
                MacroDataPoint("2023", 2.6), MacroDataPoint("2024", 2.5),
                MacroDataPoint("2025", 2.5), MacroDataPoint("2026", 2.4)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", -0.1), MacroDataPoint("2017", 0.5), MacroDataPoint("2018", 1.0),
                MacroDataPoint("2019", 0.5), MacroDataPoint("2020", 0.0), MacroDataPoint("2021", -0.2),
                MacroDataPoint("2022", 2.5), MacroDataPoint("2023", 3.2), MacroDataPoint("2024", 2.7),
                MacroDataPoint("2025", 2.7), MacroDataPoint("2026", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", -0.10), MacroDataPoint("2017", -0.10), MacroDataPoint("2018", -0.10),
                MacroDataPoint("2019", -0.10), MacroDataPoint("2020", -0.10), MacroDataPoint("2021", -0.10),
                MacroDataPoint("2022", -0.10), MacroDataPoint("2023", -0.10), MacroDataPoint("2024", 0.25),
                MacroDataPoint("2025", 0.25), MacroDataPoint("2026", 0.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 0.8), MacroDataPoint("2017", 1.7), MacroDataPoint("2018", 0.6),
                MacroDataPoint("2019", -0.4), MacroDataPoint("2020", -4.2), MacroDataPoint("2021", 2.6),
                MacroDataPoint("2022", 1.0), MacroDataPoint("2023", 1.9), MacroDataPoint("2024", 0.5),
                MacroDataPoint("2025", 1.1), MacroDataPoint("2026", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 3.1), MacroDataPoint("2017", 2.8), MacroDataPoint("2018", 2.4),
                MacroDataPoint("2019", 2.4), MacroDataPoint("2020", 2.8), MacroDataPoint("2021", 2.8),
                MacroDataPoint("2022", 2.6), MacroDataPoint("2023", 2.6), MacroDataPoint("2024", 2.5),
                MacroDataPoint("2025", 2.5), MacroDataPoint("2026", 2.4)
            )
        )

        return CountryMacroProfile(
            id = "JP",
            name = "Japan",
            flag = "🇯🇵",
            centralBank = "Bank of Japan",
            currency = "JPY",
            targetInflation = 2.0,
            neutralRateEstimate = 1.00,
            debtToGdp = 255.4,
            tenYearYield = 1.12,
            twoYearYield = 0.68,
            seriesByHorizon = series
        )
    }

    // 🇬🇧 UNITED KINGDOM
    private fun createUnitedKingdom(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 2.8), MacroDataPoint("Dec '25", 2.7),
                MacroDataPoint("Jan '26", 2.6), MacroDataPoint("Feb '26", 2.6),
                MacroDataPoint("Mar '26", 2.5), MacroDataPoint("Apr '26", 2.4),
                MacroDataPoint("May '26", 2.4), MacroDataPoint("Jun '26", 2.5),
                MacroDataPoint("Jul '26", 2.5), MacroDataPoint("Aug '26", 2.4),
                MacroDataPoint("Sep '26", 2.3), MacroDataPoint("Oct '26", 2.4)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 4.75), MacroDataPoint("Dec '25", 4.75),
                MacroDataPoint("Jan '26", 4.50), MacroDataPoint("Feb '26", 4.50),
                MacroDataPoint("Mar '26", 4.25), MacroDataPoint("Apr '26", 4.25),
                MacroDataPoint("May '26", 4.00), MacroDataPoint("Jun '26", 4.00),
                MacroDataPoint("Jul '26", 4.00), MacroDataPoint("Aug '26", 3.75),
                MacroDataPoint("Sep '26", 3.75), MacroDataPoint("Oct '26", 3.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 1.0), MacroDataPoint("Q1 '26", 1.1),
                MacroDataPoint("Q2 '26", 1.2), MacroDataPoint("Q3 '26", 1.3),
                MacroDataPoint("Q4 '26", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 4.3), MacroDataPoint("Dec '25", 4.3),
                MacroDataPoint("Jan '26", 4.4), MacroDataPoint("Feb '26", 4.4),
                MacroDataPoint("Mar '26", 4.3), MacroDataPoint("Apr '26", 4.3),
                MacroDataPoint("May '26", 4.2), MacroDataPoint("Jun '26", 4.3),
                MacroDataPoint("Jul '26", 4.3), MacroDataPoint("Aug '26", 4.2),
                MacroDataPoint("Sep '26", 4.2), MacroDataPoint("Oct '26", 4.2)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 4.2), MacroDataPoint("Q1 '24", 3.4),
                MacroDataPoint("Q2 '24", 2.0), MacroDataPoint("Q3 '24", 2.2),
                MacroDataPoint("Q4 '24", 2.5), MacroDataPoint("Q1 '25", 2.7),
                MacroDataPoint("Q2 '25", 2.8), MacroDataPoint("Q3 '25", 2.8),
                MacroDataPoint("Q4 '25", 2.7), MacroDataPoint("Q1 '26", 2.6),
                MacroDataPoint("Q2 '26", 2.4), MacroDataPoint("Q3 '26", 2.4)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 5.25), MacroDataPoint("Q1 '24", 5.25),
                MacroDataPoint("Q2 '24", 5.25), MacroDataPoint("Q3 '24", 5.00),
                MacroDataPoint("Q4 '24", 4.75), MacroDataPoint("Q1 '25", 4.75),
                MacroDataPoint("Q2 '25", 4.75), MacroDataPoint("Q3 '25", 4.75),
                MacroDataPoint("Q4 '25", 4.75), MacroDataPoint("Q1 '26", 4.50),
                MacroDataPoint("Q2 '26", 4.00), MacroDataPoint("Q3 '26", 3.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", -0.1), MacroDataPoint("Q1 '24", 0.7),
                MacroDataPoint("Q2 '24", 0.5), MacroDataPoint("Q3 '24", 0.1),
                MacroDataPoint("Q4 '24", 0.8), MacroDataPoint("Q1 '25", 0.9),
                MacroDataPoint("Q2 '25", 1.0), MacroDataPoint("Q3 '25", 1.1),
                MacroDataPoint("Q4 '25", 1.0), MacroDataPoint("Q1 '26", 1.1),
                MacroDataPoint("Q2 '26", 1.2), MacroDataPoint("Q3 '26", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 3.8), MacroDataPoint("Q1 '24", 4.3),
                MacroDataPoint("Q2 '24", 4.2), MacroDataPoint("Q3 '24", 4.3),
                MacroDataPoint("Q4 '24", 4.4), MacroDataPoint("Q1 '25", 4.4),
                MacroDataPoint("Q2 '25", 4.3), MacroDataPoint("Q3 '25", 4.3),
                MacroDataPoint("Q4 '25", 4.3), MacroDataPoint("Q1 '26", 4.4),
                MacroDataPoint("Q2 '26", 4.2), MacroDataPoint("Q3 '26", 4.2)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 2.6), MacroDataPoint("2022", 9.1),
                MacroDataPoint("2023", 7.3), MacroDataPoint("2024", 2.5),
                MacroDataPoint("2025", 2.7), MacroDataPoint("2026", 2.4)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 0.25), MacroDataPoint("2022", 3.50),
                MacroDataPoint("2023", 5.25), MacroDataPoint("2024", 4.75),
                MacroDataPoint("2025", 4.75), MacroDataPoint("2026", 3.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 8.7), MacroDataPoint("2022", 4.3),
                MacroDataPoint("2023", 0.3), MacroDataPoint("2024", 1.1),
                MacroDataPoint("2025", 1.0), MacroDataPoint("2026", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 4.5), MacroDataPoint("2022", 3.7),
                MacroDataPoint("2023", 4.0), MacroDataPoint("2024", 4.3),
                MacroDataPoint("2025", 4.3), MacroDataPoint("2026", 4.2)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 0.7), MacroDataPoint("2017", 2.7), MacroDataPoint("2018", 2.5),
                MacroDataPoint("2019", 1.8), MacroDataPoint("2020", 0.9), MacroDataPoint("2021", 2.6),
                MacroDataPoint("2022", 9.1), MacroDataPoint("2023", 7.3), MacroDataPoint("2024", 2.5),
                MacroDataPoint("2025", 2.7), MacroDataPoint("2026", 2.4)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 0.25), MacroDataPoint("2017", 0.50), MacroDataPoint("2018", 0.75),
                MacroDataPoint("2019", 0.75), MacroDataPoint("2020", 0.10), MacroDataPoint("2021", 0.25),
                MacroDataPoint("2022", 3.50), MacroDataPoint("2023", 5.25), MacroDataPoint("2024", 4.75),
                MacroDataPoint("2025", 4.75), MacroDataPoint("2026", 3.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 2.2), MacroDataPoint("2017", 2.1), MacroDataPoint("2018", 1.7),
                MacroDataPoint("2019", 1.6), MacroDataPoint("2020", -10.4), MacroDataPoint("2021", 8.7),
                MacroDataPoint("2022", 4.3), MacroDataPoint("2023", 0.3), MacroDataPoint("2024", 1.1),
                MacroDataPoint("2025", 1.0), MacroDataPoint("2026", 1.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 4.9), MacroDataPoint("2017", 4.4), MacroDataPoint("2018", 4.1),
                MacroDataPoint("2019", 3.8), MacroDataPoint("2020", 4.6), MacroDataPoint("2021", 4.5),
                MacroDataPoint("2022", 3.7), MacroDataPoint("2023", 4.0), MacroDataPoint("2024", 4.3),
                MacroDataPoint("2025", 4.3), MacroDataPoint("2026", 4.2)
            )
        )

        return CountryMacroProfile(
            id = "UK",
            name = "United Kingdom",
            flag = "🇬🇧",
            centralBank = "Bank of England",
            currency = "GBP",
            targetInflation = 2.0,
            neutralRateEstimate = 2.75,
            debtToGdp = 98.4,
            tenYearYield = 4.32,
            twoYearYield = 4.10,
            seriesByHorizon = series
        )
    }

    // 🇨🇳 CHINA
    private fun createChina(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 0.4), MacroDataPoint("Dec '25", 0.3),
                MacroDataPoint("Jan '26", 0.5), MacroDataPoint("Feb '26", 0.7),
                MacroDataPoint("Mar '26", 0.6), MacroDataPoint("Apr '26", 0.5),
                MacroDataPoint("May '26", 0.6), MacroDataPoint("Jun '26", 0.8),
                MacroDataPoint("Jul '26", 0.7), MacroDataPoint("Aug '26", 0.8),
                MacroDataPoint("Sep '26", 0.9), MacroDataPoint("Oct '26", 0.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 3.10), MacroDataPoint("Dec '25", 3.00),
                MacroDataPoint("Jan '26", 3.00), MacroDataPoint("Feb '26", 2.90),
                MacroDataPoint("Mar '26", 2.90), MacroDataPoint("Apr '26", 2.90),
                MacroDataPoint("May '26", 2.80), MacroDataPoint("Jun '26", 2.80),
                MacroDataPoint("Jul '26", 2.80), MacroDataPoint("Aug '26", 2.75),
                MacroDataPoint("Sep '26", 2.75), MacroDataPoint("Oct '26", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 4.9), MacroDataPoint("Q1 '26", 4.8),
                MacroDataPoint("Q2 '26", 4.7), MacroDataPoint("Q3 '26", 4.9),
                MacroDataPoint("Q4 '26", 5.0)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 5.1), MacroDataPoint("Dec '25", 5.1),
                MacroDataPoint("Jan '26", 5.2), MacroDataPoint("Feb '26", 5.3),
                MacroDataPoint("Mar '26", 5.2), MacroDataPoint("Apr '26", 5.0),
                MacroDataPoint("May '26", 5.0), MacroDataPoint("Jun '26", 5.1),
                MacroDataPoint("Jul '26", 5.2), MacroDataPoint("Aug '26", 5.2),
                MacroDataPoint("Sep '26", 5.1), MacroDataPoint("Oct '26", 5.0)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", -0.3), MacroDataPoint("Q1 '24", 0.1),
                MacroDataPoint("Q2 '24", 0.3), MacroDataPoint("Q3 '24", 0.4),
                MacroDataPoint("Q4 '24", 0.3), MacroDataPoint("Q1 '25", 0.5),
                MacroDataPoint("Q2 '25", 0.4), MacroDataPoint("Q3 '25", 0.5),
                MacroDataPoint("Q4 '25", 0.4), MacroDataPoint("Q1 '26", 0.6),
                MacroDataPoint("Q2 '26", 0.6), MacroDataPoint("Q3 '26", 0.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 3.45), MacroDataPoint("Q1 '24", 3.45),
                MacroDataPoint("Q2 '24", 3.45), MacroDataPoint("Q3 '24", 3.35),
                MacroDataPoint("Q4 '24", 3.10), MacroDataPoint("Q1 '25", 3.10),
                MacroDataPoint("Q2 '25", 3.10), MacroDataPoint("Q3 '25", 3.10),
                MacroDataPoint("Q4 '25", 3.00), MacroDataPoint("Q1 '26", 2.90),
                MacroDataPoint("Q2 '26", 2.80), MacroDataPoint("Q3 '26", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 5.2), MacroDataPoint("Q1 '24", 5.3),
                MacroDataPoint("Q2 '24", 4.7), MacroDataPoint("Q3 '24", 4.6),
                MacroDataPoint("Q4 '24", 4.8), MacroDataPoint("Q1 '25", 4.8),
                MacroDataPoint("Q2 '25", 4.9), MacroDataPoint("Q3 '25", 4.8),
                MacroDataPoint("Q4 '25", 4.9), MacroDataPoint("Q1 '26", 4.8),
                MacroDataPoint("Q2 '26", 4.7), MacroDataPoint("Q3 '26", 5.0)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 5.1), MacroDataPoint("Q1 '24", 5.2),
                MacroDataPoint("Q2 '24", 5.0), MacroDataPoint("Q3 '24", 5.1),
                MacroDataPoint("Q4 '24", 5.1), MacroDataPoint("Q1 '25", 5.1),
                MacroDataPoint("Q2 '25", 5.1), MacroDataPoint("Q3 '25", 5.0),
                MacroDataPoint("Q4 '25", 5.1), MacroDataPoint("Q1 '26", 5.2),
                MacroDataPoint("Q2 '26", 5.0), MacroDataPoint("Q3 '26", 5.0)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 0.9), MacroDataPoint("2022", 2.0),
                MacroDataPoint("2023", 0.2), MacroDataPoint("2024", 0.4),
                MacroDataPoint("2025", 0.4), MacroDataPoint("2026", 0.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 3.80), MacroDataPoint("2022", 3.65),
                MacroDataPoint("2023", 3.45), MacroDataPoint("2024", 3.10),
                MacroDataPoint("2025", 3.00), MacroDataPoint("2026", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 8.4), MacroDataPoint("2022", 3.0),
                MacroDataPoint("2023", 5.2), MacroDataPoint("2024", 4.8),
                MacroDataPoint("2025", 4.9), MacroDataPoint("2026", 4.9)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 5.1), MacroDataPoint("2022", 5.6),
                MacroDataPoint("2023", 5.2), MacroDataPoint("2024", 5.1),
                MacroDataPoint("2025", 5.1), MacroDataPoint("2026", 5.0)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 2.0), MacroDataPoint("2017", 1.6), MacroDataPoint("2018", 2.1),
                MacroDataPoint("2019", 2.9), MacroDataPoint("2020", 2.5), MacroDataPoint("2021", 0.9),
                MacroDataPoint("2022", 2.0), MacroDataPoint("2023", 0.2), MacroDataPoint("2024", 0.4),
                MacroDataPoint("2025", 0.4), MacroDataPoint("2026", 0.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 4.35), MacroDataPoint("2017", 4.35), MacroDataPoint("2018", 4.35),
                MacroDataPoint("2019", 4.15), MacroDataPoint("2020", 3.85), MacroDataPoint("2021", 3.80),
                MacroDataPoint("2022", 3.65), MacroDataPoint("2023", 3.45), MacroDataPoint("2024", 3.10),
                MacroDataPoint("2025", 3.00), MacroDataPoint("2026", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 6.8), MacroDataPoint("2017", 6.9), MacroDataPoint("2018", 6.7),
                MacroDataPoint("2019", 6.0), MacroDataPoint("2020", 2.2), MacroDataPoint("2021", 8.4),
                MacroDataPoint("2022", 3.0), MacroDataPoint("2023", 5.2), MacroDataPoint("2024", 4.8),
                MacroDataPoint("2025", 4.9), MacroDataPoint("2026", 4.9)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 4.0), MacroDataPoint("2017", 3.9), MacroDataPoint("2018", 4.9),
                MacroDataPoint("2019", 5.2), MacroDataPoint("2020", 5.6), MacroDataPoint("2021", 5.1),
                MacroDataPoint("2022", 5.6), MacroDataPoint("2023", 5.2), MacroDataPoint("2024", 5.1),
                MacroDataPoint("2025", 5.1), MacroDataPoint("2026", 5.0)
            )
        )

        return CountryMacroProfile(
            id = "CN",
            name = "China",
            flag = "🇨🇳",
            centralBank = "People's Bank of China",
            currency = "CNY",
            targetInflation = 3.0,
            neutralRateEstimate = 3.20,
            debtToGdp = 83.6,
            tenYearYield = 2.11,
            twoYearYield = 1.48,
            seriesByHorizon = series
        )
    }

    // 🇩🇪 GERMANY
    private fun createGermany(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 2.2), MacroDataPoint("Dec '25", 2.1),
                MacroDataPoint("Jan '26", 2.0), MacroDataPoint("Feb '26", 1.9),
                MacroDataPoint("Mar '26", 1.9), MacroDataPoint("Apr '26", 1.8),
                MacroDataPoint("May '26", 1.8), MacroDataPoint("Jun '26", 2.0),
                MacroDataPoint("Jul '26", 1.9), MacroDataPoint("Aug '26", 1.8),
                MacroDataPoint("Sep '26", 1.7), MacroDataPoint("Oct '26", 1.8)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 3.50), MacroDataPoint("Dec '25", 3.25),
                MacroDataPoint("Jan '26", 3.25), MacroDataPoint("Feb '26", 3.00),
                MacroDataPoint("Mar '26", 3.00), MacroDataPoint("Apr '26", 2.75),
                MacroDataPoint("May '26", 2.75), MacroDataPoint("Jun '26", 2.75),
                MacroDataPoint("Jul '26", 2.50), MacroDataPoint("Aug '26", 2.50),
                MacroDataPoint("Sep '26", 2.50), MacroDataPoint("Oct '26", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 0.2), MacroDataPoint("Q1 '26", 0.4),
                MacroDataPoint("Q2 '26", 0.6), MacroDataPoint("Q3 '26", 0.7),
                MacroDataPoint("Q4 '26", 0.8)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 6.0), MacroDataPoint("Dec '25", 6.0),
                MacroDataPoint("Jan '26", 6.1), MacroDataPoint("Feb '26", 6.1),
                MacroDataPoint("Mar '26", 6.0), MacroDataPoint("Apr '26", 5.9),
                MacroDataPoint("May '26", 5.9), MacroDataPoint("Jun '26", 6.0),
                MacroDataPoint("Jul '26", 6.0), MacroDataPoint("Aug '26", 5.9),
                MacroDataPoint("Sep '26", 5.8), MacroDataPoint("Oct '26", 5.8)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 3.7), MacroDataPoint("Q1 '24", 2.5),
                MacroDataPoint("Q2 '24", 2.2), MacroDataPoint("Q3 '24", 1.8),
                MacroDataPoint("Q4 '24", 2.1), MacroDataPoint("Q1 '25", 2.2),
                MacroDataPoint("Q2 '25", 2.1), MacroDataPoint("Q3 '25", 2.1),
                MacroDataPoint("Q4 '25", 2.1), MacroDataPoint("Q1 '26", 1.9),
                MacroDataPoint("Q2 '26", 1.8), MacroDataPoint("Q3 '26", 1.8)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 4.00), MacroDataPoint("Q1 '24", 4.00),
                MacroDataPoint("Q2 '24", 3.75), MacroDataPoint("Q3 '24", 3.50),
                MacroDataPoint("Q4 '24", 3.25), MacroDataPoint("Q1 '25", 3.50),
                MacroDataPoint("Q2 '25", 3.50), MacroDataPoint("Q3 '25", 3.50),
                MacroDataPoint("Q4 '25", 3.25), MacroDataPoint("Q1 '26", 3.00),
                MacroDataPoint("Q2 '26", 2.75), MacroDataPoint("Q3 '26", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", -0.3), MacroDataPoint("Q1 '24", 0.2),
                MacroDataPoint("Q2 '24", -0.1), MacroDataPoint("Q3 '24", 0.1),
                MacroDataPoint("Q4 '24", 0.0), MacroDataPoint("Q1 '25", 0.2),
                MacroDataPoint("Q2 '25", 0.3), MacroDataPoint("Q3 '25", 0.3),
                MacroDataPoint("Q4 '25", 0.2), MacroDataPoint("Q1 '26", 0.4),
                MacroDataPoint("Q2 '26", 0.6), MacroDataPoint("Q3 '26", 0.7)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 5.8), MacroDataPoint("Q1 '24", 5.9),
                MacroDataPoint("Q2 '24", 6.0), MacroDataPoint("Q3 '24", 6.0),
                MacroDataPoint("Q4 '24", 6.1), MacroDataPoint("Q1 '25", 6.1),
                MacroDataPoint("Q2 '25", 6.1), MacroDataPoint("Q3 '25", 6.0),
                MacroDataPoint("Q4 '25", 6.0), MacroDataPoint("Q1 '26", 6.0),
                MacroDataPoint("Q2 '26", 5.9), MacroDataPoint("Q3 '26", 5.8)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 3.1), MacroDataPoint("2022", 8.6),
                MacroDataPoint("2023", 5.9), MacroDataPoint("2024", 2.2),
                MacroDataPoint("2025", 2.1), MacroDataPoint("2026", 1.8)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", -0.50), MacroDataPoint("2022", 2.00),
                MacroDataPoint("2023", 4.00), MacroDataPoint("2024", 3.25),
                MacroDataPoint("2025", 3.50), MacroDataPoint("2026", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 3.2), MacroDataPoint("2022", 1.8),
                MacroDataPoint("2023", -0.3), MacroDataPoint("2024", -0.1),
                MacroDataPoint("2025", 0.3), MacroDataPoint("2026", 0.7)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 5.7), MacroDataPoint("2022", 5.3),
                MacroDataPoint("2023", 5.7), MacroDataPoint("2024", 6.0),
                MacroDataPoint("2025", 6.1), MacroDataPoint("2026", 5.8)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 0.5), MacroDataPoint("2017", 1.5), MacroDataPoint("2018", 1.7),
                MacroDataPoint("2019", 1.4), MacroDataPoint("2020", 0.5), MacroDataPoint("2021", 3.1),
                MacroDataPoint("2022", 8.6), MacroDataPoint("2023", 5.9), MacroDataPoint("2024", 2.2),
                MacroDataPoint("2025", 2.1), MacroDataPoint("2026", 1.8)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", -0.40), MacroDataPoint("2017", -0.40), MacroDataPoint("2018", -0.40),
                MacroDataPoint("2019", -0.50), MacroDataPoint("2020", -0.50), MacroDataPoint("2021", -0.50),
                MacroDataPoint("2022", 2.00), MacroDataPoint("2023", 4.00), MacroDataPoint("2024", 3.25),
                MacroDataPoint("2025", 3.50), MacroDataPoint("2026", 2.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 2.2), MacroDataPoint("2017", 2.7), MacroDataPoint("2018", 1.0),
                MacroDataPoint("2019", 1.1), MacroDataPoint("2020", -3.8), MacroDataPoint("2021", 3.2),
                MacroDataPoint("2022", 1.8), MacroDataPoint("2023", -0.3), MacroDataPoint("2024", -0.1),
                MacroDataPoint("2025", 0.3), MacroDataPoint("2026", 0.7)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 6.1), MacroDataPoint("2017", 5.7), MacroDataPoint("2018", 5.2),
                MacroDataPoint("2019", 5.0), MacroDataPoint("2020", 5.9), MacroDataPoint("2021", 5.7),
                MacroDataPoint("2022", 5.3), MacroDataPoint("2023", 5.7), MacroDataPoint("2024", 6.0),
                MacroDataPoint("2025", 6.1), MacroDataPoint("2026", 5.8)
            )
        )

        return CountryMacroProfile(
            id = "DE",
            name = "Germany",
            flag = "🇩🇪",
            centralBank = "Deutsche Bundesbank / ECB",
            currency = "EUR",
            targetInflation = 2.0,
            neutralRateEstimate = 1.75,
            debtToGdp = 63.6,
            tenYearYield = 2.28,
            twoYearYield = 2.15,
            seriesByHorizon = series
        )
    }

    // 🇮🇳 INDIA
    private fun createIndia(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 5.2), MacroDataPoint("Dec '25", 5.0),
                MacroDataPoint("Jan '26", 4.8), MacroDataPoint("Feb '26", 4.7),
                MacroDataPoint("Mar '26", 4.6), MacroDataPoint("Apr '26", 4.5),
                MacroDataPoint("May '26", 4.4), MacroDataPoint("Jun '26", 4.6),
                MacroDataPoint("Jul '26", 4.5), MacroDataPoint("Aug '26", 4.3),
                MacroDataPoint("Sep '26", 4.2), MacroDataPoint("Oct '26", 4.3)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 6.50), MacroDataPoint("Dec '25", 6.50),
                MacroDataPoint("Jan '26", 6.50), MacroDataPoint("Feb '26", 6.25),
                MacroDataPoint("Mar '26", 6.25), MacroDataPoint("Apr '26", 6.25),
                MacroDataPoint("May '26", 6.00), MacroDataPoint("Jun '26", 6.00),
                MacroDataPoint("Jul '26", 6.00), MacroDataPoint("Aug '26", 5.75),
                MacroDataPoint("Sep '26", 5.75), MacroDataPoint("Oct '26", 5.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 6.8), MacroDataPoint("Q1 '26", 7.0),
                MacroDataPoint("Q2 '26", 7.2), MacroDataPoint("Q3 '26", 7.1),
                MacroDataPoint("Q4 '26", 7.3)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 7.5), MacroDataPoint("Dec '25", 7.4),
                MacroDataPoint("Jan '26", 7.3), MacroDataPoint("Feb '26", 7.2),
                MacroDataPoint("Mar '26", 7.1), MacroDataPoint("Apr '26", 7.0),
                MacroDataPoint("May '26", 7.0), MacroDataPoint("Jun '26", 7.1),
                MacroDataPoint("Jul '26", 7.0), MacroDataPoint("Aug '26", 6.9),
                MacroDataPoint("Sep '26", 6.8), MacroDataPoint("Oct '26", 6.8)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 5.6), MacroDataPoint("Q1 '24", 5.0),
                MacroDataPoint("Q2 '24", 4.9), MacroDataPoint("Q3 '24", 5.4),
                MacroDataPoint("Q4 '24", 5.1), MacroDataPoint("Q1 '25", 4.9),
                MacroDataPoint("Q2 '25", 5.0), MacroDataPoint("Q3 '25", 4.9),
                MacroDataPoint("Q4 '25", 5.0), MacroDataPoint("Q1 '26", 4.7),
                MacroDataPoint("Q2 '26", 4.5), MacroDataPoint("Q3 '26", 4.3)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 6.50), MacroDataPoint("Q1 '24", 6.50),
                MacroDataPoint("Q2 '24", 6.50), MacroDataPoint("Q3 '24", 6.50),
                MacroDataPoint("Q4 '24", 6.50), MacroDataPoint("Q1 '25", 6.50),
                MacroDataPoint("Q2 '25", 6.50), MacroDataPoint("Q3 '25", 6.50),
                MacroDataPoint("Q4 '25", 6.50), MacroDataPoint("Q1 '26", 6.25),
                MacroDataPoint("Q2 '26", 6.00), MacroDataPoint("Q3 '26", 5.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 8.4), MacroDataPoint("Q1 '24", 7.8),
                MacroDataPoint("Q2 '24", 6.7), MacroDataPoint("Q3 '24", 6.5),
                MacroDataPoint("Q4 '24", 6.9), MacroDataPoint("Q1 '25", 7.0),
                MacroDataPoint("Q2 '25", 7.1), MacroDataPoint("Q3 '25", 6.9),
                MacroDataPoint("Q4 '25", 6.8), MacroDataPoint("Q1 '26", 7.0),
                MacroDataPoint("Q2 '26", 7.2), MacroDataPoint("Q3 '26", 7.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 8.1), MacroDataPoint("Q1 '24", 7.6),
                MacroDataPoint("Q2 '24", 9.2), MacroDataPoint("Q3 '24", 7.8),
                MacroDataPoint("Q4 '24", 7.6), MacroDataPoint("Q1 '25", 7.5),
                MacroDataPoint("Q2 '25", 7.4), MacroDataPoint("Q3 '25", 7.3),
                MacroDataPoint("Q4 '25", 7.4), MacroDataPoint("Q1 '26", 7.2),
                MacroDataPoint("Q2 '26", 7.0), MacroDataPoint("Q3 '26", 6.8)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 5.5), MacroDataPoint("2022", 6.7),
                MacroDataPoint("2023", 5.7), MacroDataPoint("2024", 4.9),
                MacroDataPoint("2025", 5.0), MacroDataPoint("2026", 4.3)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 4.00), MacroDataPoint("2022", 6.25),
                MacroDataPoint("2023", 6.50), MacroDataPoint("2024", 6.50),
                MacroDataPoint("2025", 6.50), MacroDataPoint("2026", 5.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 8.7), MacroDataPoint("2022", 7.2),
                MacroDataPoint("2023", 7.7), MacroDataPoint("2024", 7.0),
                MacroDataPoint("2025", 7.0), MacroDataPoint("2026", 7.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 7.8), MacroDataPoint("2022", 7.3),
                MacroDataPoint("2023", 7.9), MacroDataPoint("2024", 8.0),
                MacroDataPoint("2025", 7.4), MacroDataPoint("2026", 6.8)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 4.9), MacroDataPoint("2017", 3.3), MacroDataPoint("2018", 3.9),
                MacroDataPoint("2019", 3.7), MacroDataPoint("2020", 6.6), MacroDataPoint("2021", 5.5),
                MacroDataPoint("2022", 6.7), MacroDataPoint("2023", 5.7), MacroDataPoint("2024", 4.9),
                MacroDataPoint("2025", 5.0), MacroDataPoint("2026", 4.3)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 6.25), MacroDataPoint("2017", 6.00), MacroDataPoint("2018", 6.50),
                MacroDataPoint("2019", 5.15), MacroDataPoint("2020", 4.00), MacroDataPoint("2021", 4.00),
                MacroDataPoint("2022", 6.25), MacroDataPoint("2023", 6.50), MacroDataPoint("2024", 6.50),
                MacroDataPoint("2025", 6.50), MacroDataPoint("2026", 5.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 8.3), MacroDataPoint("2017", 6.8), MacroDataPoint("2018", 6.5),
                MacroDataPoint("2019", 3.9), MacroDataPoint("2020", -5.8), MacroDataPoint("2021", 8.7),
                MacroDataPoint("2022", 7.2), MacroDataPoint("2023", 7.7), MacroDataPoint("2024", 7.0),
                MacroDataPoint("2025", 7.0), MacroDataPoint("2026", 7.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 7.3), MacroDataPoint("2017", 7.1), MacroDataPoint("2018", 6.8),
                MacroDataPoint("2019", 7.0), MacroDataPoint("2020", 10.2), MacroDataPoint("2021", 7.8),
                MacroDataPoint("2022", 7.3), MacroDataPoint("2023", 7.9), MacroDataPoint("2024", 8.0),
                MacroDataPoint("2025", 7.4), MacroDataPoint("2026", 6.8)
            )
        )

        return CountryMacroProfile(
            id = "IN",
            name = "India",
            flag = "🇮🇳",
            centralBank = "Reserve Bank of India",
            currency = "INR",
            targetInflation = 4.0,
            neutralRateEstimate = 5.25,
            debtToGdp = 81.2,
            tenYearYield = 6.82,
            twoYearYield = 6.45,
            seriesByHorizon = series
        )
    }

    // 🇨🇦 CANADA
    private fun createCanada(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 2.2), MacroDataPoint("Dec '25", 2.1),
                MacroDataPoint("Jan '26", 2.1), MacroDataPoint("Feb '26", 2.0),
                MacroDataPoint("Mar '26", 2.0), MacroDataPoint("Apr '26", 2.1),
                MacroDataPoint("May '26", 2.2), MacroDataPoint("Jun '26", 2.1),
                MacroDataPoint("Jul '26", 2.0), MacroDataPoint("Aug '26", 1.9),
                MacroDataPoint("Sep '26", 1.9), MacroDataPoint("Oct '26", 2.0)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 3.75), MacroDataPoint("Dec '25", 3.50),
                MacroDataPoint("Jan '26", 3.50), MacroDataPoint("Feb '26", 3.25),
                MacroDataPoint("Mar '26", 3.25), MacroDataPoint("Apr '26", 3.00),
                MacroDataPoint("May '26", 3.00), MacroDataPoint("Jun '26", 3.00),
                MacroDataPoint("Jul '26", 2.75), MacroDataPoint("Aug '26", 2.75),
                MacroDataPoint("Sep '26", 2.75), MacroDataPoint("Oct '26", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 1.3), MacroDataPoint("Q1 '26", 1.5),
                MacroDataPoint("Q2 '26", 1.6), MacroDataPoint("Q3 '26", 1.7),
                MacroDataPoint("Q4 '26", 1.8)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 6.5), MacroDataPoint("Dec '25", 6.5),
                MacroDataPoint("Jan '26", 6.6), MacroDataPoint("Feb '26", 6.6),
                MacroDataPoint("Mar '26", 6.5), MacroDataPoint("Apr '26", 6.4),
                MacroDataPoint("May '26", 6.4), MacroDataPoint("Jun '26", 6.3),
                MacroDataPoint("Jul '26", 6.3), MacroDataPoint("Aug '26", 6.2),
                MacroDataPoint("Sep '26", 6.2), MacroDataPoint("Oct '26", 6.1)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 3.1), MacroDataPoint("Q1 '24", 2.9),
                MacroDataPoint("Q2 '24", 2.7), MacroDataPoint("Q3 '24", 2.0),
                MacroDataPoint("Q4 '24", 2.2), MacroDataPoint("Q1 '25", 2.3),
                MacroDataPoint("Q2 '25", 2.2), MacroDataPoint("Q3 '25", 2.2),
                MacroDataPoint("Q4 '25", 2.1), MacroDataPoint("Q1 '26", 2.0),
                MacroDataPoint("Q2 '26", 2.1), MacroDataPoint("Q3 '26", 2.0)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 5.00), MacroDataPoint("Q1 '24", 5.00),
                MacroDataPoint("Q2 '24", 4.75), MacroDataPoint("Q3 '24", 4.25),
                MacroDataPoint("Q4 '24", 3.75), MacroDataPoint("Q1 '25", 3.75),
                MacroDataPoint("Q2 '25", 3.75), MacroDataPoint("Q3 '25", 3.75),
                MacroDataPoint("Q4 '25", 3.50), MacroDataPoint("Q1 '26", 3.25),
                MacroDataPoint("Q2 '26", 3.00), MacroDataPoint("Q3 '26", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 0.9), MacroDataPoint("Q1 '24", 1.7),
                MacroDataPoint("Q2 '24", 2.1), MacroDataPoint("Q3 '24", 1.0),
                MacroDataPoint("Q4 '24", 1.2), MacroDataPoint("Q1 '25", 1.4),
                MacroDataPoint("Q2 '25", 1.5), MacroDataPoint("Q3 '25", 1.4),
                MacroDataPoint("Q4 '25", 1.3), MacroDataPoint("Q1 '26", 1.5),
                MacroDataPoint("Q2 '26", 1.6), MacroDataPoint("Q3 '26", 1.7)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 5.8), MacroDataPoint("Q1 '24", 6.1),
                MacroDataPoint("Q2 '24", 6.4), MacroDataPoint("Q3 '24", 6.5),
                MacroDataPoint("Q4 '24", 6.5), MacroDataPoint("Q1 '25", 6.6),
                MacroDataPoint("Q2 '25", 6.5), MacroDataPoint("Q3 '25", 6.5),
                MacroDataPoint("Q4 '25", 6.5), MacroDataPoint("Q1 '26", 6.5),
                MacroDataPoint("Q2 '26", 6.4), MacroDataPoint("Q3 '26", 6.1)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 3.4), MacroDataPoint("2022", 6.8),
                MacroDataPoint("2023", 3.9), MacroDataPoint("2024", 2.4),
                MacroDataPoint("2025", 2.2), MacroDataPoint("2026", 2.0)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 0.25), MacroDataPoint("2022", 4.25),
                MacroDataPoint("2023", 5.00), MacroDataPoint("2024", 3.75),
                MacroDataPoint("2025", 3.75), MacroDataPoint("2026", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 5.3), MacroDataPoint("2022", 3.8),
                MacroDataPoint("2023", 1.2), MacroDataPoint("2024", 1.3),
                MacroDataPoint("2025", 1.4), MacroDataPoint("2026", 1.7)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 7.5), MacroDataPoint("2022", 5.3),
                MacroDataPoint("2023", 5.4), MacroDataPoint("2024", 6.4),
                MacroDataPoint("2025", 6.5), MacroDataPoint("2026", 6.1)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 1.4), MacroDataPoint("2017", 1.6), MacroDataPoint("2018", 2.3),
                MacroDataPoint("2019", 1.9), MacroDataPoint("2020", 0.7), MacroDataPoint("2021", 3.4),
                MacroDataPoint("2022", 6.8), MacroDataPoint("2023", 3.9), MacroDataPoint("2024", 2.4),
                MacroDataPoint("2025", 2.2), MacroDataPoint("2026", 2.0)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 0.50), MacroDataPoint("2017", 1.00), MacroDataPoint("2018", 1.75),
                MacroDataPoint("2019", 1.75), MacroDataPoint("2020", 0.25), MacroDataPoint("2021", 0.25),
                MacroDataPoint("2022", 4.25), MacroDataPoint("2023", 5.00), MacroDataPoint("2024", 3.75),
                MacroDataPoint("2025", 3.75), MacroDataPoint("2026", 2.75)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 1.0), MacroDataPoint("2017", 3.0), MacroDataPoint("2018", 2.8),
                MacroDataPoint("2019", 1.9), MacroDataPoint("2020", -5.1), MacroDataPoint("2021", 5.3),
                MacroDataPoint("2022", 3.8), MacroDataPoint("2023", 1.2), MacroDataPoint("2024", 1.3),
                MacroDataPoint("2025", 1.4), MacroDataPoint("2026", 1.7)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 7.0), MacroDataPoint("2017", 6.3), MacroDataPoint("2018", 5.8),
                MacroDataPoint("2019", 5.7), MacroDataPoint("2020", 9.6), MacroDataPoint("2021", 7.5),
                MacroDataPoint("2022", 5.3), MacroDataPoint("2023", 5.4), MacroDataPoint("2024", 6.4),
                MacroDataPoint("2025", 6.5), MacroDataPoint("2026", 6.1)
            )
        )

        return CountryMacroProfile(
            id = "CA",
            name = "Canada",
            flag = "🇨🇦",
            centralBank = "Bank of Canada",
            currency = "CAD",
            targetInflation = 2.0,
            neutralRateEstimate = 2.50,
            debtToGdp = 104.7,
            tenYearYield = 3.25,
            twoYearYield = 2.98,
            seriesByHorizon = series
        )
    }

    // 🇦🇺 AUSTRALIA
    private fun createAustralia(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 3.3), MacroDataPoint("Dec '25", 3.2),
                MacroDataPoint("Jan '26", 3.1), MacroDataPoint("Feb '26", 3.0),
                MacroDataPoint("Mar '26", 2.9), MacroDataPoint("Apr '26", 2.8),
                MacroDataPoint("May '26", 2.8), MacroDataPoint("Jun '26", 2.7),
                MacroDataPoint("Jul '26", 2.7), MacroDataPoint("Aug '26", 2.6),
                MacroDataPoint("Sep '26", 2.5), MacroDataPoint("Oct '26", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 4.35), MacroDataPoint("Dec '25", 4.35),
                MacroDataPoint("Jan '26", 4.35), MacroDataPoint("Feb '26", 4.10),
                MacroDataPoint("Mar '26", 4.10), MacroDataPoint("Apr '26", 3.85),
                MacroDataPoint("May '26", 3.85), MacroDataPoint("Jun '26", 3.85),
                MacroDataPoint("Jul '26", 3.60), MacroDataPoint("Aug '26", 3.60),
                MacroDataPoint("Sep '26", 3.60), MacroDataPoint("Oct '26", 3.35)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 1.5), MacroDataPoint("Q1 '26", 1.7),
                MacroDataPoint("Q2 '26", 1.8), MacroDataPoint("Q3 '26", 1.9),
                MacroDataPoint("Q4 '26", 2.0)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 4.1), MacroDataPoint("Dec '25", 4.1),
                MacroDataPoint("Jan '26", 4.2), MacroDataPoint("Feb '26", 4.2),
                MacroDataPoint("Mar '26", 4.1), MacroDataPoint("Apr '26", 4.1),
                MacroDataPoint("May '26", 4.0), MacroDataPoint("Jun '26", 4.0),
                MacroDataPoint("Jul '26", 4.1), MacroDataPoint("Aug '26", 4.0),
                MacroDataPoint("Sep '26", 4.0), MacroDataPoint("Oct '26", 3.9)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 4.1), MacroDataPoint("Q1 '24", 3.6),
                MacroDataPoint("Q2 '24", 3.8), MacroDataPoint("Q3 '24", 2.8),
                MacroDataPoint("Q4 '24", 3.2), MacroDataPoint("Q1 '25", 3.3),
                MacroDataPoint("Q2 '25", 3.2), MacroDataPoint("Q3 '25", 3.3),
                MacroDataPoint("Q4 '25", 3.2), MacroDataPoint("Q1 '26", 2.9),
                MacroDataPoint("Q2 '26", 2.7), MacroDataPoint("Q3 '26", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 4.35), MacroDataPoint("Q1 '24", 4.35),
                MacroDataPoint("Q2 '24", 4.35), MacroDataPoint("Q3 '24", 4.35),
                MacroDataPoint("Q4 '24", 4.35), MacroDataPoint("Q1 '25", 4.35),
                MacroDataPoint("Q2 '25", 4.35), MacroDataPoint("Q3 '25", 4.35),
                MacroDataPoint("Q4 '25", 4.35), MacroDataPoint("Q1 '26", 4.10),
                MacroDataPoint("Q2 '26", 3.85), MacroDataPoint("Q3 '26", 3.35)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 1.6), MacroDataPoint("Q1 '24", 1.2),
                MacroDataPoint("Q2 '24", 1.0), MacroDataPoint("Q3 '24", 0.8),
                MacroDataPoint("Q4 '24", 1.1), MacroDataPoint("Q1 '25", 1.4),
                MacroDataPoint("Q2 '25", 1.5), MacroDataPoint("Q3 '25", 1.5),
                MacroDataPoint("Q4 '25", 1.5), MacroDataPoint("Q1 '26", 1.7),
                MacroDataPoint("Q2 '26", 1.8), MacroDataPoint("Q3 '26", 1.9)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 3.9), MacroDataPoint("Q1 '24", 3.8),
                MacroDataPoint("Q2 '24", 4.1), MacroDataPoint("Q3 '24", 4.1),
                MacroDataPoint("Q4 '24", 4.1), MacroDataPoint("Q1 '25", 4.1),
                MacroDataPoint("Q2 '25", 4.1), MacroDataPoint("Q3 '25", 4.1),
                MacroDataPoint("Q4 '25", 4.1), MacroDataPoint("Q1 '26", 4.1),
                MacroDataPoint("Q2 '26", 4.0), MacroDataPoint("Q3 '26", 3.9)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 3.5), MacroDataPoint("2022", 7.8),
                MacroDataPoint("2023", 4.1), MacroDataPoint("2024", 2.8),
                MacroDataPoint("2025", 3.2), MacroDataPoint("2026", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 0.10), MacroDataPoint("2022", 3.10),
                MacroDataPoint("2023", 4.35), MacroDataPoint("2024", 4.35),
                MacroDataPoint("2025", 4.35), MacroDataPoint("2026", 3.35)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 5.2), MacroDataPoint("2022", 3.7),
                MacroDataPoint("2023", 2.0), MacroDataPoint("2024", 1.1),
                MacroDataPoint("2025", 1.5), MacroDataPoint("2026", 1.9)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 5.1), MacroDataPoint("2022", 3.7),
                MacroDataPoint("2023", 3.7), MacroDataPoint("2024", 4.1),
                MacroDataPoint("2025", 4.1), MacroDataPoint("2026", 3.9)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 1.5), MacroDataPoint("2017", 1.9), MacroDataPoint("2018", 1.8),
                MacroDataPoint("2019", 1.8), MacroDataPoint("2020", 0.9), MacroDataPoint("2021", 3.5),
                MacroDataPoint("2022", 7.8), MacroDataPoint("2023", 4.1), MacroDataPoint("2024", 2.8),
                MacroDataPoint("2025", 3.2), MacroDataPoint("2026", 2.5)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 1.50), MacroDataPoint("2017", 1.50), MacroDataPoint("2018", 1.50),
                MacroDataPoint("2019", 0.75), MacroDataPoint("2020", 0.10), MacroDataPoint("2021", 0.10),
                MacroDataPoint("2022", 3.10), MacroDataPoint("2023", 4.35), MacroDataPoint("2024", 4.35),
                MacroDataPoint("2025", 4.35), MacroDataPoint("2026", 3.35)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", 2.7), MacroDataPoint("2017", 2.4), MacroDataPoint("2018", 2.8),
                MacroDataPoint("2019", 2.1), MacroDataPoint("2020", -0.3), MacroDataPoint("2021", 5.2),
                MacroDataPoint("2022", 3.7), MacroDataPoint("2023", 2.0), MacroDataPoint("2024", 1.1),
                MacroDataPoint("2025", 1.5), MacroDataPoint("2026", 1.9)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 5.7), MacroDataPoint("2017", 5.6), MacroDataPoint("2018", 5.3),
                MacroDataPoint("2019", 5.2), MacroDataPoint("2020", 6.5), MacroDataPoint("2021", 5.1),
                MacroDataPoint("2022", 3.7), MacroDataPoint("2023", 3.7), MacroDataPoint("2024", 4.1),
                MacroDataPoint("2025", 4.1), MacroDataPoint("2026", 3.9)
            )
        )

        return CountryMacroProfile(
            id = "AU",
            name = "Australia",
            flag = "🇦🇺",
            centralBank = "Reserve Bank of Australia",
            currency = "AUD",
            targetInflation = 2.5,
            neutralRateEstimate = 3.00,
            debtToGdp = 54.1,
            tenYearYield = 4.41,
            twoYearYield = 3.98,
            seriesByHorizon = series
        )
    }

    // 🇧🇷 BRAZIL
    private fun createBrazil(): CountryMacroProfile {
        val series = mutableMapOf<TimeHorizon, Map<MacroIndicatorType, List<MacroDataPoint>>>()
        series[TimeHorizon.ONE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Nov '25", 4.5), MacroDataPoint("Dec '25", 4.6),
                MacroDataPoint("Jan '26", 4.5), MacroDataPoint("Feb '26", 4.4),
                MacroDataPoint("Mar '26", 4.3), MacroDataPoint("Apr '26", 4.2),
                MacroDataPoint("May '26", 4.1), MacroDataPoint("Jun '26", 4.0),
                MacroDataPoint("Jul '26", 4.0), MacroDataPoint("Aug '26", 3.9),
                MacroDataPoint("Sep '26", 3.8), MacroDataPoint("Oct '26", 3.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Nov '25", 11.25), MacroDataPoint("Dec '25", 11.75),
                MacroDataPoint("Jan '26", 11.75), MacroDataPoint("Feb '26", 12.00),
                MacroDataPoint("Mar '26", 12.00), MacroDataPoint("Apr '26", 11.75),
                MacroDataPoint("May '26", 11.50), MacroDataPoint("Jun '26", 11.25),
                MacroDataPoint("Jul '26", 11.00), MacroDataPoint("Aug '26", 10.75),
                MacroDataPoint("Sep '26", 10.50), MacroDataPoint("Oct '26", 10.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '25", 2.8), MacroDataPoint("Q1 '26", 2.9),
                MacroDataPoint("Q2 '26", 3.0), MacroDataPoint("Q3 '26", 3.1),
                MacroDataPoint("Q4 '26", 3.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Nov '25", 6.8), MacroDataPoint("Dec '25", 6.6),
                MacroDataPoint("Jan '26", 6.9), MacroDataPoint("Feb '26", 6.8),
                MacroDataPoint("Mar '26", 6.7), MacroDataPoint("Apr '26", 6.6),
                MacroDataPoint("May '26", 6.5), MacroDataPoint("Jun '26", 6.4),
                MacroDataPoint("Jul '26", 6.4), MacroDataPoint("Aug '26", 6.3),
                MacroDataPoint("Sep '26", 6.3), MacroDataPoint("Oct '26", 6.2)
            )
        )

        series[TimeHorizon.THREE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("Q4 '23", 4.6), MacroDataPoint("Q1 '24", 3.9),
                MacroDataPoint("Q2 '24", 4.2), MacroDataPoint("Q3 '24", 4.4),
                MacroDataPoint("Q4 '24", 4.8), MacroDataPoint("Q1 '25", 4.6),
                MacroDataPoint("Q2 '25", 4.5), MacroDataPoint("Q3 '25", 4.6),
                MacroDataPoint("Q4 '25", 4.6), MacroDataPoint("Q1 '26", 4.4),
                MacroDataPoint("Q2 '26", 4.1), MacroDataPoint("Q3 '26", 3.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("Q4 '23", 11.75), MacroDataPoint("Q1 '24", 10.75),
                MacroDataPoint("Q2 '24", 10.50), MacroDataPoint("Q3 '24", 10.75),
                MacroDataPoint("Q4 '24", 11.25), MacroDataPoint("Q1 '25", 11.75),
                MacroDataPoint("Q2 '25", 11.75), MacroDataPoint("Q3 '25", 11.75),
                MacroDataPoint("Q4 '25", 11.75), MacroDataPoint("Q1 '26", 12.00),
                MacroDataPoint("Q2 '26", 11.50), MacroDataPoint("Q3 '26", 10.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("Q4 '23", 2.1), MacroDataPoint("Q1 '24", 2.5),
                MacroDataPoint("Q2 '24", 3.3), MacroDataPoint("Q3 '24", 4.0),
                MacroDataPoint("Q4 '24", 3.4), MacroDataPoint("Q1 '25", 3.0),
                MacroDataPoint("Q2 '25", 2.9), MacroDataPoint("Q3 '25", 2.8),
                MacroDataPoint("Q4 '25", 2.8), MacroDataPoint("Q1 '26", 2.9),
                MacroDataPoint("Q2 '26", 3.0), MacroDataPoint("Q3 '26", 3.2)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("Q4 '23", 7.4), MacroDataPoint("Q1 '24", 7.9),
                MacroDataPoint("Q2 '24", 6.9), MacroDataPoint("Q3 '24", 6.4),
                MacroDataPoint("Q4 '24", 6.2), MacroDataPoint("Q1 '25", 6.8),
                MacroDataPoint("Q2 '25", 6.6), MacroDataPoint("Q3 '25", 6.5),
                MacroDataPoint("Q4 '25", 6.6), MacroDataPoint("Q1 '26", 6.8),
                MacroDataPoint("Q2 '26", 6.5), MacroDataPoint("Q3 '26", 6.2)
            )
        )

        series[TimeHorizon.FIVE_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2021", 10.1), MacroDataPoint("2022", 5.8),
                MacroDataPoint("2023", 4.6), MacroDataPoint("2024", 4.8),
                MacroDataPoint("2025", 4.6), MacroDataPoint("2026", 3.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2021", 9.25), MacroDataPoint("2022", 13.75),
                MacroDataPoint("2023", 11.75), MacroDataPoint("2024", 11.25),
                MacroDataPoint("2025", 11.75), MacroDataPoint("2026", 10.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2021", 5.0), MacroDataPoint("2022", 3.0),
                MacroDataPoint("2023", 2.9), MacroDataPoint("2024", 3.4),
                MacroDataPoint("2025", 2.8), MacroDataPoint("2026", 3.1)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2021", 11.1), MacroDataPoint("2022", 7.9),
                MacroDataPoint("2023", 7.4), MacroDataPoint("2024", 6.4),
                MacroDataPoint("2025", 6.6), MacroDataPoint("2026", 6.2)
            )
        )

        series[TimeHorizon.TEN_YEAR] = mapOf(
            MacroIndicatorType.INFLATION to listOf(
                MacroDataPoint("2016", 6.3), MacroDataPoint("2017", 2.9), MacroDataPoint("2018", 3.7),
                MacroDataPoint("2019", 4.3), MacroDataPoint("2020", 4.5), MacroDataPoint("2021", 10.1),
                MacroDataPoint("2022", 5.8), MacroDataPoint("2023", 4.6), MacroDataPoint("2024", 4.8),
                MacroDataPoint("2025", 4.6), MacroDataPoint("2026", 3.9)
            ),
            MacroIndicatorType.INTEREST_RATE to listOf(
                MacroDataPoint("2016", 13.75), MacroDataPoint("2017", 7.00), MacroDataPoint("2018", 6.50),
                MacroDataPoint("2019", 4.50), MacroDataPoint("2020", 2.00), MacroDataPoint("2021", 9.25),
                MacroDataPoint("2022", 13.75), MacroDataPoint("2023", 11.75), MacroDataPoint("2024", 11.25),
                MacroDataPoint("2025", 11.75), MacroDataPoint("2026", 10.50)
            ),
            MacroIndicatorType.GDP_GROWTH to listOf(
                MacroDataPoint("2016", -3.3), MacroDataPoint("2017", 1.3), MacroDataPoint("2018", 1.8),
                MacroDataPoint("2019", 1.2), MacroDataPoint("2020", -3.3), MacroDataPoint("2021", 5.0),
                MacroDataPoint("2022", 3.0), MacroDataPoint("2023", 2.9), MacroDataPoint("2024", 3.4),
                MacroDataPoint("2025", 2.8), MacroDataPoint("2026", 3.1)
            ),
            MacroIndicatorType.UNEMPLOYMENT to listOf(
                MacroDataPoint("2016", 12.0), MacroDataPoint("2017", 11.8), MacroDataPoint("2018", 11.6),
                MacroDataPoint("2019", 11.0), MacroDataPoint("2020", 13.9), MacroDataPoint("2021", 11.1),
                MacroDataPoint("2022", 7.9), MacroDataPoint("2023", 7.4), MacroDataPoint("2024", 6.4),
                MacroDataPoint("2025", 6.6), MacroDataPoint("2026", 6.2)
            )
        )

        return CountryMacroProfile(
            id = "BR",
            name = "Brazil",
            flag = "🇧🇷",
            centralBank = "Banco Central do Brasil",
            currency = "BRL",
            targetInflation = 3.0,
            neutralRateEstimate = 4.75,
            debtToGdp = 78.5,
            tenYearYield = 12.15,
            twoYearYield = 11.85,
            seriesByHorizon = series
        )
    }
}
