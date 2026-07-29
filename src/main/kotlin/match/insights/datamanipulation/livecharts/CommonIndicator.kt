package match.insights.datamanipulation.livecharts

import match.insights.clientData.LiveStatistic
import match.insights.model.IndicatorBaseline
import match.insights.model.LiveChartPoint

interface CommonIndicator {
    fun currentSnapShot(
        homeAwayStats: Pair<List<LiveStatistic>, List<LiveStatistic>>,
        elapsedMinutes: Int
    ): IndicatorBaseline

    fun previousSnapShot(
        previousPoints: List<LiveChartPoint>,
        current: IndicatorBaseline,
        windowSize: Int,
    ): IndicatorBaseline

    fun chartPoint(
        value: Int,
        snapshot: IndicatorBaseline
    ): LiveChartPoint
}