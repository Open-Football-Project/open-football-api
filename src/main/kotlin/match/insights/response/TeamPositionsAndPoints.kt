package match.insights.response

data class TeamPositionsAndPoints(
    val homeTeam: List<PositionAndPoints>,
    val awayTeam: List<PositionAndPoints>
)

data class PositionAndPoints(
    val position: Int? = -1,
    val points: Int? = -1,
    val description: String? = ""
)