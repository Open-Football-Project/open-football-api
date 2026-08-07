package org.footballproject.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tracking")
data class Tracking(val trackedLeagueIds: List<Int> = emptyList())
