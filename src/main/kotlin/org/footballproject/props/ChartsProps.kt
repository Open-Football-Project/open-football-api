package org.footballproject.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "charts")
data class ChartsProps(
    val ttlSeconds: Long,
    val pollingMilli: Long,
    val schedulingEnabled: Boolean,
    val momentumWindowSize: Int = 5,
    val controlWindowSize: Int = 5,
    val goalThreatWindowSize: Int = 5,
    val idleBackoffMilli: Long = 1_800_000,
    val activeWindowMilli: Long = 7_800_000,
    val oddsPollingMilli: Long = 300_000,
    val oddsTrackedMarketNames: Set<String> = emptySet()
)