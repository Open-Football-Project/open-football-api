package org.footballproject.datamanipulation.livecharts

import org.footballproject.clientData.LiveStatistic
import org.footballproject.clientData.LiveStatisticType
import org.footballproject.model.ControlBaseline
import org.footballproject.model.LiveChartPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ControlChartManipulationTest {

    private val underTest = ControlChartManipulation()

    private val fixedNow = Instant.parse("2026-06-25T10:15:00Z")
    private val underTestWithFixedClock = ControlChartManipulation(Clock.fixed(fixedNow, ZoneOffset.UTC))

    private val homeStats = listOf(
        LiveStatistic(LiveStatisticType.BALL_POSSESSION.apiValue, "58%"),
        LiveStatistic(LiveStatisticType.TOTAL_PASSES.apiValue, 120),
        LiveStatistic(LiveStatisticType.PASSES_ACCURATE.apiValue, 96)
    )

    private val awayStats = listOf(
        LiveStatistic(LiveStatisticType.BALL_POSSESSION.apiValue, "42%"),
        LiveStatistic(LiveStatisticType.TOTAL_PASSES.apiValue, 80),
        LiveStatistic(LiveStatisticType.PASSES_ACCURATE.apiValue, 56)
    )

    private fun controlPoint(
        homePossessionPercent: Double,
        awayPossessionPercent: Double,
        homeTotalPasses: Double,
        awayTotalPasses: Double,
        homeAccuratePasses: Double,
        awayAccuratePasses: Double,
        minute: Int,
        capturedAt: Instant = Instant.parse("2026-06-22T10:00:00Z")
    ) = LiveChartPoint(
        minute = minute,
        value = 50,
        capturedAt = capturedAt,
        homePossessionPercent = homePossessionPercent,
        awayPossessionPercent = awayPossessionPercent,
        homeTotalPasses = homeTotalPasses,
        awayTotalPasses = awayTotalPasses,
        homeAccuratePasses = homeAccuratePasses,
        awayAccuratePasses = awayAccuratePasses
    )

    @Test
    fun shouldExtractPossessionAndPassCountsForBothSides() {
        val snapshot = underTestWithFixedClock.currentSnapShot(homeStats to awayStats, elapsedMinutes = 60)

        assertThat(snapshot).isEqualTo(
            ControlBaseline(
                homePossessionPercent = 58.0,
                awayPossessionPercent = 42.0,
                homeTotalPasses = 120.0,
                awayTotalPasses = 80.0,
                homeAccuratePasses = 96.0,
                awayAccuratePasses = 56.0,
                capturedAt = fixedNow,
                elapsedMinutes = 60
            )
        )
    }

    @Test
    fun shouldMatchStatTypesCaseInsensitively() {
        val upperCaseHomeStats = listOf(
            LiveStatistic("BALL POSSESSION", "58%"),
            LiveStatistic("TOTAL PASSES", 120),
            LiveStatistic("PASSES ACCURATE", 96)
        )

        val snapshot = underTest.currentSnapShot(upperCaseHomeStats to emptyList(), elapsedMinutes = 60)

        assertThat(snapshot.homePossessionPercent).isEqualTo(58.0)
        assertThat(snapshot.homeTotalPasses).isEqualTo(120.0)
        assertThat(snapshot.homeAccuratePasses).isEqualTo(96.0)
    }

    @Test
    fun shouldTreatMissingStatTypesAsZero() {
        val partialHomeStats = listOf(LiveStatistic(LiveStatisticType.BALL_POSSESSION.apiValue, "70%"))

        val snapshot = underTestWithFixedClock.currentSnapShot(partialHomeStats to emptyList(), elapsedMinutes = 10)

        assertThat(snapshot).isEqualTo(
            ControlBaseline(
                homePossessionPercent = 70.0,
                awayPossessionPercent = 0.0,
                homeTotalPasses = 0.0,
                awayTotalPasses = 0.0,
                homeAccuratePasses = 0.0,
                awayAccuratePasses = 0.0,
                capturedAt = fixedNow,
                elapsedMinutes = 10
            )
        )
    }

    @Test
    fun shouldCaptureTheSnapshotInstantUsingTheInjectedClock() {
        val snapshot = underTestWithFixedClock.currentSnapShot(homeStats to awayStats, elapsedMinutes = 60)

        assertThat(snapshot.capturedAt).isEqualTo(fixedNow)
    }

    @Test
    fun shouldReturnTheCurrentSnapshotAsTheBaselineWhenThereIsNoPriorHistory() {
        val current = ControlBaseline(
            60.0, 40.0, 100.0, 90.0, 80.0, 70.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 30
        )

        val baseline = underTest.previousSnapShot(emptyList(), current, windowSize = 5)

        assertThat(baseline).isEqualTo(current)
    }

    @Test
    fun shouldUseTheOldestAvailablePointAsBaselineWhenHistoryIsShorterThanTheWindow() {
        val oldestCapturedAt = Instant.parse("2026-06-25T09:55:00Z")
        val previousPoints = listOf(
            controlPoint(55.0, 45.0, 20.0, 18.0, 16.0, 14.0, minute = 5, capturedAt = oldestCapturedAt),
            controlPoint(56.0, 44.0, 40.0, 36.0, 32.0, 28.0, minute = 10)
        )
        val current = ControlBaseline(
            58.0, 42.0, 100.0, 90.0, 80.0, 70.0,
            capturedAt = Instant.parse("2026-06-25T10:05:00Z"), elapsedMinutes = 30
        )

        val baseline = underTest.previousSnapShot(previousPoints, current, windowSize = 5)

        assertThat(baseline).isEqualTo(
            ControlBaseline(55.0, 45.0, 20.0, 18.0, 16.0, 14.0, capturedAt = oldestCapturedAt, elapsedMinutes = 5)
        )
    }

    @Test
    fun shouldUseThePointFromExactlyWindowSizePollsBackOnceHistoryExceedsTheWindow() {
        val baselineCapturedAt = Instant.parse("2026-06-25T09:45:00Z")
        val previousPoints = listOf(
            controlPoint(40.0, 60.0, 10.0, 15.0, 8.0, 12.0, minute = 2),
            controlPoint(45.0, 55.0, 20.0, 25.0, 16.0, 20.0, minute = 5, capturedAt = baselineCapturedAt),
            controlPoint(48.0, 52.0, 30.0, 33.0, 24.0, 27.0, minute = 8),
            controlPoint(50.0, 50.0, 40.0, 42.0, 32.0, 34.0, minute = 11),
            controlPoint(52.0, 48.0, 50.0, 51.0, 40.0, 41.0, minute = 14),
            controlPoint(54.0, 46.0, 60.0, 60.0, 48.0, 48.0, minute = 17)
        )
        val current = ControlBaseline(
            58.0, 42.0, 100.0, 90.0, 80.0, 70.0,
            capturedAt = Instant.parse("2026-06-25T10:05:00Z"), elapsedMinutes = 30
        )

        val baseline = underTest.previousSnapShot(previousPoints, current, windowSize = 5)

        assertThat(baseline).isEqualTo(
            ControlBaseline(45.0, 55.0, 20.0, 25.0, 16.0, 20.0, capturedAt = baselineCapturedAt, elapsedMinutes = 5)
        )
    }

    @Test
    fun shouldRecoverTheWindowedPossessionShareFromTwoCumulativeSnapshots() {
        val baseline = ControlBaseline(50.0, 50.0, 0.0, 0.0, 0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 40)
        val current = ControlBaseline(55.0, 45.0, 0.0, 0.0, 0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 60)

        val (home, away) = underTest.windowedPossession(current, baseline)

        assertThat(home).isEqualTo(65.0)
        assertThat(away).isEqualTo(35.0)
    }

    @Test
    fun shouldReturnTheCurrentPossessionDirectlyWhenNoTimeHasPassedSinceBaseline() {
        val current = ControlBaseline(58.0, 42.0, 0.0, 0.0, 0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 30)

        val (home, away) = underTest.windowedPossession(current, current)

        assertThat(home).isEqualTo(58.0)
        assertThat(away).isEqualTo(42.0)
    }

    @Test
    fun shouldClampAWindowedPossessionShareBelowZeroToZero() {
        val baseline = ControlBaseline(100.0, 0.0, 0.0, 0.0, 0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 80)
        val current = ControlBaseline(0.0, 100.0, 0.0, 0.0, 0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 81)

        val (home, away) = underTest.windowedPossession(current, baseline)

        assertThat(home).isEqualTo(0.0)
        assertThat(away).isEqualTo(100.0)
    }

    @Test
    fun shouldRecoverTheWindowedPassAccuracyFromTwoCumulativeSnapshots() {
        val baseline = ControlBaseline(0.0, 0.0, homeTotalPasses = 40.0, awayTotalPasses = 30.0, homeAccuratePasses = 32.0, awayAccuratePasses = 21.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 40)
        val current = ControlBaseline(0.0, 0.0, homeTotalPasses = 100.0, awayTotalPasses = 70.0, homeAccuratePasses = 86.0, awayAccuratePasses = 49.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 60)

        val (home, away) = underTest.windowedPassPercentage(current, baseline)

        assertThat(home).isEqualTo(90.0)
        assertThat(away).isEqualTo(70.0)
    }

    @Test
    fun shouldFallBackToTheCumulativePassPercentageWhenNoNewPassesHappenedInTheWindow() {
        val baseline = ControlBaseline(0.0, 0.0, homeTotalPasses = 50.0, awayTotalPasses = 20.0, homeAccuratePasses = 40.0, awayAccuratePasses = 10.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 40)
        val current = ControlBaseline(0.0, 0.0, homeTotalPasses = 50.0, awayTotalPasses = 20.0, homeAccuratePasses = 40.0, awayAccuratePasses = 10.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 60)

        val (home, away) = underTest.windowedPassPercentage(current, baseline)

        assertThat(home).isEqualTo(80.0)
        assertThat(away).isEqualTo(50.0)
    }

    @Test
    fun shouldFallBackToZeroWhenNoPassesHaveBeenRecordedAtAll() {
        val baseline = ControlBaseline(0.0, 0.0, homeTotalPasses = 0.0, awayTotalPasses = 0.0, homeAccuratePasses = 0.0, awayAccuratePasses = 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 0)
        val current = ControlBaseline(0.0, 0.0, homeTotalPasses = 0.0, awayTotalPasses = 0.0, homeAccuratePasses = 0.0, awayAccuratePasses = 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 1)

        val (home, away) = underTest.windowedPassPercentage(current, baseline)

        assertThat(home).isEqualTo(0.0)
        assertThat(away).isEqualTo(0.0)
    }

    @Test
    fun shouldCombinePossessionAndPassAccuracyUsingTheControlWeights() {
        val (home, away) = underTest.weightedScore(possession = 65.0 to 35.0, passPercentage = 90.0 to 70.0)

        assertThat(home).isEqualTo(72.5)
        assertThat(away).isEqualTo(45.5)
    }

    @Test
    fun shouldBuildAChartPointCarryingTheValueAndTheRawSnapshot() {
        val snapshot = ControlBaseline(
            58.0, 42.0, 100.0, 90.0, 80.0, 70.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 60
        )

        val point = underTest.chartPoint(65, snapshot)

        assertThat(point.minute).isEqualTo(60)
        assertThat(point.value).isEqualTo(65)
        assertThat(point.homePossessionPercent).isEqualTo(58.0)
        assertThat(point.awayPossessionPercent).isEqualTo(42.0)
        assertThat(point.homeTotalPasses).isEqualTo(100.0)
        assertThat(point.awayTotalPasses).isEqualTo(90.0)
        assertThat(point.homeAccuratePasses).isEqualTo(80.0)
        assertThat(point.awayAccuratePasses).isEqualTo(70.0)
    }
}
