package match.insights.controller

import match.insights.model.VideoContent
import match.insights.request.VideoContentRequest
import match.insights.response.H2HDetails
import match.insights.response.HomeAwayTeamLastFive
import match.insights.response.LastFiveMatchesEvents
import match.insights.response.LeagueBasicInfo
import match.insights.response.PlayerSummary
import match.insights.response.TeamDetails
import match.insights.response.TeamFixture
import match.insights.response.TeamPlayer
import match.insights.response.TeamPositionsAndPoints
import match.insights.response.TeamsRestStatus
import match.insights.response.TeamsScorePerformance
import match.insights.service.TeamsService
import match.insights.response.TwoTeamsStatistics
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/teams")
class TeamController(private val teamsService: TeamsService) {

    @GetMapping("/lastfive/{homeTeamId}/{awayTeamId}")
    fun getMatches(@PathVariable homeTeamId: Int, @PathVariable awayTeamId: Int): HomeAwayTeamLastFive =
        teamsService.getLast5MatchesResults(homeTeamId, awayTeamId)

    @GetMapping("/h2h/{homeTeamId}/{awayTeamId}")
    fun getH2H(
        @PathVariable homeTeamId: Int,
        @PathVariable awayTeamId: Int,
        @RequestParam isFull: Boolean? = false
    ): List<H2HDetails> =
        teamsService.getHeadToHead(homeTeamId, awayTeamId, isFull)


    @GetMapping("/season/stats/{homeTeamId}/{awayTeamId}/{leagueId}")
    fun getTwoTeamsStats(
        @PathVariable homeTeamId: Int,
        @PathVariable awayTeamId: Int,
        @PathVariable leagueId: Int
    ): TwoTeamsStatistics = teamsService.getTeamsStats(homeTeamId, awayTeamId, leagueId)


    @GetMapping("/league/stats/{homeTeamId}/{awayTeamId}/{leagueId}")
    fun getTeamsLeagueStats(
        @PathVariable homeTeamId: Int,
        @PathVariable awayTeamId: Int,
        @PathVariable leagueId: Int
    ): TeamPositionsAndPoints = teamsService.getTeamsPositionsAndPoints(homeTeamId, awayTeamId, leagueId)

    @GetMapping("/matches/events/sum/{teamId}")
    fun getLastFiveMatchesEvents(
        @PathVariable teamId: Int,
    ): LastFiveMatchesEvents = teamsService.getLast5MatchesEvents(teamId)

    @GetMapping("/rest/status/{homeTeamId}/{awayTeamId}/{fixtureDate}")
    fun getTeamsRestStatuses(
        @PathVariable homeTeamId: Int,
        @PathVariable awayTeamId: Int,
        @PathVariable fixtureDate: String,
    ): TeamsRestStatus {
        return teamsService.teamRestStatuses(homeTeamId, awayTeamId, fixtureDate.trim())
    }


    @GetMapping("/score/performance/{homeTeamId}/{awayTeamId}/{leagueId}")
    fun getScorePerformances(
        @PathVariable homeTeamId: Int,
        @PathVariable awayTeamId: Int,
        @PathVariable leagueId: Int
    ): TeamsScorePerformance = teamsService.teamsScorePerformance(homeTeamId, awayTeamId, leagueId)

    @GetMapping("/{teamId}/details")
    fun getTeamDetails(
        @PathVariable teamId: Int,
    ): TeamDetails = teamsService.teamDetails(teamId)

    @GetMapping("/{teamId}/players")
    fun getTeamPlayers(
        @PathVariable teamId: Int,
    ): List<PlayerSummary> = teamsService.teamPlayers(teamId)

    @GetMapping("/fixture/{teamId}")
    fun getTeamFixture(
        @PathVariable teamId: Int,
    ): TeamFixture = teamsService.teamFixture(teamId)

    @GetMapping("/squad/{teamId}")
    fun getTeamSquad(
        @PathVariable teamId: Int,
    ): List<TeamPlayer> = teamsService.teamSquad(teamId)

    @GetMapping("/leagues/{teamId}")
    fun getTeamLeagues(
        @PathVariable teamId: Int,
    ): List<LeagueBasicInfo> = teamsService.teamLeagues(teamId)


    @PostMapping("/{teamId}/video")
    @ResponseStatus(HttpStatus.CREATED)
    fun newVideo(@PathVariable teamId: Int, @RequestBody request: VideoContentRequest): Set<VideoContent> {
        request.videos.forEach { it.selfValidate() }
        return teamsService.addVideoContent(teamId, request.videos)
    }
}