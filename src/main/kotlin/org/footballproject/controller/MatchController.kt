package org.footballproject.controller

import org.footballproject.model.VideoContent
import org.footballproject.response.DayMatchesResponse
import org.footballproject.response.MatchDetails
import org.footballproject.response.MatchEvent
import org.footballproject.response.TeamsLineups
import org.footballproject.response.TwoTeamsStatistics
import org.footballproject.request.VideoContentRequest
import org.footballproject.service.MatchService
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
@RequestMapping("/api/matches")
class MatchController(private val matchService: MatchService) {

    @GetMapping()
    fun getMatches(
        @RequestParam date: String,
    ): List<DayMatchesResponse> =
        matchService.getMatchesByDate(date)

    @GetMapping("/{matchId}/details")
    fun getMatches(@PathVariable matchId: Int): MatchDetails =
        matchService.getMatchDetails(matchId)


    @GetMapping("/stats/{fixtureId}")
    fun fixtureStats(@PathVariable fixtureId: Int): TwoTeamsStatistics {
        return matchService.matchStats(fixtureId)
    }

    @GetMapping("/lineups/{fixtureId}")
    fun fixtureLineups(@PathVariable fixtureId: Int): TeamsLineups {
        return matchService.matchLineups(fixtureId)
    }

    @GetMapping("/events/{fixtureId}")
    fun fixtureEvents(@PathVariable fixtureId: Int): List<MatchEvent> {
        return matchService.matchEvents(fixtureId)
    }

    @PostMapping("/{matchId}/video")
    @ResponseStatus(HttpStatus.CREATED)
    fun newVideo(
        @PathVariable matchId: Int,
        @RequestBody request: VideoContentRequest
    ): Set<VideoContent> {
        request.videos.forEach { it.selfValidate() }
        return matchService.addVideoContent(matchId, request.videos)
    }
}