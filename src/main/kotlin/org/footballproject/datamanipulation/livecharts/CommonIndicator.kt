package org.footballproject.datamanipulation.livecharts

import org.footballproject.clientData.LiveStatistic
import org.footballproject.model.IndicatorBaseline
import org.footballproject.model.LiveChartPoint

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