package org.footballproject.live

import org.footballproject.service.TodayPlayersService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TodayPlayersScheduler(
    private val todayPlayersService: TodayPlayersService
) {

    @Scheduled(cron = "0 0 0,12 * * *")
    fun captureTodayPlayers() {
        todayPlayersService.captureTodayPlayers()
    }
}
