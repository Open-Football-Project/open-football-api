package org.footballproject.datamanipulation


import org.footballproject.clientData.MatchResponse
import org.footballproject.model.MatchStatus
import org.footballproject.response.LeagueFixture
import org.footballproject.response.LeagueFixtureDay
import org.footballproject.response.LeagueFixtureRound
import org.footballproject.response.LeagueFixturesMatch
import org.footballproject.response.TeamFixture
import org.footballproject.response.TeamFixtureMatch
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Component
class FixturesManipulation {
    fun extractTeamFixture(fixtures: Map<String, List<MatchResponse>>, liveFixtureIds: Set<Int>): TeamFixture {

        return TeamFixture(
            previous = fixtures["previous"]?.map { teamFixtureMapping(it, liveFixtureIds) } ?: listOf(),
            upcoming = fixtures["upcoming"]?.map { teamFixtureMapping(it, liveFixtureIds) } ?: listOf(),
        )
    }


    private fun teamFixtureMapping(match: MatchResponse, liveFixtureIds: Set<Int>): TeamFixtureMatch {
        return TeamFixtureMatch(
            fixtureId = match.fixture.id,
            homeTeamId = match.teams.home?.id ?: -1,
            awayTeamId = match.teams.away?.id ?: -1,
            homeTeamName = match.teams.home?.name ?: "Unknown",
            awayTeamName = match.teams.away?.name ?: "Unknown",
            homeTeamLogo = match.teams.home?.logo,
            awayTeamLogo = match.teams.away?.logo,
            date = match.fixture.date,
            isFinished = isAFinishedMatch(match.fixture.status?.short),
            homeTeamScore = match.score?.fulltime?.home,
            awayTeamScore = match.score?.fulltime?.away,
            statusShort = match.fixture.status?.short ?: "UN",
            statusLong = match.fixture.status?.long ?: "Unknown",
            isLiveNow = liveFixtureIds.contains(match.fixture.id)
        )
    }

    fun groupByRound(matches: List<MatchResponse>): Map<String, List<MatchResponse>> {
        return matches.groupBy { matchRound(it) }
    }


    fun groupByDate(matches: List<MatchResponse>): Map<String, List<MatchResponse>> {
        return matches.groupBy { it.fixture.date.substring(0, 10) }
    }

    fun toLeagueFixtureDays(matches: List<MatchResponse>, liveFixtureIds: Set<Int>): List<LeagueFixtureDay> {
        return groupByDate(matches).map { (date, dailyMatches) ->
            LeagueFixtureDay(
                date = date,
                matches = dailyMatches.map { toLeagueFixturesMatch(it, liveFixtureIds) }
            )
        }.sortedBy { it.date }
    }

    fun toLeagueFixtureRounds(matches: List<MatchResponse>, liveFixtureIds: Set<Int>): List<LeagueFixtureRound> {
        return groupByRound(matches).map { (roundName, roundMatches) ->
            LeagueFixtureRound(
                name = roundName,
                days = toLeagueFixtureDays(roundMatches, liveFixtureIds)
            )
        }.sortedBy {
            it.days.minOfOrNull { LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE) }
                ?: LocalDate.MAX
        }
    }

    fun toLeagueFixture(matches: List<MatchResponse>, liveFixtureIds: Set<Int>): LeagueFixture {
        val rounds = toLeagueFixtureRounds(matches, liveFixtureIds)
        val today = LocalDate.now()

        val currentRoundIndex = rounds.indexOfFirst { round ->
            val earliestDate = round.days.minOfOrNull { LocalDate.parse(it.date) }
            earliestDate != null && !earliestDate.isBefore(today)
        }.takeIf { it != -1 } ?: (rounds.size - 1)

        return LeagueFixture(
            rounds = rounds,
            currentRoundIndex = currentRoundIndex,
            totalRounds = rounds.size
        )
    }


    private fun toLeagueFixturesMatch(match: MatchResponse, liveFixtureIds: Set<Int>): LeagueFixturesMatch {
        return LeagueFixturesMatch(
            fixtureId = match.fixture.id,
            homeTeamId = match.teams.home?.id ?: -1,
            awayTeamId = match.teams.away?.id ?: -1,
            homeTeamName = match.teams.home?.name ?: "Unknown",
            awayTeamName = match.teams.away?.name ?: "Unknown",
            homeTeamLogo = match.teams.home?.logo,
            awayTeamLogo = match.teams.away?.logo,
            date = match.fixture.date,
            homeTeamScore = match.goals?.home,
            awayTeamScore = match.goals?.away,
            isFinished = isAFinishedMatch(match.fixture.status?.short),
            fixtureRound = matchRound(match),
            statusShort = match.fixture.status?.short ?: "UN",
            statusLong = match.fixture.status?.long ?: "Unknown",
            isLiveNow = liveFixtureIds.contains(match.fixture.id)
        )
    }


    private fun matchRound(match: MatchResponse): String {

        if (match.fixture.round != null && match.fixture.round != "Unknown Round" && match.fixture.round != "Unknown")
            return match.fixture.round

        if (match.league.round != null && match.league.round != "Unknown Round" && match.league.round != "Unknown")
            return match.league.round

        return "Unknown Round"

    }

    private fun isAFinishedMatch(short: String?) = setOf(
        MatchStatus.FULL_TIME.code,
        MatchStatus.AFTER_EXTRA_TIME.code,
        MatchStatus.AFTER_PENALTIES.code,
        MatchStatus.AWARDED.code
    ).contains(short)
}