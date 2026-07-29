package match.insights.response

data class LeagueFixturesMatch(
    val fixtureId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeTeamLogo: String? = null,
    val awayTeamLogo: String? = null,
    val date: String,
    val homeTeamScore: Int? = null,
    val awayTeamScore: Int? = null,
    val isFinished: Boolean,
    val fixtureRound: String,
    val statusShort: String,
    val statusLong: String,
    val isLiveNow: Boolean
)

data class LeagueFixtureDay(
    val date: String,
    val matches: List<LeagueFixturesMatch>
)

data class LeagueFixtureRound(
    val name: String,
    val days: List<LeagueFixtureDay>
)

data class LeagueFixture(
    val rounds: List<LeagueFixtureRound>,
    val currentRoundIndex: Int,
    val totalRounds: Int
)
