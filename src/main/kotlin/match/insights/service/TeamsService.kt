package match.insights.service

import match.insights.apidata.TeamData
import match.insights.apidata.LeaguesData
import match.insights.apidata.LiveData
import match.insights.apidata.MatchesData
import match.insights.clientData.CoachResponse
import match.insights.clientData.TeamResponse
import match.insights.datamanipulation.DataManipulation
import match.insights.datamanipulation.EventsDataManipulation
import match.insights.datamanipulation.FixturesManipulation
import match.insights.datamanipulation.LeagueDataManipulation
import match.insights.datamanipulation.PerformanceDataManipulation
import match.insights.datamanipulation.TeamSquadManipulation
import match.insights.datamanipulation.TeamStatisticsManipulation
import match.insights.errors.ApiFailedException
import match.insights.errors.ErrorMessage
import match.insights.internaldata.VideoContentManager
import match.insights.model.ContentKey
import match.insights.model.VideoContent
import match.insights.response.H2HDetails
import match.insights.response.HomeAwayTeamLastFive
import match.insights.response.LeagueBasicInfo
import match.insights.response.PlayerSummary
import match.insights.response.TeamDetails
import match.insights.response.TeamFixture
import match.insights.response.TeamPlayer
import match.insights.response.TeamPositionsAndPoints
import match.insights.response.TeamStats
import match.insights.response.TeamsRestStatus
import match.insights.response.TeamsScorePerformance
import match.insights.response.TwoTeamsStatistics
import match.insights.response.isEmpty
import org.springframework.stereotype.Service
import java.time.Year

import kotlin.collections.map

@Service
class TeamsService(
    private val apiData: TeamData,
    private val matchesData: MatchesData,
    private val leaguesData: LeaguesData,
    private val liveData: LiveData,
    private val dataManipulation: DataManipulation,
    private val eventsDataManipulation: EventsDataManipulation,
    private val performanceDataManipulation: PerformanceDataManipulation,
    private val teamSquadManipulation: TeamSquadManipulation,
    private val leagueDataManipulation: LeagueDataManipulation,
    private val fixturesManipulation: FixturesManipulation,
    private val teamStatisticsManipulation: TeamStatisticsManipulation,
    private val videoContentManager: VideoContentManager
) {

    fun getLast5MatchesResults(homeTeamId: Int, awayTeamId: Int): HomeAwayTeamLastFive {
        val data = matchesData.lastFiveMatchesResults(homeTeamId, awayTeamId)

        return HomeAwayTeamLastFive(
            homeTeamLastFive = dataManipulation.lastFiveResults(homeTeamId, data[homeTeamId] ?: emptyList()),
            awayTeamLastFive = dataManipulation.lastFiveResults(awayTeamId, data[awayTeamId] ?: emptyList())
        )
    }

    fun getHeadToHead(homeTeamId: Int, awayTeamId: Int, isFull: Boolean? = false): List<H2HDetails> = matchesData
        .headToHead(homeTeamId, awayTeamId)
        .let {
            if (isFull == true) it
            else it.take(10)
        }
        .map { H2HDetails.fromResponseData(it) }


    fun getTeamsStats(homeTeamId: Int, awayTeamId: Int, leagueId: Int): TwoTeamsStatistics {
        val data = apiData.getTwoTeamsStats(homeTeamId, awayTeamId, leagueId)
        return teamStatisticsManipulation.twoTeamsStats(data["hometeamstats"], data["awayteamstats"])
            .let {
                if (it.isEmpty()) throw ApiFailedException(ErrorMessage.EMPTY_STATS)
                else it
            }
    }

    fun getTeamStats(teamId: Int, leagueId: Int): TeamStats =
        teamStatisticsManipulation.teamStats(apiData.teamStats(teamId = teamId, leagueId = leagueId))


    fun teamLeagues(teamId: Int): List<LeagueBasicInfo> =
        apiData.teamLeagues(teamId = teamId, Year.now().value)
            .map { participation ->
                LeagueBasicInfo(
                    participation.league.id,
                    participation.league.name,
                    participation.league.type,
                    participation.league.logo
                )
            }


    fun getTeamsPositionsAndPoints(homeTeamId: Int, awayTeamId: Int, leagueId: Int): TeamPositionsAndPoints =
        leagueDataManipulation.positionAndPoints(homeTeamId, awayTeamId, leaguesData.leagueStandings(leagueId))


    fun getLast5MatchesEvents(teamId: Int) =
        eventsDataManipulation.fiveMachesEventsSum(matchesData.lastFiveMatchesEvents(teamId))

    fun teamRestStatuses(homeTeamId: Int, awayTeamId: Int, fixtureDate: String): TeamsRestStatus {
        val matches = matchesData.mostRecentPlayedMatches(homeTeamId, awayTeamId)

        return TeamsRestStatus(
            dataManipulation.teamRestStatus(
                dataManipulation.daysBetween(
                    matches[homeTeamId]?.fixture?.date,
                    fixtureDate
                ) ?: -1
            ),
            dataManipulation.teamRestStatus(
                dataManipulation.daysBetween(
                    matches[awayTeamId]?.fixture?.date,
                    fixtureDate
                ) ?: -1
            )
        )
    }

    fun teamsScorePerformance(homeTeamId: Int, awayTeamId: Int, leagueId: Int): TeamsScorePerformance {
        val matches = matchesData.getTeamsLeagueMatches(homeTeamId, awayTeamId, leagueId)

        return TeamsScorePerformance(
            performanceDataManipulation.calculateScorePerformance(homeTeamId, matches[homeTeamId] ?: emptyList()),
            performanceDataManipulation.calculateScorePerformance(homeTeamId, matches[awayTeamId] ?: emptyList())
        )
    }

    fun teamDetails(teamId: Int): TeamDetails {
        return apiData.getTeamsDetails(teamId).let {
            TeamDetails.fromClientResponse(
                it["coach"] as CoachResponse?,
                it["details"] as TeamResponse,
                videoContentManager.getVideos(ContentKey.TEAM, teamId)
            )
        }
    }

    fun addVideoContent(teamId: Int, videos: Set<VideoContent>): Set<VideoContent> =
        videoContentManager.newContent(ContentKey.TEAM, teamId, videos)

    fun teamPlayers(teamId: Int): List<PlayerSummary> =
        teamSquadManipulation.teamSquadSummary(apiData.teamSquad(teamId))


    fun teamFixture(teamId: Int): TeamFixture {
        val liveMatchesIds = liveData.allLiveMatches().map { it.fixture.id }.toSet()
        return fixturesManipulation.extractTeamFixture(matchesData.previousAndUpcomingMatches(teamId), liveMatchesIds)
    }

    fun teamSquad(teamId: Int): List<TeamPlayer> =
        apiData.currentTeamSquad(teamId).map { TeamPlayer.fromSquadPlayer(it) }
}

