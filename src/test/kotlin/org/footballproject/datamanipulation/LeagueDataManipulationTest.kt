package org.footballproject.datamanipulation

import io.mockk.every
import io.mockk.mockk
import org.footballproject.data.client.ClientLeagueData
import org.footballproject.data.client.ClientTeamDetails
import org.footballproject.data.response.LeaguesResponseData
import org.footballproject.sorting.LeaguesSort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LeagueDataManipulationTest {
    val leaguesSort: LeaguesSort = mockk()
    val underTest = LeagueDataManipulation(leaguesSort)

    @Test
    fun shouldReturnLeagueInfo() {
        val result = underTest.extractLeaguesInfo(ClientLeagueData.leagueStandings)

        assertThat(result.id).isEqualTo(1)
        assertThat(result.season).isEqualTo(2022)
        assertThat(result.group[0].label).isEqualTo("default")
        assertThat(result.group[0].teams[0].teamId).isEqualTo(33)
        assertThat(result.group[0].teams[0].rank).isEqualTo(1)
        assertThat(result.group[0].teams[0].points).isEqualTo(89)
        assertThat(result.group[0].teams[0].form).isEqualTo("WWDWW")
        assertThat(result.group[0].teams[0].played).isEqualTo(38)
        assertThat(result.group[0].teams[0].won).isEqualTo(28)
    }

    @Test
    fun shouldRanksAndPointsForBothTeams() {
        val result = underTest.positionAndPoints(33, 33, ClientLeagueData.leagueStandings)

        assertThat(result.homeTeam[0].points).isEqualTo(89)
        assertThat(result.awayTeam[0].points).isEqualTo(89)
        assertThat(result.homeTeam[0].position).isEqualTo(1)
        assertThat(result.awayTeam[0].position).isEqualTo(1)
        assertThat(result.awayTeam[0].description).isEqualTo("default")
        assertThat(result.homeTeam[0].description).isEqualTo("default")
    }


    @Test
    fun shouldGroupLeagues() {
        every { leaguesSort.sortPriorityCountryLeagues(any()) } returns listOf(
            LeaguesResponseData.argLeague,
            LeaguesResponseData.kazLeague
        )

        every { leaguesSort.sortInternationalLeagues(any()) } returns listOf(
            LeaguesResponseData.internationalLeagues[2]
        )

        val result = underTest.groupLeagues(ClientLeagueData.allLeagues)

        assertThat(result.internationals.size).isEqualTo(1)
        assertThat(result.countryLeagues[0].country).isEqualTo("Argentina")
        assertThat(result.countryLeagues[0].leagues[0].name).isEqualTo("anyLeague at the top")
        assertThat(result.countryLeagues[1].country).isEqualTo("Kazakhstan")
        assertThat(result.countryLeagues[1].leagues[0].name).isEqualTo("anyLeague at the top")

        assertThat(result.others.size).isEqualTo(1)

    }


    @Test
    fun shouldGetLeagueRankings() {
        val result = underTest.topNRankings(ClientTeamDetails.mockPlayersResponse)

        assertThat(result.topScorers.players.size).isEqualTo(2)
        assertThat(result.topAppearances.players.size).isEqualTo(2)
        assertThat(result.topAssists.players.size).isEqualTo(2)
        assertThat(result.topGoalsConceded.players.size).isEqualTo(2)
        assertThat(result.topPenaltiesMissed.players.size).isEqualTo(2)
        assertThat(result.topSaves.players.size).isEqualTo(2)
    }
}