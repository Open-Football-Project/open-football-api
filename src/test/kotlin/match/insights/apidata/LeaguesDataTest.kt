package match.insights.apidata

import match.insights.client.ApiSportsClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import match.insights.clientData.ApiPagingResponse
import match.insights.clientData.Paging
import match.insights.data.client.ClientLeagueData
import match.insights.data.client.ClientLeagueTeams
import match.insights.data.client.ClientMatchResponseData
import match.insights.data.client.ClientTeamDetails
import match.insights.seasons.Seasons
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LeaguesDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val seasons: Seasons = mockk()
    val underTest = LeaguesData(apiSportsClient, seasons)

    @Test
    fun `fetch league standings`() {
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        every { apiSportsClient.fetchLeagueInfo(any()) } returns
                ClientLeagueData.leagueStandings

        val result = underTest.leagueStandings(1)

        assertThat(result?.id).isEqualTo(1)
        assertThat(result?.season).isEqualTo(2022)
        assertThat(result?.standings?.first()?.first()?.rank).isEqualTo(1)
        assertThat(result?.standings?.first()?.first()?.team?.id).isEqualTo(33)

        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchLeagueInfo(any()) }
    }


    @Test
    fun `fetch available leagues`() {
        every { apiSportsClient.fetchAllLeagues("/leagues?current=true") } returns ClientLeagueData.allLeagues.filter { it.seasons?.any { it.current } == true }

        val result = underTest.leagues()

        assertThat(result[1].league.name).isEqualTo("world cup")
        assertThat(result[1].country.name).isEqualTo("World")
        assertThat(result[2].league.name).isEqualTo("Premier League")
        assertThat(result[2].country.name).isEqualTo("England")

        verify { apiSportsClient.fetchAllLeagues("/leagues?current=true") }
    }

    @Test
    fun `fetch league season matches`() {
        every { apiSportsClient.fetchMatches(any()) } returns ClientMatchResponseData.matchResponseList
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        val result = underTest.leagueSeasonMatches(1)

        assertThat(result.size).isEqualTo(4)

        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchMatches("/fixtures/?season=2025&league=1") }
    }

    @Test
    fun `fetch all League Players`() {
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        every { apiSportsClient.fetchPlayers(any()) } returns ApiPagingResponse(
            ClientTeamDetails.mockPlayersResponse, Paging(1, 1)
        )

        val result = underTest.allLeaguePlayers(39)

        assertThat(result).isEqualTo(mapOf(1 to ClientTeamDetails.mockPlayersResponse))

        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchPlayers(any()) }
    }

    @Test
    fun `fetch league teams`() {
        every { apiSportsClient.fetchLeagueTeams("/teams?league=128&season=2024") } returns ClientLeagueTeams.leagueTeams

        assertThat(underTest.leagueTeams(128, 2024)).isNotEmpty

        verify { apiSportsClient.fetchLeagueTeams("/teams?league=128&season=2024") }
    }
}