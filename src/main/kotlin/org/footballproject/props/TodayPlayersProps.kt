package org.footballproject.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "today-players")
data class TodayPlayersProps(
    val ttlSeconds: Long = 129_600,
    val attackerMarkets: Set<String> = emptySet(),
    val midfieldMarkets: Set<String> = emptySet(),
    val goalkeeperMarkets: Set<String> = emptySet(),
    val defenderMarkets: Set<String> = emptySet(),
    val nonPlayerOddsValues: Set<String> = emptySet()
) {
    fun isNonPlayerOddsValue(value: String): Boolean =
        normalize(value) in nonPlayerOddsValues.map(::normalize) || overUnderLine.matches(value.trim())

    companion object {
        fun normalize(marketName: String): String =
            marketName.trim().lowercase().replace(Regex("\\s+"), "_")

        private val overUnderLine = Regex("(?i)^(over|under)\\s+\\d+(\\.\\d+)?$")
    }
}
