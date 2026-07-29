package org.footballproject.seasons

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.client.ApiSportsClient
import org.footballproject.data.client.ClientLeagueData

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SeasonsTest {

    private val leaguesData: ApiSportsClient = mockk()
    val underTest = Seasons(leaguesData)

    @Test
    fun shouldBuildTheLeaguesSeasonsMap() {
        every { leaguesData.fetchAllLeagues("/leagues?current=true") } returns ClientLeagueData.allLeagues

        val resultMap = underTest.leaguesCurrentSeasons()

        assertThat(resultMap[39]).isEqualTo(2025)
        assertThat(resultMap[334]).isEqualTo(2024)

        verify { leaguesData.fetchAllLeagues("/leagues?current=true") }

    }

    @Test
    fun shouldGetTheCurrentLeagueSeason() {
        every { leaguesData.fetchAllLeagues("/leagues?current=true") } returns ClientLeagueData.allLeagues

        assertThat(underTest.leagueCurrentSeason(39)).isEqualTo(2025)

        verify { leaguesData.fetchAllLeagues("/leagues?current=true") }

    }

    @Test
    fun shouldGetTheCurrentYearWithoutALeagueID() {
        assertThat(underTest.leagueCurrentSeason()).isEqualTo(LocalDate.now().year)
    }
}