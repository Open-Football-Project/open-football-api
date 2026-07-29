package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient
import org.footballproject.clientData.FixtureOdds
import org.footballproject.clientData.LiveOddsMarket
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