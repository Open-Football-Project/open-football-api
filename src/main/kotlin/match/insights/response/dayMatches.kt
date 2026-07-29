package match.insights.response

import match.insights.clientData.MatchResponse
import match.insights.model.MatchStatus


data class DayMatchesResponse(
    val country: String,
    val matchesByLeague: List<DayMatches>
) {
    companion object {
        fun fromCountryMatches(
            country: String,
            countryMatches: List<DayMatches>
        ) = DayMatchesResponse(
            country,
            countryMatches
        )
    }
}

data class DayMatches(
    val leagueId: Int,
    val leagueName: String,
    val leagueLogo: String? = null,
    val matches: List<OnDayMatch>
) {
    companion object {
        fun fromMatchResponses(
            responses: List<MatchResponse>,
            liveIds: Set<Int>
        ) = DayMatches(
            leagueId = responses.firstOrNull()?.league?.id ?: -1,
            leagueName = responses.firstOrNull()?.league?.name ?: "Unknown League",
            leagueLogo = responses.firstOrNull()?.league?.logo,
            responses.map { OnDayMatch.fromMatchResponse(it, liveIds) }
        )
    }
}

data class OnDayMatch(
    val fixtureId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeTeamLogo: String? = null,
    val awayTeamLogo: String? = null,
    val homeTeamScore: Int? = null,
    val awayTeamScore: Int? = null,
    val isFinished: Boolean,
    val date: String,
    val statusShort: String,
    val statusLong: String,
    val isLiveNow: Boolean
) {
    companion object {
        fun fromMatchResponse(
            response: MatchResponse,
            liveIds: Set<Int>
        ) = OnDayMatch(
            response.fixture.id,
            response.teams.home?.id ?: -1,
            response.teams.away?.id ?: -1,
            response.teams.home?.name ?: "Unknown",
            response.teams.away?.name ?: "Unknown",
            response.teams.home?.logo,
            response.teams.away?.logo,
            isFinished = isAFinishedMatch(response.fixture.status?.short),
            homeTeamScore = response.score?.fulltime?.home,
            awayTeamScore = response.score?.fulltime?.away,
            date = response.fixture.date,
            statusShort = response.fixture.status?.short ?: "UN",
            statusLong = response.fixture.status?.long ?: "Unknown",
            isLiveNow = liveIds.contains(response.fixture.id)
        )

        private fun isAFinishedMatch(short: String?) = setOf(
            MatchStatus.FULL_TIME.code,
            MatchStatus.AFTER_EXTRA_TIME.code,
            MatchStatus.AFTER_PENALTIES.code,
            MatchStatus.AWARDED.code
        ).contains(short)
    }
}


