package match.insights.response

data class PlayerRanking(
    val player: String,
    val team: String,
    val statName: String,
    val value: Int
)

data class Ranking(
    val title: String,
    val players: List<PlayerRanking>
)

data class LeagueRankings(
    val topScorers: Ranking = Ranking("Top Scorers", listOf()),
    val topAssists: Ranking = Ranking("Top Assists", listOf()),
    val topYellowCards: Ranking = Ranking("Top Yellow Cards", listOf()),
    val topRedCards: Ranking = Ranking("Top Red Cards", listOf()),
    val topAppearances: Ranking = Ranking("Top Appearances", listOf()),
    val topPenaltyGoals: Ranking = Ranking("Top Penalty Goals", listOf()),
    val topPenaltiesSaved: Ranking = Ranking("Top Penalties Saved", listOf()),
    val topGoalsConceded: Ranking = Ranking("Top Goals Conceded", listOf()),
    val topSaves: Ranking = Ranking("Top Saves", listOf()),
    val topPenaltiesWon: Ranking = Ranking("Top Penalties Won", listOf()),
    val topPenaltiesMissed: Ranking = Ranking("Top Penalties Missed", listOf())
)