package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient
import org.footballproject.data.client.ClientEventsData
import org.footballproject.data.client.ClientMatchResponseData

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.data.client.ClientLineupResponseData
import org.footballproject.data.client.ClientStatistics
import org.footballproject.seasons.Seasons
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MatchesDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val seasons: Seasons = mockk()
    val underTest = MatchesData(apiSportsClient, seasons)


    @Test
    fun `fetch matches of the day`() {
        val utcNow = ZonedDateTime.now(ZoneId.of("UTC"))
        val today = utcNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        every { apiSportsClient.fetchMatches("/fixtures?date=$today") } returns ClientMatchResponseData.matchResponseList

        val result = underTest.matchesOfTheDay(today)

        assertThat(result).isNotEmpty()

        verify { apiSportsClient.fetchMatches("/fixtures?date=$today") }
    }


    @Test
    fun `fetch match details`() {

        every { apiSportsClient.fetchMatchDetails("/fixtures?id=1234") } returns ClientMatchResponseData.matchResponse

        val result = underTest.matchDetails(1234)

        assertThat(result).isNotNull()

        verify { apiSportsClient.fetchMatchDetails("/fixtures?id=1234") }
    }

    @Test
    fun `fetch head to head`() {
        every { apiSportsClient.fetchMatches("/fixtures/headtohead?h2h=${12}-${22}") } returns ClientMatchResponseData.matchResponseList

        val result = underTest.headToHead(12, 22)

        assertThat(result.size).isEqualTo(1)

        verify { apiSportsClient.fetchMatches("/fixtures/headtohead?h2h=${12}-${22}") }
    }

    @Test
    fun `fetch last five matches results`() {
        every { seasons.leagueCurrentSeason() } returns 2025
        every { apiSportsClient.fetchMatches(any()) } returns ClientMatchResponseData.matchResponseList

        val result = underTest.lastFiveMatchesResults(1, 2)

        assertThat(result[1]).isNotEmpty
        assertThat(result[2]).isNotEmpty

        verify { apiSportsClient.fetchMatches(any()) }
        verify { seasons.leagueCurrentSeason() }
    }

    @Test
    fun `fetch teams leagues results`() {
        every { apiSportsClient.fetchMatches(any()) } returns ClientMatchResponseData.matchResponseList
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        val result = underTest.getTeamsLeagueMatches(1, 2, 1)

        assertThat(result[1]).isNotEmpty
        assertThat(result[2]).isNotEmpty

        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchMatches(any()) }
    }


    @Test
    fun `fetch last five matches events`() {
        every { apiSportsClient.fetchMatches(any()) } returns ClientMatchResponseData.matchResponseList
        every { seasons.leagueCurrentSeason() } returns 2025
        every { apiSportsClient.fetchMatchEvents(any()) } returns ClientEventsData.mockEvents

        val result = underTest.lastFiveMatchesEvents(33)

        assertThat(result.size).isEqualTo(3)

        verify { seasons.leagueCurrentSeason() }
        verify { apiSportsClient.fetchMatches(any()) }
        verify { apiSportsClient.fetchMatchEvents(any()) }
    }

    @Test
    fun `fetch most recent played matches`() {

        every { apiSportsClient.fetchMatches(any()) } returns ClientMatchResponseData.matchResponseList
        every { seasons.leagueCurrentSeason() } returns 2025

        val result = underTest.mostRecentPlayedMatches(33, 44)

        assertThat(result[33]?.fixture?.date).isNotNull
        assertThat(result[44]?.fixture?.date).isNotNull

        verify { apiSportsClient.fetchMatches(any()) }
        verify { seasons.leagueCurrentSeason() }
    }

    @Test
    fun `fetch upcoming and previous matches`() {

        every { apiSportsClient.fetchMatches(any()) } returns ClientMatchResponseData.matchResponseList
        every { seasons.leagueCurrentSeason() } returns 2025

        val result = underTest.previousAndUpcomingMatches(33)

        assertThat(result["previous"]).isEqualTo(ClientMatchResponseData.matchResponseList)
        assertThat(result["upcoming"]).isEqualTo(ClientMatchResponseData.matchResponseList)

        verify { apiSportsClient.fetchMatches(any()) }
        verify { seasons.leagueCurrentSeason() }

    }

    @Test
    fun shouldFetchLiveStatistics() {
        every { apiSportsClient.fetchLiveStatistics("/fixtures/statistics?fixture=1457376") } returns ClientStatistics.liveStatistics

        val result = underTest.statistics(1457376)

        assertThat(result).isEqualTo(ClientStatistics.liveStatistics)

        verify { apiSportsClient.fetchLiveStatistics("/fixtures/statistics?fixture=1457376") }

    }


    @Test
    fun shouldFetchLiveLineUps() {
        every { apiSportsClient.fetchLiveLineups("/fixtures/lineups?fixture=1457376") } returns ClientLineupResponseData.mockLineups

        val result = underTest.lineups(1457376)

        assertThat(result).isEqualTo(ClientLineupResponseData.mockLineups)

        verify { apiSportsClient.fetchLiveLineups("/fixtures/lineups?fixture=1457376") }

    }

    @Test
    fun shouldFetchMatchEvents() {
        every { apiSportsClient.fetchMatchEvents("/fixtures/events?fixture=1457376") } returns ClientEventsData.mockEvents

        val result = underTest.singleMatchEvents(1457376)

        assertThat(result).isEqualTo(ClientEventsData.mockEvents)

        verify { apiSportsClient.fetchMatchEvents("/fixtures/events?fixture=1457376") }
    }

}