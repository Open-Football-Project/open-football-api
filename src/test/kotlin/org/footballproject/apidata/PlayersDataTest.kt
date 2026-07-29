package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.data.client.ClientPlayerInfo
import org.footballproject.data.client.ClientPlayerTrophies
import org.footballproject.data.client.ClientTransfersData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PlayersDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val underTest = PlayersData(apiSportsClient)

    @Test
    fun `fetch player Trophies`() {

        every { apiSportsClient.fetchPlayerTrophies("/trophies?player=142") } returns ClientPlayerTrophies.trophies

        val result = underTest.trophies(142)

        assertThat(result).isNotEmpty()

        verify { apiSportsClient.fetchPlayerTrophies("/trophies?player=142") }
    }

    @Test
    fun `fetch player Transfers`() {

        every { apiSportsClient.fetchTransfers("/transfers?player=142") } returns ClientTransfersData.transfers

        val result = underTest.transfers(142)

        assertThat(result).isNotEmpty()

        verify { apiSportsClient.fetchTransfers("/transfers?player=142") }
    }


    @Test
    fun `fetch player Info by id`() {

        every { apiSportsClient.fetchPlayerInfo("/players?id=1234&season=${LocalDate.now().year}") } returns listOf(
            ClientPlayerInfo.playerInfoResponse
        )

        val result = underTest.playerInfo(1234)

        assertThat(result?.player).isNotNull
        assertThat(result?.statistics).isNotEmpty

        verify { apiSportsClient.fetchPlayerInfo("/players?id=1234&season=${LocalDate.now().year}") }
    }

    @Test
    fun `returns null when player info is empty for both current and previous year`() {
        val year = LocalDate.now().year
        every { apiSportsClient.fetchPlayerInfo("/players?id=19012&season=$year") } returns emptyList()
        every { apiSportsClient.fetchPlayerInfo("/players?id=19012&season=${year - 1}") } returns emptyList()

        val result = underTest.playerInfo(19012)

        assertThat(result).isNull()
    }

    @Test
    fun `falls back to previous year when current year has no data`() {
        val year = LocalDate.now().year
        every { apiSportsClient.fetchPlayerInfo("/players?id=1234&season=$year") } returns emptyList()
        every { apiSportsClient.fetchPlayerInfo("/players?id=1234&season=${year - 1}") } returns listOf(
            ClientPlayerInfo.playerInfoResponse
        )

        val result = underTest.playerInfo(1234)

        assertThat(result).isNotNull
        assertThat(result?.player).isNotNull

        verify { apiSportsClient.fetchPlayerInfo("/players?id=1234&season=$year") }
        verify { apiSportsClient.fetchPlayerInfo("/players?id=1234&season=${year - 1}") }
    }
}