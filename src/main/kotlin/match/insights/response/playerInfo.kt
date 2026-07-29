package match.insights.response

import match.insights.model.VideoContent

data class PlayerHistory(
    val player: PlayerHistoryInfo?,
    val trophies: List<PlayerTrophyInfo>,
    val transfers: List<PlayerTransferInfo>
)

data class PlayerHistoryInfo(
    val id: Int,
    val name: String,
    val photo: String?
)

data class PlayerTrophyInfo(
    val league: String,
    val country: String,
    val season: String?,
    val place: String
)

data class PlayerTransferInfo(
    val date: String?,
    val fromTeamId: Int?,
    val fromTeamName: String,
    val fromTeamLogo: String?,
    val toTeamId: Int?,
    val toTeamName: String,
    val toTeamLogo: String?,
)


data class PlayerMainInfo(
    val playerId: Int = -1,
    val name: String = "Unknown",
    val age: Int = -1,
    val nationality: String = "Unknown",
    val position: String = "Unknown",
    val height: String = "Unknown",
    val weight: String = "Unknown",
    val teamId: Int = -1,
    val teamName: String = "Unknown",
    val teamLogo: String? = null,
    val photo: String? = null,
    val injured: Boolean = false,
    val videos: Set<VideoContent>
)