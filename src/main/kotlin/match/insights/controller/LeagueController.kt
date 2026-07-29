package match.insights.controller

import match.insights.model.VideoContent
import match.insights.request.VideoContentRequest
import match.insights.response.ArgSpecial
import match.insights.response.BasicTeamInfo
import match.insights.response.LeagueFixture
import match.insights.response.LeagueInfo
import match.insights.response.LeagueRankings
import match.insights.response.LeaguesGroups
import match.insights.service.ArgentinaService
import match.insights.service.LeagueService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/league")
class LeagueController(private val leagueService: LeagueService, private val argentinaService: ArgentinaService) {

    @GetMapping("/standing/{leagueId}")
    fun leagueStandings(@PathVariable leagueId: Int): LeagueInfo =
        leagueService.leagueInfo(leagueId)


    @GetMapping("/all")
    fun leagues(): LeaguesGroups = leagueService.allLeagues()

    @GetMapping("/fixture/{leagueId}")
    fun leagues(@PathVariable leagueId: Int): LeagueFixture = leagueService.leagueSeasonFixture(leagueId)


    @GetMapping("/rankings/{leagueId}")
    suspend fun leagueRankings(@PathVariable leagueId: Int): LeagueRankings = leagueService.leagueRankings(leagueId)

    @GetMapping("/teams/{leagueId}")
    suspend fun leagueTeams(@PathVariable leagueId: Int): List<BasicTeamInfo> = leagueService.currentYearTeams(leagueId)


    @GetMapping("/arg/special")
    fun argSpecial(): ArgSpecial = argentinaService.argSpecialTables()

    @PostMapping("/{leagueId}/video")
    @ResponseStatus(HttpStatus.CREATED)
    fun newVideo(@PathVariable leagueId: Int, @RequestBody request: VideoContentRequest): Set<VideoContent> {
        request.videos.forEach { it.selfValidate() }
        return leagueService.addVideoContent(leagueId, request.videos)
    }
}