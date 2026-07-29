package match.insights.apidata

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import match.insights.client.ApiSportsClient
import match.insights.data.client.ClientLiveMatches
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LiveDataTest {

    val apiSportsClient: ApiSportsClient = mockk()
    val underTest: LiveData = LiveData(apiSportsClient)

    @Test
    fun shouldFetchLiveFixtures() {
        every { apiSportsClient.fetchLiveFixtures("/fixtures?live=all") } returns ClientLiveMatches.liveFixtures

        assertThat(underTest.allLiveMatches()).isNotEmpty

        verify { apiSportsClient.fetchLiveFixtures("/fixtures?live=all") }

    }
}