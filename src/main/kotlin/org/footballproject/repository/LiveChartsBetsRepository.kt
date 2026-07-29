package org.footballproject.repository

import org.footballproject.model.LiveOddsFixture
import org.footballproject.model.LiveOddsMarketEntry
import org.footballproject.model.LiveOddsSnapshot
import org.footballproject.props.ChartsProps
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class LiveChartsBetsRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val chartsProps: ChartsProps
) {

    fun appendSnapshots(
        fixtureId: Int,
        marketId: Int,
        marketName: String,
        homeTeamName: String,
        awayTeamName: String,
        snapshots: List<LiveOddsSnapshot>
    ) {
        val key = buildKey(fixtureId, marketId)
        val history = (getMarket(fixtureId, marketId)?.history ?: emptyList()) + snapshots
        redisTemplate.opsForValue()
            .set(key, LiveOddsMarketEntry(marketId, marketName, homeTeamName, awayTeamName, history))
        redisTemplate.expire(key, Duration.ofSeconds(chartsProps.ttlSeconds))
    }

    fun getAllMarkets(fixtureId: Int): List<LiveOddsMarketEntry> =
        (redisTemplate.keys(buildKeyPattern(fixtureId)) ?: emptySet())
            .mapNotNull { redisTemplate.opsForValue().get(it) as? LiveOddsMarketEntry }

    fun getTrackedFixtures(): Set<LiveOddsFixture> =
        (redisTemplate.keys(ALL_FIXTURES_PATTERN) ?: emptySet())
            .mapNotNull { key ->
                (redisTemplate.opsForValue().get(key) as? LiveOddsMarketEntry)?.let {
                    LiveOddsFixture(fixtureIdFromKey(key), it.homeTeamName, it.awayTeamName)
                }
            }.toSet()

    private fun getMarket(fixtureId: Int, marketId: Int): LiveOddsMarketEntry? =
        redisTemplate.opsForValue().get(buildKey(fixtureId, marketId)) as? LiveOddsMarketEntry

    private fun fixtureIdFromKey(key: String): Int = key.split(":")[1].toInt()

    private fun buildKey(fixtureId: Int, marketId: Int) = "live-chart:$fixtureId:bets:$marketId"

    private fun buildKeyPattern(fixtureId: Int) = "live-chart:$fixtureId:bets:*"

    companion object {
        private const val ALL_FIXTURES_PATTERN = "live-chart:*:bets:*"
    }
}
