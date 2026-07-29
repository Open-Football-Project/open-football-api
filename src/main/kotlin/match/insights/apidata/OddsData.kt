package match.insights.apidata

import match.insights.client.ApiSportsClient
import match.insights.clientData.FixtureOdds
import match.insights.clientData.LiveOddsMarket
import org.springframework.stereotype.Component

@Component
class OddsData(
    private val apiSportsClient: ApiSportsClient,
) {

    fun fetchAllOdds(fixtureId: Int): List<FixtureOdds> =
        apiSportsClient.fetchFixtureOdds("/odds?fixture=$fixtureId")

    fun liveOdds(fixtureId: Int): List<LiveOddsMarket> =
        apiSportsClient.fetchLiveFixtureOdds("/odds/live?fixture=$fixtureId")
            .firstOrNull()?.odds ?: emptyList()
}