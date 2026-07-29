package match.insights.clientData

enum class LiveStatisticType(val apiValue: String) {
    SHOTS_ON_GOAL("Shots on Goal"),
    SHOTS_OFF_GOAL("Shots off Goal"),
    TOTAL_SHOTS("Total Shots"),
    BLOCKED_SHOTS("Blocked Shots"),
    SHOTS_INSIDEBOX("Shots insidebox"),
    SHOTS_OUTSIDEBOX("Shots outsidebox"),
    FOULS("Fouls"),
    CORNER_KICKS("Corner Kicks"),
    OFFSIDES("Offsides"),
    BALL_POSSESSION("Ball Possession"),
    YELLOW_CARDS("Yellow Cards"),
    RED_CARDS("Red Cards"),
    GOALKEEPER_SAVES("Goalkeeper Saves"),
    TOTAL_PASSES("Total passes"),
    PASSES_ACCURATE("Passes accurate"),
    PASSES_PERCENTAGE("Passes %"),
    EXPECTED_GOALS("expected_goals"),
    GOALS_PREVENTED("goals_prevented");

    companion object {
        private val byApiValue = values().associateBy { it.apiValue.lowercase() }

        fun from(apiValue: String): LiveStatisticType? = byApiValue[apiValue.lowercase()]
    }
}

val LiveStatistic.statisticType: LiveStatisticType?
    get() = LiveStatisticType.from(type)
