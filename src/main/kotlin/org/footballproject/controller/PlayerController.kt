package org.footballproject.controller

import org.footballproject.model.VideoContent
import org.footballproject.request.VideoContentRequest
import org.footballproject.response.FixtureTodayPlayers
import org.footballproject.response.PlayerHistory
import org.footballproject.response.PlayerMainInfo
import org.footballproject.service.PlayerService
import org.footballproject.service.TodayPlayersService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/player")
class PlayerController(
    private val playerService: PlayerService,
    private val todayPlayersService: TodayPlayersService
) {

    @GetMapping("/history/{playerId}")
    fun playerHistoryProfile(@PathVariable playerId: Int): PlayerHistory =
        playerService.playerHistory(playerId)

    @GetMapping("/{playerId}")
    fun playerProfile(@PathVariable playerId: Int): PlayerMainInfo =
        playerService.fullPlayerInfo(playerId)

    @PostMapping("/{playerId}/video")
    @ResponseStatus(HttpStatus.CREATED)
    fun newVideo(@PathVariable playerId: Int, @RequestBody request: VideoContentRequest): Set<VideoContent> {
        request.videos.forEach { it.selfValidate() }
        return playerService.addVideoContent(playerId, request.videos)
    }

    @GetMapping("/today-players/leagues")
    fun todayPlayersTrackedLeagues(): List<Int> = todayPlayersService.trackedLeagueIds()

    @GetMapping("/today-players/fixtures")
    fun todayPlayersAvailableFixtureIds(): List<Int> = todayPlayersService.availableFixtureIds()

    @GetMapping("/today-players")
    fun todayPlayersFixtures(): List<FixtureTodayPlayers> = todayPlayersService.allFixtures()
}