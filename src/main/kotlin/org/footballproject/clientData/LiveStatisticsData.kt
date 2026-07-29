package org.footballproject.clientData

import java.io.Serializable


data class LiveTeamStats(
    val team: LiveStatsTeamInfo,
    val statistics: List<LiveStatistic>
) : Serializable

data class LiveStatsTeamInfo(
    val id: Int,
    val name: String,
    val logo: String
) : Serializable

data class LiveStatistic(
    val type: String,
    val value: Any?
) : Serializable
