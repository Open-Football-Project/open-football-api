package org.footballproject.live

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.service.TodayPlayersService
import org.junit.jupiter.api.Test

class TodayPlayersSchedulerTest {

    private val todayPlayersService: TodayPlayersService = mockk()
    private val underTest = TodayPlayersScheduler(todayPlayersService)

    @Test
    fun `delegates each scheduled run to the service`() {
        every { todayPlayersService.captureTodayPlayers() } just Runs

        underTest.captureTodayPlayers()

        verify { todayPlayersService.captureTodayPlayers() }
    }
}
