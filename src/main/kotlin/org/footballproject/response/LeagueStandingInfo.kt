package org.footballproject.response

import org.footballproject.model.VideoContent

data class LeagueInfo(
    val id: Int,
    val name: String? = "Unknown League",
    val country: String? = "Unknown Country",
    val logo: String? = "Unknown Logo",
    val flag: String? = "Unknown Flag",
    val season: Int,
    val group: List<LeagueGroup>,
    val videos: Set<VideoContent>
)


data class LeagueGroup(
    val label: String? = "Default",
    val teams: List<LeagueTeamInfo>
)

data class LeagueTeamInfo(
    val teamId: Int,
    val rank: Int,
    val teamName: String,
    val logo: String,
    val points: Int,
    val played: Int,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val form: String,
    val update: String?
)