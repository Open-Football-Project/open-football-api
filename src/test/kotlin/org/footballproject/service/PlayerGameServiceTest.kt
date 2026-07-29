package org.footballproject.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.TeamData
import org.footballproject.apidata.PlayersData
import org.footballproject.data.client.ClientPlayerTrophies
import org.footballproject.data.client.ClientTeamDetails
import org.footballproject.data.client.ClientTransfersData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlayerGameServiceTest {
    val teamData: TeamData = mockk()
    val playersData: PlayersData = mockk()
    val underTest = PlayerGameService(teamData, playersData)

    @Test
    fun shouldGetThePlayersByTeamId() {
        every { teamData.teamSquad(1243, any()) } returns mapOf(1 to ClientTeamDetails.mockPlayersResponse)

        val result = underTest.getPlayers(1243)

        assertThat(result).isNotEmpty
        verify(exactly = 2) { teamData.teamSquad(1243, any()) }
    }

    @Test
    fun shouldGetAPlayersEmptyMap() {
        every { teamData.teamSquad(1243, any()) } returns mapOf(1 to emptyList())

        val result = underTest.getPlayers(1243)

        assertThat(result).isEmpty()

        verify(exactly = 2) { teamData.teamSquad(1243, any()) }
    }


    @Test
    fun shouldCheckIfItIsAnInterestingPlayer() {
        assertThat(underTest.isAnInterestingPlayer(ClientTeamDetails.mockPlayersResponse[0])).isTrue
    }


    @Test
    fun shouldCreateInvalidGameOptions() {
        val selectedPlayer = ClientTeamDetails.mockPlayersResponse[0].player.name

        val result = underTest.playerInvalidOptions(
            mapOf(1 to ClientTeamDetails.mockPlayersResponse),
            selectedPlayer
        )
        assertThat(result.contains(selectedPlayer)).isFalse
    }

    @Test
    fun shouldCreateGameHints() {
        val selectedPlayer = ClientTeamDetails.mockPlayersResponse[0].player.name

        val result = underTest.playerInvalidOptions(
            mapOf(1 to ClientTeamDetails.mockPlayersResponse),
            selectedPlayer
        )
        assertThat(result.contains(selectedPlayer)).isFalse
    }

    @Test
    fun shouldGenerateTheHints() {
        val result = underTest.generatePlayerHints(
            mapOf(
                "trophies" to ClientPlayerTrophies.trophies,
                "transfers" to ClientTransfersData.transfers
            )
        )

        assertThat(result.size).isEqualTo(5)
    }


    @Test
    fun shouldGenerateARandomPlayerGame() {
        every { playersData.trophies(any()) } returns ClientPlayerTrophies.trophies
        every { playersData.transfers(any()) } returns ClientTransfersData.transfers

        val result = underTest.getRandomPlayerGame(
            ClientTeamDetails.mockPlayersResponse[0],
            mapOf(1 to ClientTeamDetails.mockPlayersResponse)
        )

        assertThat(result.isAvailable).isTrue
        assertThat(result.hints).isNotEmpty
        assertThat(result.options).isNotEmpty

        verify(exactly = 1) { playersData.trophies(any()) }
        verify(exactly = 1) { playersData.transfers(any()) }
    }

    @Test
    fun shouldGenerateAnInvalidRandomPlayerGame() {

        val result = underTest.getRandomPlayerGame(
            null,
            mapOf(1 to ClientTeamDetails.mockPlayersResponse)
        )

        assertThat(result.isAvailable).isFalse

    }

    @Test
    fun shouldGenerateAPlayerGame() {
        every { playersData.trophies(any()) } returns ClientPlayerTrophies.trophies
        every { playersData.transfers(any()) } returns ClientTransfersData.transfers
        every { teamData.teamSquad(any(), any()) } returns mapOf(1 to ClientTeamDetails.mockPlayersResponse)

        val result = underTest.generatePlayerGame(
            1243
        )


        assertThat(result.isAvailable).isTrue
        assertThat(result.hints).isNotEmpty
        assertThat(result.options).isNotEmpty

        verify(exactly = 2) { teamData.teamSquad(any(), any()) }
        verify(exactly = 1) { playersData.trophies(any()) }
        verify(exactly = 1) { playersData.transfers(any()) }
    }


    @Test
    fun shouldGenerateAnInvalidPlayerGame() {
        every { teamData.teamSquad(any(), any()) } returns emptyMap()

        val result = underTest.generatePlayerGame(
            1243
        )


        assertThat(result.isAvailable).isFalse

        verify(exactly = 2) { teamData.teamSquad(any(), any()) }
    }
}