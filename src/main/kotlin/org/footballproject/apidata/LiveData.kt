package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient
import org.footballproject.clientData.LiveFixtureResponse
import org.springframework.stereotype.Component

@Component
class LiveData(
    private val apiSportsClient: ApiSportsClient
) {
    fun allLiveMatches(): List<LiveFixtureResponse> {
        return apiSportsClient.fetchLiveFixtures("/fixtures?live=all")
    }
}