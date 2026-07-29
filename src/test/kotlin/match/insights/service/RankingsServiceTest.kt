package match.insights.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import match.insights.apidata.RankingsData
import match.insights.data.client.raw.ClientRankingsData
import match.insights.model.RankingKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class RankingsServiceTest {

    val apidata: RankingsData = mockk()

    val underTest = RankingsService(apidata)

    @Test
    fun shouldGetLeagueTopScorers() {
        every { apidata.leagueRanking(RankingKey.SCORERS, 128) } returns ClientRankingsData.topScorers


        val result = underTest.leagueRanking(RankingKey.SCORERS, 128)

        assertThat(result[0].playerName).isEqualTo("E. Haaland")
        assertThat(result[0].playerTotalGoals).isEqualTo(11)
        assertThat(result[0].playerTeamName).isEqualTo("Manchester City")

        verify { apidata.leagueRanking(RankingKey.SCORERS, 128) }

    }

}