package match.insights.live

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import match.insights.service.TodayPlayersService
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
