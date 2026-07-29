package org.footballproject.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.LeaguesData
import org.footballproject.apidata.TeamData
import org.footballproject.clientData.Team
import org.footballproject.data.client.ClientLeagueTeams
import org.footballproject.data.client.ClientTeamDetails
import org.footballproject.data.client.ClientTeamStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamGameServiceTest {
    val teamData: TeamData = mockk()
    val leagueData: LeaguesData = mockk()
    val underTest = TeamGameService(teamData, leagueData)

    @Test
    fun shouldGetAListOfTeamOnARandomYear() {
        every { leagueData.leagueTeams(128, any()) } returns ClientLeagueTeams.leagueTeams

        val result = underTest.getTeams(128, 2005, 2020)

        assertThat(result.first).isEqualTo(2005)
        assertThat(result.second).isNotEmpty

        verify(exactly = 2) { leagueData.leagueTeams(128, any()) }
    }


    @Test
    fun shouldCheckIfItIsAnInterestingPlayer() {
        assertThat(underTest.isAnInterestingPlayer(ClientTeamDetails.mockPlayersResponse[0])).isTrue
    }

    @Test
    fun shouldGenerateGameOptions() {
        assertThat(
            underTest.generateOptions(
                ClientTeamDetails.details.team.name, listOf(
                    ClientTeamDetails.details.copy(team = Team(name = "team x"))
                )
            )
        ).contains("team x", ClientTeamDetails.details.team.name)
    }

    @Test
    fun shouldGenerateAListOfStatsHints() {
        val result = underTest.generateStatsHints(ClientTeamStats.mockTeamStats)

        assertThat(result).isNotEmpty
    }

    @Test
    fun shouldGenerateAListOfPlayerHints() {
        val result = underTest.generatePlayerHints(ClientTeamDetails.mockPlayersResponse)

        assertThat(result).isNotEmpty
    }

    @Test
    fun shouldGenerateAListHints() {
        every { teamData.teamStats(435, 2023, 128) } returns ClientTeamStats.mockTeamStats
        every { teamData.teamSquad(435, 2023) } returns mapOf(1 to ClientTeamDetails.mockPlayersResponse)

        val result = underTest.generateHints(2023, 435, 128)

        assertThat(result).isNotEmpty

        verify(exactly = 1) { teamData.teamStats(435, 2023, 128) }
        verify(exactly = 1) { teamData.teamSquad(435, 2023) }
    }


    @Test
    fun shouldGenerateANewGame() {
        every { leagueData.leagueTeams(128, any()) } returns ClientLeagueTeams.leagueTeams
        every { teamData.teamStats(any(), any(), 128) } returns ClientTeamStats.mockTeamStats
        every { teamData.teamSquad(any(), any()) } returns mapOf(1 to ClientTeamDetails.mockPlayersResponse)

        val result = underTest.newTeamGame(128)

        assertThat(result.isAvailable).isTrue

        verify(exactly = 2) { leagueData.leagueTeams(128, any()) }
        verify(exactly = 1) { teamData.teamStats(any(), any(), 128) }
        verify(exactly = 1) { teamData.teamSquad(any(), any()) }
    }

    @Test
    fun shouldGenerateANotAvailableGame() {
        every { leagueData.leagueTeams(128, any()) } returns emptyList()

        val result = underTest.newTeamGame(128)

        assertThat(result.isAvailable).isFalse

        verify(exactly = 2) { leagueData.leagueTeams(128, any()) }

    }

}