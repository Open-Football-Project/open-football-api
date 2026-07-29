package org.footballproject.repository

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.model.LiveChartPoint
import org.footballproject.model.LiveIndicatorFixture
import org.footballproject.model.LiveIndicatorFixtures
import org.footballproject.model.LiveIndicatorType
import org.footballproject.props.ChartsProps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import java.time.Instant

class LiveChartsRepositoryTest {

    private val redisTemplate: RedisTemplate<String, Any> = mockk()
    private val valueOps = mockk<ValueOperations<String, Any>>()

    private val underTest = LiveChartsRepository(
        redisTemplate,
        ChartsProps(ttlSeconds = 14400, pollingMilli = 180000, trackedLeagueIds = emptyList(), schedulingEnabled = true)
    )

    @Test
    fun shouldCreateANewFixtureEntryWhenAppendingForTheFirstTime() {
        val point = LiveChartPoint(minute = 23, value = 71, capturedAt = Instant.parse("2026-06-22T10:00:00Z"))
        val expected = LiveIndicatorFixtures(listOf(LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(point))))

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns null
        every { valueOps.set("live-chart:momentum", expected) } just Runs
        every { redisTemplate.expire("live-chart:momentum", Duration.ofSeconds(14400)) } returns true

        underTest.append(233, LiveIndicatorType.MOMENTUM, "Home FC", "Away FC", point)

        verify { valueOps.set("live-chart:momentum", expected) }
        verify { redisTemplate.expire("live-chart:momentum", Duration.ofSeconds(14400)) }
    }

    @Test
    fun shouldAppendThePointToAnExistingFixture() {
        val firstPoint = LiveChartPoint(10, 20, Instant.now().minusSeconds(60))
        val secondPoint = LiveChartPoint(13, 30, Instant.now())
        val existing =
            LiveIndicatorFixtures(listOf(LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(firstPoint))))

        val expected =
            LiveIndicatorFixtures(
                listOf(
                    LiveIndicatorFixture(
                        233,
                        "Home FC",
                        "Away FC",
                        listOf(firstPoint, secondPoint)
                    )
                )
            )

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns existing
        every { valueOps.set("live-chart:momentum", expected) } just Runs
        every { redisTemplate.expire("live-chart:momentum", Duration.ofSeconds(14400)) } returns true

        underTest.append(233, LiveIndicatorType.MOMENTUM, "Home FC", "Away FC", secondPoint)

        verify { valueOps.set("live-chart:momentum", expected) }
    }

    @Test
    fun shouldAddANewFixtureEntryAlongsideExistingOnesForTheSameIndicator() {
        val existingPoint = LiveChartPoint(10, 20, Instant.now().minusSeconds(60))
        val newPoint = LiveChartPoint(5, 0, Instant.now())
        val existing =
            LiveIndicatorFixtures(listOf(LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(existingPoint))))
        val expected = LiveIndicatorFixtures(
            listOf(
                LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(existingPoint)),
                LiveIndicatorFixture(234, "Other Home", "Other Away", listOf(newPoint))
            )
        )

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns existing
        every { valueOps.set("live-chart:momentum", expected) } just Runs
        every { redisTemplate.expire("live-chart:momentum", Duration.ofSeconds(14400)) } returns true

        underTest.append(234, LiveIndicatorType.MOMENTUM, "Other Home", "Other Away", newPoint)

        verify { valueOps.set("live-chart:momentum", expected) }
    }

    @Test
    fun shouldDropFixtureEntriesStaleFixtures() {
        val staleEntry = LiveIndicatorFixture(
            111, "Stale Home", "Stale Away",
            listOf(LiveChartPoint(80, 10, Instant.now().minus(Duration.ofHours(5))))
        )
        val freshPoint = LiveChartPoint(10, 20, Instant.now().minus(Duration.ofHours(1)))
        val freshEntry = LiveIndicatorFixture(234, "Other Home", "Other Away", listOf(freshPoint))

        val newPoint = LiveChartPoint(15, 25, Instant.now())
        val existing = LiveIndicatorFixtures(listOf(staleEntry, freshEntry))
        val expected = LiveIndicatorFixtures(
            listOf(LiveIndicatorFixture(234, "Other Home", "Other Away", listOf(freshPoint, newPoint)))
        )

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns existing
        every { valueOps.set("live-chart:momentum", expected) } just Runs
        every { redisTemplate.expire("live-chart:momentum", Duration.ofSeconds(14400)) } returns true

        underTest.append(234, LiveIndicatorType.MOMENTUM, "Other Home", "Other Away", newPoint)

        verify { valueOps.set("live-chart:momentum", expected) }
    }

    @Test
    fun shouldReturnTheFixtureEntryForAGivenIndicator() {
        val point = LiveChartPoint(10, 20, Instant.parse("2026-06-22T10:00:00Z"))
        val entry = LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(point))

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns LiveIndicatorFixtures(listOf(entry))

        val result = underTest.getFixture(233, LiveIndicatorType.MOMENTUM)

        assertThat(result).isEqualTo(entry)
    }

    @Test
    fun shouldReturnNullWhenTheFixtureHasNoEntryYet() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns null

        val result = underTest.getFixture(233, LiveIndicatorType.MOMENTUM)

        assertThat(result).isNull()
    }

    @Test
    fun shouldReturnAllFixtureEntriesForAGivenIndicator() {
        val firstEntry =
            LiveIndicatorFixture(
                233,
                "Home FC",
                "Away FC",
                listOf(LiveChartPoint(10, 20, Instant.parse("2026-06-22T10:00:00Z")))
            )
        val secondEntry =
            LiveIndicatorFixture(
                234,
                "Other Home",
                "Other Away",
                listOf(LiveChartPoint(5, -10, Instant.parse("2026-06-22T10:05:00Z")))
            )

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns LiveIndicatorFixtures(listOf(firstEntry, secondEntry))

        val result = underTest.getAllFixtures(LiveIndicatorType.MOMENTUM)

        assertThat(result).containsExactly(firstEntry, secondEntry)
    }

    @Test
    fun shouldReturnAnEmptyListWhenTheIndicatorHasNoDataYet() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("live-chart:momentum") } returns null

        val result = underTest.getAllFixtures(LiveIndicatorType.MOMENTUM)

        assertThat(result).isEmpty()
    }
}
