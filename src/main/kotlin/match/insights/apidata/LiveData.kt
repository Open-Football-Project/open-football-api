package match.insights.apidata

import match.insights.client.ApiSportsClient
import match.insights.clientData.LiveFixtureResponse
import org.springframework.stereotype.Component

@Component
class LiveData(
    private val apiSportsClient: ApiSportsClient
) {
    fun allLiveMatches(): List<LiveFixtureResponse> {
        return apiSportsClient.fetchLiveFixtures("/fixtures?live=all")
    }
}