package org.footballproject.response

import org.footballproject.model.PlayerPosition
import org.footballproject.model.PlayerScore
import org.footballproject.model.TodayPlayersWatchlist

data class FixtureTodayPlayers(
    val fixtureId: Int,
    val leagueId: Int,
    val leagueName: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val home: Map<PlayerPosition, List<PlayerScore>>,
    val away: Map<PlayerPosition, List<PlayerScore>>
) {
    companion object {
        fun fromWatchlist(watchlist: TodayPlayersWatchlist) = FixtureTodayPlayers(
            fixtureId = watchlist.fixtureId,
            leagueId = watchlist.leagueId,
            leagueName = watchlist.leagueName,
            homeTeamName = watchlist.homeTeamName,
            awayTeamName = watchlist.awayTeamName,
            home = watchlist.home,
            away = watchlist.away
        )
    }
}
