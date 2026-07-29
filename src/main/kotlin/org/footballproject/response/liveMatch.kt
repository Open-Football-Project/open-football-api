package org.footballproject.response

import org.footballproject.clientData.Event
import org.footballproject.clientData.LiveFixtureResponse
import org.footballproject.model.Poll

data class LiveMatchesResponse(
    val country: String,
    val leagueMatches: List<LiveLeagueMatches>,
) {
    companion object {
        fun fromLiveFixtureResponses(
            country: String,
            matches: List<LiveLeagueMatches>
        ) = LiveMatchesResponse(
            country = country,
            leagueMatches = matches
        )
    }
}

data class LiveLeagueMatches(
    val leagueId: Int,
    val leagueName: String,
    val leagueCountry: String? = null,
    val leagueLogo: String? = null,
    val matches: List<LiveMatch>
) {
    companion object {
        fun fromLiveData(
            liveMatches: List<LiveMatchInfo>,
            formFor: (homeId: Int, awayId: Int) -> LiveHomeAwayForm
        ): LiveLeagueMatches {
            val league = liveMatches.firstOrNull()?.response?.league
            return LiveLeagueMatches(
                leagueId = league?.id ?: -1,
                leagueName = league?.name ?: "Unknown League",
                leagueCountry = league?.country,
                leagueLogo = league?.logo,
                matches = liveMatches.map { info ->
                    val homeId = info.response.teams.home?.id ?: -1
                    val awayId = info.response.teams.away?.id ?: -1
                    LiveMatch.fromMatchInfo(info, formFor(homeId, awayId))
                }
            )
        }
    }
}

data class LiveMatchInfo(
    val response: LiveFixtureResponse,
    val polls: List<Poll>
)

data class LiveHomeAwayForm(
    val homeForm: List<String>,
    val awayForm: List<String>,
    val isHomeHot: Boolean,
    val isHomeCold: Boolean,
    val isAwayHot: Boolean,
    val isAwayCold: Boolean,
    val isDisplayable: Boolean
)

data class LiveMatch(
    val id: Int,
    val homeTeamLogo: String?,
    val awayTeamLogo: String?,
    val homeTeamName: String,
    val homeTeamId: Int,
    val awayTeamName: String,
    val awayTeamId: Int,
    val homeTeamScore: Int,
    val awayTeamScore: Int,
    val elapsedTime: Int?,
    val extraTime: Int?,
    val leagueName: String,
    val leagueId: Int,
    val matchDate: String,
    val statusShort: String,
    val statusLong: String,
    val events: List<LiveEvent>,
    val polls: List<Poll>,
    val homeAwayForm: LiveHomeAwayForm
) {
    companion object {
        fun fromMatchInfo(
            info: LiveMatchInfo,
            homeAwayForm: LiveHomeAwayForm
        ) = LiveMatch(
            info.response.fixture.id,
            info.response.teams.home?.logo,
            info.response.teams.away?.logo,
            info.response.teams.home?.name ?: "Unknown",
            info.response.teams.home?.id ?: -1,
            info.response.teams.away?.name ?: "Unknown",
            info.response.teams.away?.id ?: -1,
            info.response.goals.home ?: 0,
            info.response.goals.away ?: 0,
            info.response.fixture.status?.elapsed,
            info.response.fixture.status?.extra,
            info.response.league.name,
            info.response.league.id,
            info.response.fixture.date,
            info.response.fixture.status?.short ?: "Unknown",
            info.response.fixture.status?.long ?: "Unknown",
            info.response.events.map { LiveEvent.fromEvent(it) },
            polls = info.polls,
            homeAwayForm = homeAwayForm
        )
    }
}


data class LiveEvent(
    val timeElapsed: Int,
    val timeExtra: Int?,
    val teamName: String,
    val teamLogo: String?,
    val playerName: String?,
    val eventType: String,
    val eventDetails: String
) {
    companion object {
        fun fromEvent(event: Event) = LiveEvent(
            timeElapsed = event.time.elapsed,
            timeExtra = event.time.extra,
            teamName = event.team.name,
            teamLogo = event.team.logo,
            playerName = event.player?.name,
            eventType = event.type,
            eventDetails = event.detail.orEmpty()
        )
    }
}