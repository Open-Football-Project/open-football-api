package org.footballproject.response

import org.footballproject.model.LiveChartPoint

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
