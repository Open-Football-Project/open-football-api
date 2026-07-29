package org.footballproject.controller


import org.footballproject.model.RankingKey
import org.footballproject.response.LeagueRankingPlayer
import org.footballproject.service.RankingsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ranking")
class RankingController(private val rankingsService: RankingsService) {

    @GetMapping("/league")
    fun leagueRanking(
        @RequestParam key: RankingKey,
        @RequestParam leagueId: Int
    ): List<LeagueRankingPlayer> {
        return rankingsService.leagueRanking(key, leagueId)
    }
}