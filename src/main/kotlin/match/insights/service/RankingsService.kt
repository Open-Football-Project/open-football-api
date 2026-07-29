package match.insights.service

import match.insights.apidata.RankingsData
import match.insights.model.RankingKey
import match.insights.response.LeagueRankingPlayer
import org.springframework.stereotype.Service


@Service
class RankingsService(
    private val apidata: RankingsData,
) {
    fun leagueRanking(rankingKey: RankingKey, leagueId: Int): List<LeagueRankingPlayer> =
        apidata.leagueRanking(rankingKey, leagueId)
            .map { LeagueRankingPlayer.fromClientResponse(it) }
}
