package match.insights.response

data class Bet(
    val betName: String,
    val values: List<SingleOdd>
)

data class SingleOdd(
    val label: String,
    val odd: Double
)
