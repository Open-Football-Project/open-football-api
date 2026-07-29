package match.insights.datamanipulation.livecharts

import match.insights.clientData.LiveStatistic
import match.insights.clientData.LiveStatisticType
import match.insights.clientData.statisticType

fun statValue(stats: List<LiveStatistic>, type: LiveStatisticType): Double =
    stats.firstOrNull { it.statisticType == type }
        ?.let { numericValue(it.value) } ?: 0.0

fun numericValue(value: Any?): Double = when (value) {
    is Number -> value.toDouble()
    is String -> value.trim().removeSuffix("%").toDoubleOrNull() ?: 0.0
    else -> 0.0
}

fun normalize(home: Double, away: Double): Pair<Int, Int> {
    val total = home + away
    if (total == 0.0) return 50 to 50
    val homePercent = Math.round(home / total * 100).toInt()
    return homePercent to (100 - homePercent)
}

fun baselineIndex(totalPoints: Int, windowSize: Int) = maxOf(0, totalPoints - windowSize)