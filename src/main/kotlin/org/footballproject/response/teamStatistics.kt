package org.footballproject.response

class TwoTeamsStatistics(
    val teamA: TeamStats = TeamStats(),
    val teamB: TeamStats = TeamStats(),
)

class TeamStats(
    val teamId: Int = -1,
    val teamLogo: String = "",
    val teamName: String = "Unknown Team",
    val statistics: List<TeamStatistic> = emptyList()
)

class TeamStatistic(
    val name: String,
    val value: Int,
    val total: Int,
    val isPositive: Boolean
)

fun TwoTeamsStatistics.isEmpty(): Boolean {
    val aTeamEmpties = this.teamA.statistics.count { it.total == 0 && it.value == 0 }
    val bTeamEmpties = this.teamB.statistics.count { it.total == 0 && it.value == 0 }

    return aTeamEmpties == this.teamA.statistics.size && bTeamEmpties == this.teamB.statistics.size
}