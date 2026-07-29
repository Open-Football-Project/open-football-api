package org.footballproject.datamanipulation

import org.footballproject.data.client.ClientStatistics
import org.footballproject.data.client.ClientTeamStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamStatisticsManipulationTest {
    val underTest = TeamStatisticsManipulation()


    @Test
    fun twoTeamsLiveStats() {
        val result = underTest.twoTeamsStats(ClientStatistics.liveStatistics)

        assertThat(result.teamA).isNotNull
        assertThat(result.teamB).isNotNull
    }

    @Test
    fun twoTeamsSeasonStats() {
        val result = underTest.twoTeamsStats(ClientTeamStats.mockTeamStats, ClientTeamStats.mockTeamStats)

        assertThat(result.teamA).isNotNull
        assertThat(result.teamB).isNotNull

    }

    @Test
    fun getTeamStatsFromSeasonData() {
        val result = underTest.teamStats(ClientTeamStats.mockTeamStats)

        assertThat(result.teamId).isEqualTo(435)
        assertThat(result.teamName).isEqualTo("River Plate")
        assertThat(result.statistics).isNotEmpty

    }

    @Test
    fun getTeamStatsFromLiveData() {
        val result = underTest.teamStats(ClientStatistics.liveStatistics[0])
        assertThat(result.teamId).isEqualTo(101)
        assertThat(result.teamName).isEqualTo("Manchester United")
        assertThat(result.statistics).isNotEmpty
    }


    @Test
    fun willGetLiveStatsList() {
        val result = underTest.teamStatistics(ClientStatistics.liveStatistics[0].statistics)
        assertThat(result).isNotEmpty

        assertThat(result.first { it.name == "Shots insidebox" }.isPositive).isTrue
        assertThat(result.first { it.name == "Shots off Goal" }.isPositive).isFalse
        assertThat(result.first { it.name == "Ball Possession" }.value).isEqualTo(58)
        assertThat(result.first { it.name == "Ball Possession" }.total).isEqualTo(100)

    }

    @Test
    fun shouldGetAllStats() {
        val result = underTest.teamStatistics(ClientTeamStats.mockTeamStats)
        assertThat(result).isNotEmpty

        assertThat(result.first { it.name == "Won at Home" }).isNotNull
        assertThat(result.first { it.name == "Goals Forward at Home" }).isNotNull
        assertThat(result.first { it.name == "Clean Sheets at Home" }).isNotNull
        assertThat(result.first { it.name == "Penalties Missed" }).isNotNull
    }


    @Test
    fun willGetSeasonFixtureWonLostAndDraws() {
        val result = underTest.seasonFixturesStats(ClientTeamStats.mockTeamStats.fixtures)
        assertThat(result.first { it.name === "Won at Home" }.value).isEqualTo(10)
        assertThat(result.first { it.name === "Won at Home" }.total).isEqualTo(15)
        assertThat(result.first { it.name === "Won Away" }.value).isEqualTo(5)
        assertThat(result.first { it.name === "Won Away" }.total).isEqualTo(15)

        assertThat(result.first { it.name === "Lost at Home" }.value).isEqualTo(5)
        assertThat(result.first { it.name === "Lost at Home" }.total).isEqualTo(7)
        assertThat(result.first { it.name === "Lost Away" }.value).isEqualTo(2)
        assertThat(result.first { it.name === "Lost Away" }.total).isEqualTo(7)

        assertThat(result.first { it.name === "Draws at Home" }.value).isEqualTo(3)
        assertThat(result.first { it.name === "Draws at Home" }.total).isEqualTo(10)
        assertThat(result.first { it.name === "Draws Away" }.value).isEqualTo(7)
        assertThat(result.first { it.name === "Draws Away" }.total).isEqualTo(10)
    }


    @Test
    fun willGetSeasonGoalStats() {
        val result = underTest.seasonGoalsStats(ClientTeamStats.mockTeamStats.goals)
        assertThat(result.first { it.name === "Goals Forward at Home" }.value).isEqualTo(28)
        assertThat(result.first { it.name === "Goals Forward at Home" }.total).isEqualTo(45)
        assertThat(result.first { it.name === "Goals Forward Away" }.value).isEqualTo(17)
        assertThat(result.first { it.name === "Goals Forward Away" }.total).isEqualTo(45)

        assertThat(result.first { it.name === "Goals Against at Home" }.value).isEqualTo(16)
        assertThat(result.first { it.name === "Goals Against at Home" }.total).isEqualTo(24)
        assertThat(result.first { it.name === "Goals Against Away" }.value).isEqualTo(8)
        assertThat(result.first { it.name === "Goals Against Away" }.total).isEqualTo(24)

    }

    @Test
    fun willGetSeasonCleanSheetsStats() {
        val result = underTest.seasonStatsTotals(true, "Clean Sheets", ClientTeamStats.mockTeamStats.clean_sheet)
        assertThat(result.first { it.name == "Clean Sheets at Home" }.value).isEqualTo(7)
        assertThat(result.first { it.name == "Clean Sheets at Home" }.total).isEqualTo(15)
        assertThat(result.first { it.name == "Clean Sheets Away" }.value).isEqualTo(8)
        assertThat(result.first { it.name == "Clean Sheets Away" }.total).isEqualTo(15)

    }

    @Test
    fun willGetSeasonPenaltiesStats() {
        val result = underTest.seasonPenaltyStats(ClientTeamStats.mockTeamStats.penalty)
        assertThat(result.first { it.name == "Penalties Scored" }.value).isEqualTo(2)
        assertThat(result.first { it.name == "Penalties Scored" }.total).isEqualTo(2)
        assertThat(result.first { it.name == "Penalties Missed" }.value).isEqualTo(0)
        assertThat(result.first { it.name == "Penalties Missed" }.total).isEqualTo(2)

    }
}