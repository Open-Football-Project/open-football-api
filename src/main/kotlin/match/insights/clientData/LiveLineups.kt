package match.insights.clientData

import java.io.Serializable

data class LineupTeam(
    val team: LineupTeamInfo,
    val coach: LineupCoach?,
    val formation: String?,
    val startXI: List<LineupPlayerWrapper>,
    val substitutes: List<LineupPlayerWrapper>
) : Serializable

data class LineupTeamInfo(
    val id: Int,
    val name: String,
    val logo: String
) : Serializable

data class LineupCoach(
    val id: Int?,
    val name: String?,
    val photo: String?
) : Serializable

data class LineupPlayerWrapper(
    val player: LineupPlayer
) : Serializable

data class LineupPlayer(
    val id: Int,
    val name: String,
    val number: Int?,
    val pos: String,
    val grid: String?
) : Serializable
