package match.insights.datamanipulation.livecharts

import match.insights.clientData.LiveStatistic
import match.insights.clientData.LiveStatisticType
import match.insights.model.IndicatorBaseline
import match.insights.model.LiveChartPoint
import match.insights.model.MomentumBaseline
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Component
class MomentumChartManipulation(private val clock: Clock = Clock.systemUTC()) : CommonIndicator {

    override fun currentSnapShot(
        homeAwayStats: Pair<List<LiveStatistic>, List<LiveStatistic>>,
        elapsedMinutes: Int
    ): MomentumBaseline {
        return MomentumBaseline(
            momentumScore(homeAwayStats.first),
            momentumScore(homeAwayStats.second),
            elapsedMinutes = elapsedMinutes,
            capturedAt = Instant.now(clock)
        )
    }

    override fun previousSnapShot(
        previousPoints: List<LiveChartPoint>,
        current: IndicatorBaseline,
        windowSize: Int
    ): MomentumBaseline {
        if (previousPoints.isEmpty()) return current as MomentumBaseline
        val point = previousPoints[baselineIndex(previousPoints.size, windowSize)]
        return MomentumBaseline(
            point.homeMomentumScore,
            point.awayMomentumScore,
            capturedAt = point.capturedAt,
            elapsedMinutes = point.minute
        )
    }

    override fun chartPoint(value: Int, snapshot: IndicatorBaseline): LiveChartPoint {
        val momentumSnapshot = snapshot as MomentumBaseline
        return LiveChartPoint(
            momentumSnapshot.elapsedMinutes,
            value,
            momentumSnapshot.capturedAt,
            momentumSnapshot.homeScore,
            momentumSnapshot.awayScore
        )
    }

    fun oscillate(homeDelta: Double, awayDelta: Double): Int {
        val home = maxOf(0.0, homeDelta)
        val away = maxOf(0.0, awayDelta)
        val total = home + away
        if (total == 0.0) return 0
        return Math.round((home - away) / total * 100).toInt()
    }

    fun confidenceFactor(homeDelta: Double, awayDelta: Double, baselineCapturedAt: Instant): Double {
        val total = maxOf(0.0, homeDelta) + maxOf(0.0, awayDelta)
        val elapsedMinutes = Duration.between(baselineCapturedAt, Instant.now(clock)).toMillis() / MILLIS_PER_MINUTE
        val expectedActivity = (EXPECTED_ACTIVITY_PER_90_MIN / MATCH_LENGTH_MINUTES) * elapsedMinutes
        return minOf(1.0, total / expectedActivity)
    }

    fun confidenceScaledOscillate(homeDelta: Double, awayDelta: Double, baselineCapturedAt: Instant): Int =
        Math.round(
            oscillate(homeDelta, awayDelta) * confidenceFactor(homeDelta, awayDelta, baselineCapturedAt)
        ).toInt()


    private fun momentumScore(stats: List<LiveStatistic>): Double =
        statValue(stats, LiveStatisticType.SHOTS_ON_GOAL) * TOP_MOMENTUM_WEIGHT +
                statValue(stats, LiveStatisticType.SHOTS_INSIDEBOX) * MEDIUM_MOMENTUM_WEIGHT +
                statValue(stats, LiveStatisticType.SHOTS_OFF_GOAL) * LOW_MOMENTUM_WEIGHT +
                statValue(stats, LiveStatisticType.CORNER_KICKS) * LOW_MOMENTUM_WEIGHT

    companion object {
        val TOP_MOMENTUM_WEIGHT = 3
        val MEDIUM_MOMENTUM_WEIGHT = 2
        val LOW_MOMENTUM_WEIGHT = 1

        private const val MILLIS_PER_MINUTE = 60_000.0
        private const val MATCH_LENGTH_MINUTES = 90.0

        // Combined (both teams) bootstrap estimate, not measured data
        private const val AVERAGE_SHOTS_ON_GOAL_PER_MATCH = 6.0
        private const val AVERAGE_SHOTS_INSIDE_BOX_PER_MATCH = 8.0
        private const val AVERAGE_SHOTS_OFF_GOAL_PER_MATCH = 6.0
        private const val AVERAGE_CORNERS_PER_MATCH = 9.0

        val EXPECTED_ACTIVITY_PER_90_MIN =
            AVERAGE_SHOTS_ON_GOAL_PER_MATCH * TOP_MOMENTUM_WEIGHT +
                    AVERAGE_SHOTS_INSIDE_BOX_PER_MATCH * MEDIUM_MOMENTUM_WEIGHT +
                    AVERAGE_SHOTS_OFF_GOAL_PER_MATCH * LOW_MOMENTUM_WEIGHT +
                    AVERAGE_CORNERS_PER_MATCH * LOW_MOMENTUM_WEIGHT
    }
}
