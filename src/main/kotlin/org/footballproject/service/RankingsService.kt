package org.footballproject.service

import org.footballproject.apidata.RankingsData
import org.footballproject.model.RankingKey
import org.footballproject.response.LeagueRankingPlayer
import org.springframework.stereotype.Service


@Service
class RankingsService(
    private val apidata: RankingsData,
) {
    fun leagueRanking(rankingKey: RankingKey, leagueId: Int): List<LeagueRankingPlayer> =
        apidata.leagueRanking(rankingKey, leagueId)
            .map { LeagueRankingPlayer.fromClientResponse(it) }
}
