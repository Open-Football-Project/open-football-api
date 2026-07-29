package match.insights.apidata

import match.insights.client.ApiSportsClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import match.insights.data.client.raw.ClientRankingsData
import match.insights.model.RankingKey
import match.insights.seasons.Seasons
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RankingsDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val seasons: Seasons = mockk()
    val underTest = RankingsData(apiSportsClient, seasons)

    @Test
    fun `fetch top scorers`() {
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        every { apiSportsClient.fetchLeagueRanking("/players/topscorers?league=128&season=2025") } returns
                ClientRankingsData.topScorers

        val result = underTest.leagueRanking(RankingKey.SCORERS, 128)

        assertThat(result[0].player.name).isEqualTo("E. Haaland")
        assertThat(result[0].statistics[0].goals?.total).isEqualTo(11)

        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchLeagueRanking("/players/topscorers?league=128&season=2025") }
    }

    @Test
    fun `fetch top assists`() {
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        every { apiSportsClient.fetchLeagueRanking("/players/topassists?league=128&season=2025") } returns
                ClientRankingsData.topAssists

        val result = underTest.leagueRanking(RankingKey.ASSISTS, 128)

        assertThat(result[0].player.name).isEqualTo("M. Kudus")


        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchLeagueRanking("/players/topassists?league=128&season=2025") }
    }

    @Test
    fun `fetch top yellow cards`() {
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        every { apiSportsClient.fetchLeagueRanking("/players/topyellowcards?league=128&season=2025") } returns
                ClientRankingsData.topYellowCards

        val result = underTest.leagueRanking(RankingKey.YELLOW_CARD, 128)

        assertThat(result[0].statistics[0].cards?.yellow).isEqualTo(5)


        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchLeagueRanking("/players/topyellowcards?league=128&season=2025") }
    }

    @Test
    fun `fetch top red cards`() {
        every { seasons.leagueCurrentSeason(any()) } returns 2025

        every { apiSportsClient.fetchLeagueRanking("/players/topredcards?league=128&season=2025") } returns
                ClientRankingsData.topRedCards

        val result = underTest.leagueRanking(RankingKey.RED_CARD, 128)

        assertThat(result[0].statistics[0].cards?.red).isEqualTo(1)


        verify { seasons.leagueCurrentSeason(any()) }
        verify { apiSportsClient.fetchLeagueRanking("/players/topredcards?league=128&season=2025") }
    }


}