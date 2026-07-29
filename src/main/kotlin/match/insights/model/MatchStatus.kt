package match.insights.model

enum class MatchStatus(val code: String) {
    NOT_STARTED("NS"),
    TIME_TBD("TIME"),
    FIRST_HALF("1H"),
    HALF_TIME("HT"),
    SECOND_HALF("2H"),
    EXTRA_TIME("ET"),
    PENALTIES("P"),
    BREAK_TIME("BT"),
    LIVE("LIVE"),
    INTERRUPTED("INT"),

    FULL_TIME("FT"),
    AFTER_EXTRA_TIME("AET"),
    AFTER_PENALTIES("PEN"),
    CANCELLED("CANC"),
    POSTPONED("PST"),
    ABANDONED("ABD"),
    AWARDED("AWD"),
    WALKOVER("WO"),
    SUSPENDED("SUSP");

    fun isNow() =
        this in setOf(
            LIVE,
            FIRST_HALF,
            HALF_TIME,
            SECOND_HALF,
            EXTRA_TIME,
            PENALTIES,
            BREAK_TIME,
            INTERRUPTED
        )

    fun isValidLiveChartStatus() =
        this in setOf(
            LIVE,
            FIRST_HALF,
            SECOND_HALF,
            EXTRA_TIME
    )

    fun isVoided() =
        this in setOf(
            CANCELLED,
            POSTPONED,
            ABANDONED,
            WALKOVER,
            AWARDED
        )
}
