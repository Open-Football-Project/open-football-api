package org.footballproject.repository

import org.footballproject.model.LiveChartPoint
import org.footballproject.model.LiveIndicatorFixture
import org.footballproject.model.LiveIndicatorFixtures
import org.footballproject.model.LiveIndicatorType
import org.footballproject.props.ChartsProps
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant

@Repository
class LiveChartsRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val chartsProps: ChartsProps
) {

    fun append(
        fixtureId: Int,
        indicatorType: LiveIndicatorType,
        homeTeamName: String,
        awayTeamName: String,
        point: LiveChartPoint
    ) {
        val key = buildKey(indicatorType)
        val ttl = Duration.ofSeconds(chartsProps.ttlSeconds)
        val staleCutoff = Instant.now().minus(ttl)

        val freshFixtures = readFixtures(key).filter { it.points.last().capturedAt >= staleCutoff }

        val updated = updatedFixture(fixtureId, freshFixtures, homeTeamName, awayTeamName, point)
        val savingFixtures = freshFixtures.filterNot { it.fixtureId == fixtureId } + updated

        redisTemplate.opsForValue().set(key, LiveIndicatorFixtures(savingFixtures))
        redisTemplate.expire(key, ttl)
    }

    fun getFixture(fixtureId: Int, indicatorType: LiveIndicatorType): LiveIndicatorFixture? =
        readFixtures(buildKey(indicatorType)).find { it.fixtureId == fixtureId }

    fun getAllFixtures(indicatorType: LiveIndicatorType): List<LiveIndicatorFixture> =
        readFixtures(buildKey(indicatorType))

    private fun readFixtures(key: String): List<LiveIndicatorFixture> =
        (redisTemplate.opsForValue().get(key) as? LiveIndicatorFixtures)?.fixtures ?: emptyList()

    private fun updatedFixture(
        fixtureId: Int,
        fixtures: List<LiveIndicatorFixture>,
        homeTeamName: String,
        awayTeamName: String,
        point: LiveChartPoint
    ): LiveIndicatorFixture =
        fixtures.firstOrNull { it.fixtureId == fixtureId }
            ?.let { it.copy(points = it.points + point) }
            ?: LiveIndicatorFixture(fixtureId, homeTeamName, awayTeamName, listOf(point))

    private fun buildKey(indicatorType: LiveIndicatorType) = "live-chart:${indicatorType.name.lowercase()}"
}