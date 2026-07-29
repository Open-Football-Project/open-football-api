package org.footballproject.datamanipulation.livecharts

import org.footballproject.clientData.LiveStatistic
import org.footballproject.clientData.LiveStatisticType
import org.footballproject.model.GoalThreatBaseline
import org.footballproject.model.LiveChartPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class GoalThreatChartManipulationTest {

    private val underTest = GoalThreatChartManipulation()

    private val fixedNow = Instant.parse("2026-06-25T10:15:00Z")
    private val underTestWithFixedClock = GoalThreatChartManipulation(Clock.fixed(fixedNow, ZoneOffset.UTC))

    private val homeStats = listOf(
        LiveStatistic(LiveStatisticType.EXPECTED_GOALS.apiValue, "1.5"),
        LiveStatistic(LiveStatisticType.SHOTS_ON_GOAL.apiValue, 4),
        LiveStatistic(LiveStatisticType.SHOTS_INSIDEBOX.apiValue, 3)
    )

    private val awayStats = listOf(
        LiveStatistic(LiveStatisticType.EXPECTED_GOALS.apiValue, "0.8"),
        LiveStatistic(LiveStatisticType.SHOTS_ON_GOAL.apiValue, 2),
        LiveStatistic(LiveStatisticType.SHOTS_INSIDEBOX.apiValue, 1)
    )

    private fun rawPoint(
        homeScore: Double,
        awayScore: Double,
        capturedAt: Instant = Instant.parse("2026-06-22T10:00:00Z"),
        minute: Int = 0
    ) = LiveChartPoint(
        minute = minute,
        value = 0,
        capturedAt = capturedAt,
        homeGoalThreatScore = homeScore,
        awayGoalThreatScore = awayScore
    )

    @Test
    fun shouldReturnTheWeightedRawScoreForEachSide() {
        val snapshot = underTest.currentSnapShot(homeStats to awayStats, elapsedMinutes = 23)

        assertThat(snapshot.homeScore).isEqualTo(30.0)
        assertThat(snapshot.awayScore).isEqualTo(15.0)
    }

    @Test
    fun shouldParseExpectedGoalsAsANumericStringRatherThanAssumingItIsAlreadyANumber() {
        val statsWithStringExpectedGoals = listOf(LiveStatistic(LiveStatisticType.EXPECTED_GOALS.apiValue, "1.72"))

        val snapshot = underTest.currentSnapShot(statsWithStringExpectedGoals to emptyList(), elapsedMinutes = 23)

        assertThat(snapshot.homeScore).isEqualTo(17.2)
    }

    @Test
    fun shouldMatchStatTypesCaseInsensitively() {
        val upperCaseHomeStats = listOf(
            LiveStatistic("EXPECTED_GOALS", "1.5"),
            LiveStatistic("SHOTS ON GOAL", 4),
            LiveStatistic("SHOTS INSIDEBOX", 3)
        )

        val snapshot = underTest.currentSnapShot(upperCaseHomeStats to awayStats, elapsedMinutes = 23)

        assertThat(snapshot.homeScore).isEqualTo(30.0)
        assertThat(snapshot.awayScore).isEqualTo(15.0)
    }

    @Test
    fun shouldTreatMissingStatTypesAsZero() {
        val partialHomeStats = listOf(LiveStatistic(LiveStatisticType.SHOTS_ON_GOAL.apiValue, 4))

        val snapshot = underTest.currentSnapShot(partialHomeStats to emptyList(), elapsedMinutes = 23)

        assertThat(snapshot.homeScore).isEqualTo(12.0)
        assertThat(snapshot.awayScore).isEqualTo(0.0)
    }

    @Test
    fun shouldIgnoreStatTypesNotPartOfTheGoalThreatFormula() {
        val homeStatsWithExtras = homeStats + LiveStatistic("Ball Possession", 60)
        val awayStatsWithExtras = awayStats + LiveStatistic("Ball Possession", 40)

        val snapshot = underTest.currentSnapShot(homeStatsWithExtras to awayStatsWithExtras, elapsedMinutes = 23)

        assertThat(snapshot.homeScore).isEqualTo(30.0)
        assertThat(snapshot.awayScore).isEqualTo(15.0)
    }

    @Test
    fun shouldCaptureTheSnapshotInstantUsingTheInjectedClock() {
        val snapshot = underTestWithFixedClock.currentSnapShot(homeStats to awayStats, elapsedMinutes = 23)

        assertThat(snapshot.capturedAt).isEqualTo(fixedNow)
    }

    @Test
    fun shouldReturnTheCurrentSnapshotAsTheBaselineWhenThereIsNoPriorHistory() {
        val current = GoalThreatBaseline(30.0, 15.0, fixedNow, elapsedMinutes = 23)

        val baseline = underTest.previousSnapShot(emptyList(), current, windowSize = 5)

        assertThat(baseline).isEqualTo(current)
    }

    @Test
    fun shouldUseTheOldestAvailablePointAsBaselineWhenHistoryIsShorterThanTheWindow() {
        val oldestCapturedAt = Instant.parse("2026-06-25T09:55:00Z")
        val previousPoints = listOf(
            rawPoint(5.0, 2.0, oldestCapturedAt, minute = 50),
            rawPoint(10.0, 4.0, Instant.parse("2026-06-25T10:00:00Z"), minute = 55)
        )
        val current = GoalThreatBaseline(20.0, 8.0, Instant.parse("2026-06-25T10:05:00Z"), elapsedMinutes = 60)

        val baseline = underTest.previousSnapShot(previousPoints, current, windowSize = 5)

        assertThat(baseline).isEqualTo(GoalThreatBaseline(5.0, 2.0, oldestCapturedAt, elapsedMinutes = 50))
    }

    @Test
    fun shouldUseThePointFromExactlyWindowSizePollsBackOnceHistoryExceedsTheWindow() {
        val baselineCapturedAt = Instant.parse("2026-06-25T09:45:00Z")
        val previousPoints = listOf(
            rawPoint(0.0, 0.0, Instant.parse("2026-06-25T09:42:00Z"), minute = 39),
            rawPoint(10.0, 4.0, baselineCapturedAt, minute = 42),
            rawPoint(18.0, 7.0, Instant.parse("2026-06-25T09:48:00Z"), minute = 45),
            rawPoint(22.0, 9.0, Instant.parse("2026-06-25T09:51:00Z"), minute = 48),
            rawPoint(26.0, 11.0, Instant.parse("2026-06-25T09:54:00Z"), minute = 51),
            rawPoint(28.0, 12.0, Instant.parse("2026-06-25T09:57:00Z"), minute = 54)
        )
        val current = GoalThreatBaseline(30.0, 15.0, Instant.parse("2026-06-25T10:00:00Z"), elapsedMinutes = 57)

        val baseline = underTest.previousSnapShot(previousPoints, current, windowSize = 5)

        assertThat(baseline).isEqualTo(GoalThreatBaseline(10.0, 4.0, baselineCapturedAt, elapsedMinutes = 42))
    }

    @Test
    fun shouldBuildAChartPointCarryingTheValueAndTheRawScore() {
        val snapshot = GoalThreatBaseline(30.0, 15.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)

        val point = underTest.chartPoint(69, snapshot)

        assertThat(point.minute).isEqualTo(23)
        assertThat(point.value).isEqualTo(69)
        assertThat(point.homeGoalThreatScore).isEqualTo(30.0)
        assertThat(point.awayGoalThreatScore).isEqualTo(15.0)
    }

    @Test
    fun shouldReturnZeroForBothSidesWhenThereIsNoActivitySinceBaseline() {
        val snapshot = GoalThreatBaseline(30.0, 15.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)

        val (home, away) = underTest.delta(snapshot, snapshot)

        assertThat(home).isEqualTo(0.0)
        assertThat(away).isEqualTo(0.0)
    }

    @Test
    fun shouldReturnTheIndependentScoreIncreaseForEachSideSinceTheBaseline() {
        val baseline = GoalThreatBaseline(5.0, 2.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 10)
        val current = GoalThreatBaseline(20.0, 8.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)

        val (home, away) = underTest.delta(current, baseline)

        assertThat(home).isEqualTo(15.0)
        assertThat(away).isEqualTo(6.0)
    }

    @Test
    fun shouldClampANegativeHomeDeltaToZeroWhenAProviderCorrectionLowersTheHomeScore() {
        val baseline = GoalThreatBaseline(20.0, 5.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 10)
        val current = GoalThreatBaseline(15.0, 9.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)

        val (home, away) = underTest.delta(current, baseline)

        assertThat(home).isEqualTo(0.0)
        assertThat(away).isEqualTo(4.0)
    }

    @Test
    fun shouldClampANegativeAwayDeltaToZeroWhenAProviderCorrectionLowersTheAwayScore() {
        val baseline = GoalThreatBaseline(5.0, 20.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 10)
        val current = GoalThreatBaseline(9.0, 15.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)

        val (home, away) = underTest.delta(current, baseline)

        assertThat(home).isEqualTo(4.0)
        assertThat(away).isEqualTo(0.0)
    }
}
