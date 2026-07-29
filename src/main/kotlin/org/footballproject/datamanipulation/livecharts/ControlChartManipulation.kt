package org.footballproject.datamanipulation.livecharts

import org.footballproject.clientData.LiveStatistic
import org.footballproject.clientData.LiveStatisticType
import org.footballproject.model.ControlBaseline
import org.footballproject.model.IndicatorBaseline
import org.footballproject.model.LiveChartPoint
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class ControlChartManipulation(private val clock: Clock = Clock.systemUTC()) : CommonIndicator {

    override fun currentSnapShot(
        homeAwayStats: Pair<List<LiveStatistic>, List<LiveStatistic>>,
        elapsedMinutes: Int
    ): ControlBaseline {
        val (homeStats, awayStats) = homeAwayStats
        return ControlBaseline(
            homePossessionPercent = statValue(homeStats, LiveStatisticType.BALL_POSSESSION),
            awayPossessionPercent = statValue(awayStats, LiveStatisticType.BALL_POSSESSION),
            homeTotalPasses = statValue(homeStats, LiveStatisticType.TOTAL_PASSES),
            awayTotalPasses = statValue(awayStats, LiveStatisticType.TOTAL_PASSES),
            homeAccuratePasses = statValue(homeStats, LiveStatisticType.PASSES_ACCURATE),
            awayAccuratePasses = statValue(awayStats, LiveStatisticType.PASSES_ACCURATE),
            elapsedMinutes = elapsedMinutes,
            capturedAt = Instant.now(clock)
        )
    }

    override fun previousSnapShot(
        previousPoints: List<LiveChartPoint>,
        current: IndicatorBaseline,
        windowSize: Int
    ): ControlBaseline {
        if (previousPoints.isEmpty()) return current as ControlBaseline
        val point = previousPoints[baselineIndex(previousPoints.size, windowSize)]
        return ControlBaseline(
            homePossessionPercent = point.homePossessionPercent,
            awayPossessionPercent = point.awayPossessionPercent,
            homeTotalPasses = point.homeTotalPasses,
            awayTotalPasses = point.awayTotalPasses,
            homeAccuratePasses = point.homeAccuratePasses,
            awayAccuratePasses = point.awayAccuratePasses,
            elapsedMinutes = point.minute,
            capturedAt = point.capturedAt
        )
    }

    override fun chartPoint(value: Int, snapshot: IndicatorBaseline): LiveChartPoint {
        val controlSnapshot = snapshot as ControlBaseline
        return LiveChartPoint(
            minute = controlSnapshot.elapsedMinutes,
            value = value,
            capturedAt = controlSnapshot.capturedAt,
            homePossessionPercent = controlSnapshot.homePossessionPercent,
            awayPossessionPercent = controlSnapshot.awayPossessionPercent,
            homeTotalPasses = controlSnapshot.homeTotalPasses,
            awayTotalPasses = controlSnapshot.awayTotalPasses,
            homeAccuratePasses = controlSnapshot.homeAccuratePasses,
            awayAccuratePasses = controlSnapshot.awayAccuratePasses
        )
    }

    fun windowedPossession(current: ControlBaseline, baseline: ControlBaseline): Pair<Double, Double> {
        val elapsedDelta = current.elapsedMinutes - baseline.elapsedMinutes
        if (elapsedDelta <= 0) return current.homePossessionPercent to current.awayPossessionPercent

        val home = (current.homePossessionPercent * current.elapsedMinutes -
                baseline.homePossessionPercent * baseline.elapsedMinutes) / elapsedDelta
        val away = (current.awayPossessionPercent * current.elapsedMinutes -
                baseline.awayPossessionPercent * baseline.elapsedMinutes) / elapsedDelta

        return home.coerceIn(0.0, 100.0) to away.coerceIn(0.0, 100.0)
    }

    fun windowedPassPercentage(current: ControlBaseline, baseline: ControlBaseline): Pair<Double, Double> {
        val home = windowedRatio(
            current.homeAccuratePasses, current.homeTotalPasses,
            baseline.homeAccuratePasses, baseline.homeTotalPasses
        )
        val away = windowedRatio(
            current.awayAccuratePasses, current.awayTotalPasses,
            baseline.awayAccuratePasses, baseline.awayTotalPasses
        )
        return home to away
    }

    fun weightedScore(possession: Pair<Double, Double>, passPercentage: Pair<Double, Double>): Pair<Double, Double> {
        val home = possession.first * POSSESSION_WEIGHT + passPercentage.first * PASS_WEIGHT
        val away = possession.second * POSSESSION_WEIGHT + passPercentage.second * PASS_WEIGHT
        return home to away
    }

    private fun windowedRatio(
        currentAccurate: Double,
        currentTotal: Double,
        baselineAccurate: Double,
        baselineTotal: Double
    ): Double {
        val totalDelta = currentTotal - baselineTotal
        if (totalDelta <= 0.0) {
            return if (currentTotal <= 0.0) 0.0 else currentAccurate / currentTotal * 100
        }
        return (currentAccurate - baselineAccurate) / totalDelta * 100
    }

    companion object {
        const val POSSESSION_WEIGHT = 0.7
        const val PASS_WEIGHT = 0.3
    }
}
