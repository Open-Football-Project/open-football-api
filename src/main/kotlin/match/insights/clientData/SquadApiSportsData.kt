package match.insights.clientData

import java.io.Serializable

data class SquadResponse(
    val team: SquadTeam,
    val players: List<SquadPlayer>
) : Serializable

data class SquadTeam(
    val id: Int,
    val name: String,
    val logo: String
) : Serializable

data class SquadPlayer(
    val id: Int?,
    val name: String,
    val age: Int?,
    val number: Int?,
    val position: String?,
    val photo: String?
) : Serializable
