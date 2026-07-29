package match.insights.repository

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import match.insights.model.LiveOddsFixture
import match.insights.model.LiveOddsMarketEntry
import match.insights.model.LiveOddsSnapshot
import match.insights.props.ChartsProps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import java.time.Instant

class LiveChartsBetsRepositoryTest {

    private val redisTemplate: RedisTemplate<String, Any> = mockk()
    private val valueOps = mockk<ValueOperations<String, Any>>()
    private val underTest = LiveChartsBetsRepository(
        redisTemplate,
        ChartsProps(ttlSeconds = 14400, pollingMilli = 180000, trackedLeagueIds = emptyList(), schedulingEnabled = true)
    )

    private val key = "live-chart:1539007:bets:59"

    private val snapshots = listOf(
        LiveOddsSnapshot(
            label = "Home",
            odd = "1.758",
            minute = 23,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z")
        ),
        LiveOddsSnapshot(label = "Away", odd = "4.500", minute = 23, capturedAt = Instant.parse("2026-06-22T10:00:00Z"))
    )

    @Test
    fun shouldCreateMarketEntryWhenStoringFirstSnapshots() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get(key) } returns null
        every { valueOps.set(key, any()) } just Runs
        every { redisTemplate.expire(key, Duration.ofSeconds(14400)) } returns true

        underTest.appendSnapshots(1539007, 59, "fulltime_result", "Home FC", "Away FC", snapshots)

        verify { valueOps.set(key, LiveOddsMarketEntry(59, "fulltime_result", "Home FC", "Away FC", snapshots)) }
    }

    @Test
    fun shouldAppendSnapshotsToExistingMarketHistory() {
        val earlier = LiveOddsSnapshot(
            label = "Home",
            odd = "1.7",
            minute = 10,
            capturedAt = Instant.parse("2026-06-22T09:00:00Z")
        )
        val existing = LiveOddsMarketEntry(59, "fulltime_result", "Home FC", "Away FC", listOf(earlier))
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get(key) } returns existing
        every { valueOps.set(key, any()) } just Runs
        every { redisTemplate.expire(key, Duration.ofSeconds(14400)) } returns true

        underTest.appendSnapshots(1539007, 59, "fulltime_result", "Home FC", "Away FC", snapshots)

        verify {
            valueOps.set(
                key,
                LiveOddsMarketEntry(59, "fulltime_result", "Home FC", "Away FC", listOf(earlier) + snapshots)
            )
        }
    }

    @Test
    fun shouldRefreshTtlToConfiguredTtlSecondsOnEveryWrite() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get(key) } returns null
        every { valueOps.set(key, any()) } just Runs
        every { redisTemplate.expire(key, Duration.ofSeconds(14400)) } returns true

        underTest.appendSnapshots(1539007, 59, "fulltime_result", "Home FC", "Away FC", snapshots)

        verify { redisTemplate.expire(key, Duration.ofSeconds(14400)) }
    }

    @Test
    fun shouldReturnEmptyListWhenNoMarketsStoredForFixture() {
        every { redisTemplate.keys("live-chart:1539007:bets:*") } returns emptySet()

        assertThat(underTest.getAllMarkets(1539007)).isEmpty()
    }

    @Test
    fun shouldReturnAllStoredMarketsForFixture() {
        val fulltimeResult = LiveOddsMarketEntry(59, "fulltime_result", "Home FC", "Away FC", snapshots)
        val matchCorners = LiveOddsMarketEntry(20, "match_corners", "Home FC", "Away FC", snapshots)
        every { redisTemplate.keys("live-chart:1539007:bets:*") } returns setOf(
            "live-chart:1539007:bets:59",
            "live-chart:1539007:bets:20"
        )
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:1539007:bets:59") } returns fulltimeResult
        every { valueOps.get("live-chart:1539007:bets:20") } returns matchCorners

        assertThat(underTest.getAllMarkets(1539007)).containsExactlyInAnyOrder(fulltimeResult, matchCorners)
    }

    @Test
    fun shouldReturnEmptyListWhenNoOddsTrackedAtAll() {
        every { redisTemplate.keys("live-chart:*:bets:*") } returns emptySet()

        assertThat(underTest.getTrackedFixtures()).isEmpty()
    }

    @Test
    fun shouldReturnOneEntryPerDistinctFixtureAcrossAllTrackedOdds() {
        val fulltimeResult = LiveOddsMarketEntry(59, "fulltime_result", "Home FC", "Away FC", snapshots)
        val matchCorners = LiveOddsMarketEntry(20, "match_corners", "Home FC", "Away FC", snapshots)
        val otherFixture = LiveOddsMarketEntry(59, "fulltime_result", "Other Home", "Other Away", snapshots)
        every { redisTemplate.keys("live-chart:*:bets:*") } returns setOf(
            "live-chart:1539007:bets:59",
            "live-chart:1539007:bets:20",
            "live-chart:2000000:bets:59"
        )
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:1539007:bets:59") } returns fulltimeResult
        every { valueOps.get("live-chart:1539007:bets:20") } returns matchCorners
        every { valueOps.get("live-chart:2000000:bets:59") } returns otherFixture

        assertThat(underTest.getTrackedFixtures()).containsExactlyInAnyOrder(
            LiveOddsFixture(1539007, "Home FC", "Away FC"),
            LiveOddsFixture(2000000, "Other Home", "Other Away")
        )
    }
}
