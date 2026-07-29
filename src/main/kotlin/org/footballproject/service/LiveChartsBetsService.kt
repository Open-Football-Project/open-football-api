package org.footballproject.service

import org.footballproject.apidata.OddsData
import org.footballproject.clientData.LiveFixtureResponse
import org.footballproject.clientData.LiveOddsMarket
import org.footballproject.model.LiveOddsSnapshot
import org.footballproject.model.MatchStatus
import org.footballproject.props.ChartsProps
import org.footballproject.repository.LiveChartsBetsRepository
import org.footballproject.response.BetMarketInfo
import org.footballproject.response.BetOddsPoint
import org.footballproject.response.LiveChartableMatch
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class LiveChartsBetsService(
    private val oddsData: OddsData,
    private val liveChartsBetsRepository: LiveChartsBetsRepository,
    private val chartsProps: ChartsProps
) {

    fun captureLiveOdds(fixture: LiveFixtureResponse) {
        if (!isInTheRightStatus(fixture) || fixture.fixture.status?.elapsed == null) return

        val fixtureId = fixture.fixture.id
        val minute = fixture.fixture.status.elapsed
        val homeTeamName = fixture.teams.home?.name ?: "Unknown Team"
        val awayTeamName = fixture.teams.away?.name ?: "Unknown Team"

        oddsData.liveOdds(fixtureId)
            .map { it.copy(name = normalizeMarketName(it.name)) }
            .filter { chartsProps.oddsTrackedMarketNames.contains(it.name) }
            .forEach { market ->
                sanitize(market, minute).let { sanitized ->
                    if (sanitized.isNotEmpty()) {
                        liveChartsBetsRepository.appendSnapshots(
                            fixtureId,
                            market.id,
                            marketName = market.name,
                            homeTeamName = homeTeamName,
                            awayTeamName = awayTeamName,
                            sanitized
                        )
                    }
                }

            }
    }

    fun getAllLiveMarkets(fixtureId: Int): List<List<BetMarketInfo>> =
        liveChartsBetsRepository.getAllMarkets(fixtureId)
            .map { market -> BetMarketInfo(market.marketId, market.marketName, buildHistory(market.history)) }
            .chunked(3)

    fun trackedFixtures(): List<LiveChartableMatch> =
        liveChartsBetsRepository.getTrackedFixtures()
            .map { LiveChartableMatch(it.fixtureId, it.homeTeamName, it.awayTeamName) }

    private fun buildHistory(history: List<LiveOddsSnapshot>): Map<String, List<BetOddsPoint>> =
        history.groupBy(LiveOddsSnapshot::label) { BetOddsPoint(it.minute, it.odd, it.capturedAt) }

    private fun sanitize(market: LiveOddsMarket, minute: Int): List<LiveOddsSnapshot> {
        val valid = market.values.filter { !it.suspended && it.odd != "" && it.odd != "0" }
        val result = if (valid.any { it.main == true }) valid.filter { it.main == true } else valid
        val capturedAt = Instant.now()
        return result.map { LiveOddsSnapshot(label = it.value, odd = it.odd, minute = minute, capturedAt = capturedAt) }
    }

    private fun normalizeMarketName(name: String): String =
        name.trim().lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')

    private fun isInTheRightStatus(fixtureResponse: LiveFixtureResponse) = MatchStatus.entries
        .find { it.code == fixtureResponse.fixture.status?.short }
        ?.isNow() == true
}
