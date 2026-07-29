package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient
import org.footballproject.data.client.ClientTeamDetails
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.clientData.ApiPagingResponse
import org.footballproject.clientData.Paging
import org.footballproject.data.client.ClientSquadPlayers
import org.footballproject.data.client.ClientTeamLeaguesParticipation
import org.footballproject.data.client.ClientTeamStats
import org.footballproject.data.client.ClientTeamTransfers
import org.footballproject.seasons.Seasons
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val seasons: Seasons = mockk()
    val underTest = TeamData(apiSportsClient, seasons)


    @Test
    fun `fetch team details and coach map`() {
        every { apiSportsClient.fetchTeamDetails("/teams?id=${33}") } returns ClientTeamDetails.details
        every { apiSportsClient.fetchCoachDetails("/coachs?team=${33}") } returns listOf(ClientTeamDetails.coach)

        val result = underTest.getTeamsDetails(33)

        assertThat(result["details"]).isEqualTo(ClientTeamDetails.details)
        assertThat(result["coach"]).isEqualTo(ClientTeamDetails.coach)

        verify { apiSportsClient.fetchTeamDetails("/teams?id=${33}") }
        verify { apiSportsClient.fetchCoachDetails("/coachs?team=${33}") }
    }

    @Test
    fun `fetch team squad`() {
        every { apiSportsClient.fetchPlayers(any()) } returns ApiPagingResponse(
            ClientTeamDetails.mockPlayersResponse, Paging(1, 1)
        )
        every { seasons.leagueCurrentSeason() } returns 2025

        val result = underTest.teamSquad(33)

        assertThat(result).isEqualTo(mapOf(1 to ClientTeamDetails.mockPlayersResponse))

        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchPlayers(any()) }
    }

    @Test
    fun `fetch team squad on specific year`() {
        every { apiSportsClient.fetchPlayers(any()) } returns ApiPagingResponse(
            ClientTeamDetails.mockPlayersResponse, Paging(1, 1)
        )
        val result = underTest.teamSquad(33, 2018)

        assertThat(result).isEqualTo(mapOf(1 to ClientTeamDetails.mockPlayersResponse))

        verify { apiSportsClient.fetchPlayers(any()) }
    }

    @Test
    fun `fetch current team squad`() {
        every { apiSportsClient.fetchCurrentSquad(any()) } returns ClientSquadPlayers.squadPlayers


        val result = underTest.currentTeamSquad(33)

        assertThat(result.size).isEqualTo(2)

        verify { apiSportsClient.fetchCurrentSquad(any()) }
    }


    @Test
    fun `fetch team transfers`() {
        every { apiSportsClient.fetchTransfers(any()) } returns ClientTeamTransfers.mockTeamTransfers


        val result = underTest.transfers(33)

        assertThat(result.size).isEqualTo(3)

        verify { apiSportsClient.fetchTransfers(any()) }
    }

    @Test
    fun `fetch team client statistics`() {
        every { apiSportsClient.fetchTeamStats(any()) } returns ClientTeamStats.mockTeamStats

        val result = underTest.teamStats(33, 2025, 39)

        assertThat(result.league).isNotNull
        assertThat(result.cards).isNotNull
        assertThat(result.team).isNotNull

        verify { apiSportsClient.fetchTeamStats(any()) }
    }

    @Test
    fun `fetch two teams client statistics`() {
        every { apiSportsClient.fetchTeamStats(any()) } returns ClientTeamStats.mockTeamStats
        every { seasons.leagueCurrentSeason(any()) } returns 2025
        val result = underTest.getTwoTeamsStats(33, 50, 39)

        assertThat(result.size).isEqualTo(2)

        verify { apiSportsClient.fetchTeamStats(any()) }
        verify { seasons.leagueCurrentSeason(any()) }
    }


    @Test
    fun `fetch team leagues`() {
        every { apiSportsClient.fetchTeamLeagues("/leagues?team=128&season=2024") } returns ClientTeamLeaguesParticipation.participation

        val result = underTest.teamLeagues(128, 2024)

        assertThat(result).isNotEmpty

        verify { apiSportsClient.fetchTeamLeagues("/leagues?team=128&season=2024") }
    }


}