package org.footballproject.response

data class TeamFixture(
    val previous: List<TeamFixtureMatch>,
    val upcoming: List<TeamFixtureMatch>,
)


data class TeamFixtureMatch(
    val fixtureId: Int,
    val date: String,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeTeamLogo: String?,
    val awayTeamLogo: String?,
    val isFinished: Boolean,
    val homeTeamScore: Int?,
    val awayTeamScore: Int?,
    val statusShort: String,
    val statusLong: String,
    val isLiveNow: Boolean
)