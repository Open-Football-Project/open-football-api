package org.footballproject.response

data class ValueBetOutcome(val label: String, val odd: Double, val isValue: Boolean)

data class BookmakerLine(val name: String, val outcomes: List<ValueBetOutcome>)

data class FairOdd(val label: String, val odd: Double)

data class ValueBetMarket(
    val betName: String,
    val fairOdds: List<FairOdd>,
    val bookmakers: List<BookmakerLine>
)

data class ValueBetsResponse(val markets: List<ValueBetMarket>)
