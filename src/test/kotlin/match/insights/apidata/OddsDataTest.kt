package match.insights.apidata

import match.insights.client.ApiSportsClient
import match.insights.clientData.LiveFixtureOdds
import match.insights.clientData.LiveOddsMarket
import match.insights.data.client.ClientOddsData

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OddsDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val underTest = OddsData(apiSportsClient)

    @Test
    fun `fetch all odds`() {

        every { apiSportsClient.fetchFixtureOdds("/odds?fixture=1234") } returns ClientOddsData.mockResponse

        val result = underTest.fetchAllOdds(1234)

        assertThat(result).isNotEmpty()

        verify { apiSportsClient.fetchFixtureOdds("/odds?fixture=1234") }
    }

    @Test
    fun shouldFetchLiveOddsAndReturnMarketsFromFirstEntry() {
        val market = LiveOddsMarket(59, "Fulltime Result", emptyList())
        val fixtureOdds = LiveFixtureOdds(odds = listOf(market))
        every { apiSportsClient.fetchLiveFixtureOdds("/odds/live?fixture=1539007") } returns listOf(fixtureOdds)

        val result = underTest.liveOdds(1539007)

        assertThat(result).containsExactly(market)
        verify { apiSportsClient.fetchLiveFixtureOdds("/odds/live?fixture=1539007") }
    }

    @Test
    fun shouldReturnEmptyListWhenLiveOddsResponseIsEmpty() {
        every { apiSportsClient.fetchLiveFixtureOdds("/odds/live?fixture=1539007") } returns emptyList()

        val result = underTest.liveOdds(1539007)

        assertThat(result).isEmpty()
        verify { apiSportsClient.fetchLiveFixtureOdds("/odds/live?fixture=1539007") }
    }
}