package match.insights.clientData

import java.io.Serializable

data class RankingPlayerStats(
    val player: RankingPlayer,
    val statistics: List<RankingPlayerStatistic>
) : Serializable

data class RankingPlayer(
    val id: Int?,
    val name: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val age: Int? = null,
    val photo: String? = null
) : Serializable

data class RankingPlayerStatistic(
    val team: RankingTeam,
    val goals: RankingGoals? = null,
    val cards: RankingCards? = null,
    val games: RankingGames? = null
) : Serializable

data class RankingTeam(
    val id: Int,
    val name: String,
    val logo: String? = null
) : Serializable

data class RankingGoals(
    val total: Int? = null,
    val assists: Int? = null
) : Serializable

data class RankingCards(
    val yellow: Int? = null,
    val red: Int? = null
) : Serializable

data class RankingGames(
    val appearences: Int? = null
) : Serializable