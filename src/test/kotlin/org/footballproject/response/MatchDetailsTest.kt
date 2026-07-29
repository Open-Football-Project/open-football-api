package org.footballproject.response

import org.footballproject.data.client.ClientMatchResponseData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MatchDetailsTest {

    @Test
    fun shouldMapFromMatchResponse() {
        val response = MatchDetails.fromResponseData(ClientMatchResponseData.matchResponse, setOf(123456))

        assertThat(response.date).isEqualTo(ClientMatchResponseData.matchResponse.fixture.date)
        assertThat(response.venue).isEqualTo(ClientMatchResponseData.matchResponse.venue)
        assertThat(response.league).isEqualTo(ClientMatchResponseData.matchResponse.league)
        assertThat(response.homeTeam).isEqualTo(ClientMatchResponseData.matchResponse.teams.home)
        assertThat(response.awayTeam).isEqualTo(ClientMatchResponseData.matchResponse.teams.away)
        assertThat(response.goals).isEqualTo(ClientMatchResponseData.matchResponse.goals)
        assertThat(response.score).isEqualTo(ClientMatchResponseData.matchResponse.score)
        assertThat(response.isLiveNow).isTrue
    }
}