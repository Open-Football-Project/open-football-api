package org.footballproject.apidata

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.client.ApiSportsClient
import org.footballproject.data.client.ClientLiveMatches
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