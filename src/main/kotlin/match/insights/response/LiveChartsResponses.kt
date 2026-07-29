package match.insights.response

import match.insights.model.LiveChartPoint

data class FixtureChartsResponse(
    val fixtureId: Int,
    val homeTeamName: String,
    val awayTeamName: String,
    val indicators: Map<String, List<LiveChartPoint>>
)

data class LiveChartableMatch(
    val fixtureId: Int,
    val homeTeamName: String,
    val awayTeamName: String
)
