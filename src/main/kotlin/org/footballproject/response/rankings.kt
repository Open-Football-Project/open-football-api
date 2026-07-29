package org.footballproject.response

import org.footballproject.clientData.RankingPlayerStats

data class LeagueRankingPlayer(
    val playerId: Int,
    val playerName: String,
    val playerPhoto: String,
    val playerAge: Int,
    val playerTeamId: Int,
    val playerTeamLogo: String,
    val playerTeamName: String,
    val playerTotalGoals: Int,
    val playerTotalAssists: Int,
    val playerTotalYellowCards: Int,
    val playerTotalRedCards: Int,
    val playerTotalAppearances: Int

) {
    companion object {

        fun fromClientResponse(info: RankingPlayerStats): LeagueRankingPlayer = if (
            info.statistics.isEmpty()
        ) LeagueRankingPlayer(
            info.player.id ?: -1,
            info.player.name,
            info.player.photo ?: "",
            info.player.age ?: -1,
            0, "", "", 0, 0, 0, 0, 0
        )
        else LeagueRankingPlayer(
            info.player.id ?: -1,
            info.player.name,
            info.player.photo ?: "",
            info.player.age ?: -1,
            info.statistics[0].team.id,
            info.statistics[0].team.logo ?: "",
            info.statistics[0].team.name,
            info.statistics[0].goals?.total ?: 0,
            info.statistics[0].goals?.assists ?: 0,
            info.statistics[0].cards?.yellow ?: 0,
            info.statistics[0].cards?.red ?: 0,
            info.statistics[0].games?.appearences ?: 0

        )
    }
}
