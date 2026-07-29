package org.footballproject.controller

import org.footballproject.service.PlayerGameService
import org.footballproject.service.TeamGameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/game")
class GameController(
    private val playerGameService: PlayerGameService,
    private val teamGameService: TeamGameService
) {

    @GetMapping("/{teamId}/player")
    fun guessThePlayer(@PathVariable teamId: Int) = playerGameService.generatePlayerGame(teamId)


    @GetMapping("/{leagueId}/team")
    fun guessTheTeam(@PathVariable leagueId: Int) = teamGameService.newTeamGame(leagueId)

}