package match.insights.datamanipulation.livecharts

import match.insights.clientData.LiveStatistic
import match.insights.clientData.LiveStatisticType
import match.insights.model.GoalThreatBaseline
import match.insights.model.IndicatorBaseline
import match.insights.model.LiveChartPoint
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class GoalThreatChartManipulation(private val clock: Clock = Clock.systemUTC()) : CommonIndicator {

    override fun currentSnapShot(
        homeAwayStats: Pair<List<LiveStatistic>, List<LiveStatistic>>,
        elapsedMinutes: Int
    ): GoalThreatBaseline {
        return GoalThreatBaseline(
            goalThreatScore(homeAwayStats.first),
            goalThreatScore(homeAwayStats.second),
            elapsedMinutes = elapsedMinutes,
            capturedAt = Instant.now(clock)
        )
    }

    override fun previousSnapShot(
        previousPoints: List<LiveChartPoint>,
        current: IndicatorBaseline,
        windowSize: Int
    ): GoalThreatBaseline {
        if (previousPoints.isEmpty()) return current as GoalThreatBaseline
        val point = previousPoints[baselineIndex(previousPoints.size, windowSize)]
        return GoalThreatBaseline(
            point.homeGoalThreatScore,
            point.awayGoalThreatScore,
            capturedAt = point.capturedAt,
            elapsedMinutes = point.minute
        )
    }

    override fun chartPoint(value: Int, snapshot: IndicatorBaseline): LiveChartPoint {
        val goalThreatSnapshot = snapshot as GoalThreatBaseline
        return LiveChartPoint(
            minute = goalThreatSnapshot.elapsedMinutes,
            value = value,
            capturedAt = goalThreatSnapshot.capturedAt,
            homeGoalThreatScore = goalThreatSnapshot.homeScore,
            awayGoalThreatScore = goalThreatSnapshot.awayScore
        )
    }

    fun delta(current: GoalThreatBaseline, baseline: GoalThreatBaseline): Pair<Double, Double> {
        val home = maxOf(0.0, current.homeScore - baseline.homeScore)
        val away = maxOf(0.0, current.awayScore - baseline.awayScore)
        return home to away
    }

    private fun goalThreatScore(stats: List<LiveStatistic>): Double =
        statValue(stats, LiveStatisticType.EXPECTED_GOALS) * TOP_GOAL_THREAT_WEIGHT +
                statValue(stats, LiveStatisticType.SHOTS_ON_GOAL) * MEDIUM_GOAL_THREAT_WEIGHT +
                statValue(stats, LiveStatisticType.SHOTS_INSIDEBOX) * LOW_GOAL_THREAT_WEIGHT

    companion object {
        const val TOP_GOAL_THREAT_WEIGHT = 10
        const val MEDIUM_GOAL_THREAT_WEIGHT = 3
        const val LOW_GOAL_THREAT_WEIGHT = 1
    }
}
