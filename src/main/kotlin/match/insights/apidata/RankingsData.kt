package match.insights.apidata

import match.insights.client.ApiSportsClient
import match.insights.model.RankingKey
import match.insights.seasons.Seasons
import org.springframework.stereotype.Component


@Component
class RankingsData(
    private val apiSportsClient: ApiSportsClient,
    private val seasons: Seasons,

    ) {

    fun leagueRanking(rankingKey: RankingKey, leagueId: Int) =
        when (rankingKey) {
            RankingKey.RED_CARD -> apiSportsClient.fetchLeagueRanking(
                "/players/topredcards?league=${leagueId}&season=${
                    seasons.leagueCurrentSeason(
                        leagueId
                    )
                }"
            )

            RankingKey.YELLOW_CARD -> apiSportsClient.fetchLeagueRanking(
                "/players/topyellowcards?league=${leagueId}&season=${
                    seasons.leagueCurrentSeason(
                        leagueId
                    )
                }"
            )

            RankingKey.ASSISTS -> apiSportsClient.fetchLeagueRanking(
                "/players/topassists?league=${leagueId}&season=${
                    seasons.leagueCurrentSeason(
                        leagueId
                    )
                }"
            )

            else -> apiSportsClient.fetchLeagueRanking(
                "/players/topscorers?league=${leagueId}&season=${
                    seasons.leagueCurrentSeason(
                        leagueId
                    )
                }"
            )
        }
}