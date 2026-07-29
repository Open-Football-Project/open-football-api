package match.insights.datamanipulation.livecharts

import match.insights.clientData.LiveStatistic
import match.insights.clientData.LiveStatisticType
import match.insights.model.LiveChartPoint
import match.insights.model.MomentumBaseline
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class MomentumChartManipulationTest {

    private val underTest = MomentumChartManipulation()

    private val fixedNow = Instant.parse("2026-06-25T10:15:00Z")
    private val underTestWithFixedClock = MomentumChartManipulation(Clock.fixed(fixedNow, ZoneOffset.UTC))

    private val homeStats = listOf(
        LiveStatistic(LiveStatisticType.SHOTS_ON_GOAL.apiValue, 3),
        LiveStatistic(LiveStatisticType.SHOTS_OFF_GOAL.apiValue, 2),
        LiveStatistic(LiveStatisticType.SHOTS_INSIDEBOX.apiValue, 4),
        LiveStatistic(LiveStatisticType.CORNER_KICKS.apiValue, 1)
    )

    private val awayStats = listOf(
        LiveStatistic(LiveStatisticType.SHOTS_ON_GOAL.apiValue, 1),
        LiveStatistic(LiveStatisticType.SHOTS_OFF_GOAL.apiValue, 1),
        LiveStatistic(LiveStatisticType.SHOTS_INSIDEBOX.apiValue, 2),
        LiveStatistic(LiveStatisticType.CORNER_KICKS.apiValue, 0)
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
        homeMomentumScore = homeScore,
        awayMomentumScore = awayScore
    )

    @Test
    fun shouldReturnTheWeightedRawScoreForEachSide() {
        val snapshot = underTest.currentSnapShot(homeStats to awayStats, elapsedMinutes = 23)

        Assertions.assertThat(snapshot.homeScore).isEqualTo(20.0)
        Assertions.assertThat(snapshot.awayScore).isEqualTo(8.0)
    }

    @Test
    fun shouldMatchStatTypesCaseInsensitively() {
        val upperCaseHomeStats = listOf(
            LiveStatistic("SHOTS ON GOAL", 3),
            LiveStatistic("SHOTS OFF GOAL", 2),
            LiveStatistic("SHOTS INSIDEBOX", 4),
            LiveStatistic("CORNER KICKS", 1)
        )

        val snapshot = underTest.currentSnapShot(upperCaseHomeStats to awayStats, elapsedMinutes = 23)

        Assertions.assertThat(snapshot.homeScore).isEqualTo(20.0)
        Assertions.assertThat(snapshot.awayScore).isEqualTo(8.0)
    }

    @Test
    fun shouldTreatMissingStatTypesAsZero() {
        val partialHomeStats = listOf(LiveStatistic("Shots on Goal", 3))

        val snapshot = underTest.currentSnapShot(partialHomeStats to emptyList(), elapsedMinutes = 23)

        Assertions.assertThat(snapshot.homeScore).isEqualTo(9.0)
        Assertions.assertThat(snapshot.awayScore).isEqualTo(0.0)
    }

    @Test
    fun shouldIgnoreStatTypesNotPartOfTheMomentumFormula() {
        val homeStatsWithExtras = homeStats + LiveStatistic("Ball Possession", 60)
        val awayStatsWithExtras = awayStats + LiveStatistic("Ball Possession", 40)

        val snapshot = underTest.currentSnapShot(homeStatsWithExtras to awayStatsWithExtras, elapsedMinutes = 23)

        Assertions.assertThat(snapshot.homeScore).isEqualTo(20.0)
        Assertions.assertThat(snapshot.awayScore).isEqualTo(8.0)
    }

    @Test
    fun shouldCaptureTheSnapshotInstantUsingTheInjectedClock() {
        val snapshot = underTestWithFixedClock.currentSnapShot(homeStats to awayStats, elapsedMinutes = 23)

        Assertions.assertThat(snapshot.capturedAt).isEqualTo(fixedNow)
    }

    @Test
    fun shouldReturnTheCurrentSnapshotAsTheBaselineWhenThereIsNoPriorHistory() {
        val current = MomentumBaseline(9.0, 3.0, fixedNow, elapsedMinutes = 23)

        val baseline = underTest.previousSnapShot(emptyList(), current, 5)

        Assertions.assertThat(baseline).isEqualTo(current)
    }

    @Test
    fun shouldUseTheOldestAvailablePointAsBaselineWhenHistoryIsShorterThanTheWindow() {
        val oldestCapturedAt = Instant.parse("2026-06-25T09:55:00Z")
        val previousPoints = listOf(
            rawPoint(3.0, 1.0, oldestCapturedAt, minute = 50),
            rawPoint(6.0, 2.0, Instant.parse("2026-06-25T10:00:00Z"), minute = 55)
        )
        val current = MomentumBaseline(12.0, 4.0, Instant.parse("2026-06-25T10:05:00Z"), elapsedMinutes = 60)

        val baseline = underTest.previousSnapShot(previousPoints, current, 5)

        Assertions.assertThat(baseline).isEqualTo(MomentumBaseline(3.0, 1.0, oldestCapturedAt, elapsedMinutes = 50))
    }

    @Test
    fun shouldUseThePointFromExactlyWindowSizePollsBackOnceHistoryExceedsTheWindow() {
        val baselineCapturedAt = Instant.parse("2026-06-25T09:45:00Z")
        val previousPoints = listOf(
            rawPoint(0.0, 0.0, Instant.parse("2026-06-25T09:42:00Z"), minute = 39),
            rawPoint(5.0, 2.0, baselineCapturedAt, minute = 42),
            rawPoint(15.0, 6.0, Instant.parse("2026-06-25T09:48:00Z"), minute = 45),
            rawPoint(18.0, 7.0, Instant.parse("2026-06-25T09:51:00Z"), minute = 48),
            rawPoint(22.0, 9.0, Instant.parse("2026-06-25T09:54:00Z"), minute = 51),
            rawPoint(24.0, 9.0, Instant.parse("2026-06-25T09:57:00Z"), minute = 54)
        )
        val current = MomentumBaseline(25.0, 10.0, Instant.parse("2026-06-25T10:00:00Z"), elapsedMinutes = 57)

        val baseline = underTest.previousSnapShot(previousPoints, current, 5)

        Assertions.assertThat(baseline).isEqualTo(MomentumBaseline(5.0, 2.0, baselineCapturedAt, elapsedMinutes = 42))
    }

    @Test
    fun shouldBuildAChartPointCarryingTheValueAndTheRawScore() {
        val snapshot = MomentumBaseline(20.0, 8.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)

        val point = underTest.chartPoint(50, snapshot)

        Assertions.assertThat(point.minute).isEqualTo(23)
        Assertions.assertThat(point.value).isEqualTo(50)
        Assertions.assertThat(point.homeMomentumScore).isEqualTo(20.0)
        Assertions.assertThat(point.awayMomentumScore).isEqualTo(8.0)
    }

    @Test
    fun shouldReturnZeroWhenNeitherSideHasAnyActivityInTheWindow() {
        val value = underTest.oscillate(0.0, 0.0)

        Assertions.assertThat(value).isEqualTo(0)
    }

    @Test
    fun shouldReturnAPositiveValueWhenHomeHasMoreActivityThanAwayInTheWindow() {
        val value = underTest.oscillate(6.0, 2.0)

        Assertions.assertThat(value).isEqualTo(50)
    }

    @Test
    fun shouldReturnANegativeValueWhenAwayHasMoreActivityThanHomeInTheWindow() {
        val value = underTest.oscillate(2.0, 6.0)

        Assertions.assertThat(value).isEqualTo(-50)
    }

    @Test
    fun shouldReturnExactlyOneHundredWhenOnlyHomeHasActivity() {
        val value = underTest.oscillate(10.0, 0.0)

        Assertions.assertThat(value).isEqualTo(100)
    }

    @Test
    fun shouldReturnExactlyMinusOneHundredWhenOnlyAwayHasActivity() {
        val value = underTest.oscillate(0.0, 10.0)

        Assertions.assertThat(value).isEqualTo(-100)
    }

    @Test
    fun shouldClampANegativeHomeDeltaToZeroInsteadOfGoingBelowMinusOneHundred() {
        val value = underTest.oscillate(-5.0, 3.0)

        Assertions.assertThat(value).isEqualTo(-100)
    }

    @Test
    fun shouldClampANegativeAwayDeltaToZeroInsteadOfExceedingOneHundred() {
        val value = underTest.oscillate(3.0, -5.0)

        Assertions.assertThat(value).isEqualTo(100)
    }

    @Test
    fun shouldStayNeutralWhenAProviderCorrectionDropsBothTeamsScoresBetweenPolls() {
        val value = underTest.oscillate(-4.0, -9.0)

        Assertions.assertThat(value).isEqualTo(0)
    }

    @Test
    fun shouldNeverProduceAValueOutsideMinusOneHundredToOneHundredForAnyCombinationOfDeltas() {
        val combinations = listOf(
            -50.0 to 80.0,
            80.0 to -50.0,
            -1.0 to -1.0,
            0.0 to -100.0,
            -100.0 to 0.0
        )

        combinations.forEach { (homeDelta, awayDelta) ->
            val value = underTest.oscillate(homeDelta, awayDelta)
            Assertions.assertThat(value).isBetween(-100, 100)
        }
    }

    @Test
    fun shouldProduceASmallerMagnitudeValueForAnIsolatedShotThanForAComparableFlurryAtTheSameElapsedTime() {
        val baselineCapturedAt = fixedNow.minusSeconds(15 * 60)

        val isolatedShot = underTestWithFixedClock.confidenceScaledOscillate(3.0, 0.0, baselineCapturedAt)
        val flurry = underTestWithFixedClock.confidenceScaledOscillate(15.0, 0.0, baselineCapturedAt)

        Assertions.assertThat(isolatedShot).isLessThan(flurry)
    }

    @Test
    fun shouldMatchTheUnscaledOscillateValueWhenActivityMeetsOrExceedsTheLeagueAverageRate() {
        val baselineCapturedAt = fixedNow.minusSeconds(10 * 60)

        val value = underTestWithFixedClock.confidenceScaledOscillate(6.0, 4.0, baselineCapturedAt)

        Assertions.assertThat(value).isEqualTo(underTest.oscillate(6.0, 4.0))
    }

    @Test
    fun shouldStillProduceZeroOnTheFirstCaptureWhenBothDeltasAreZero() {
        val baselineCapturedAt = fixedNow.minusSeconds(15 * 60)

        val value = underTestWithFixedClock.confidenceScaledOscillate(0.0, 0.0, baselineCapturedAt)

        Assertions.assertThat(value).isEqualTo(0)
    }

    @Test
    fun shouldCapConfidenceAtOneEvenWhenActivityFarExceedsTheLeagueAverageRate() {
        val baselineCapturedAt = fixedNow.minusSeconds(5 * 60)

        val confidence = underTestWithFixedClock.confidenceFactor(200.0, 100.0, baselineCapturedAt)

        Assertions.assertThat(confidence).isEqualTo(1.0)
    }

    @Test
    fun shouldNeverReturnAConfidenceFactorOutsideZeroToOne() {
        val baselineCapturedAt = fixedNow.minusSeconds(20 * 60)
        val combinations = listOf(
            0.0 to 0.0,
            1.0 to 0.0,
            50.0 to 50.0,
            500.0 to 500.0,
            0.0 to 200.0
        )

        combinations.forEach { (homeDelta, awayDelta) ->
            val confidence = underTestWithFixedClock.confidenceFactor(homeDelta, awayDelta, baselineCapturedAt)
            Assertions.assertThat(confidence).isBetween(0.0, 1.0)
        }
    }
}
