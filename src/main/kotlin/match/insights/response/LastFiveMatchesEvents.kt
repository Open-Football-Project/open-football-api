package match.insights.response

data class LastFiveMatchesEvents(
    val penalties: Int,
    val firstHalfGoals: Int,
    val secondHalfGoals: Int,
    val extraTimeGoals: Int,

    val firstHalfYellowCards: Int,
    val secondHalfYellowCards: Int,
    val extraTimeYellowCards: Int,

    val firstHalfRedCards: Int,
    val secondHalfRedCards: Int,
    val extraTimeRedCards: Int
)