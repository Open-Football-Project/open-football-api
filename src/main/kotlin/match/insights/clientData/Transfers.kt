package match.insights.clientData

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PlayerTransfer(
    val player: TransferPlayer,
    val update: String?,
    val transfers: List<Transfer>
) : Serializable

data class TransferPlayer(
    val id: Int,
    val name: String,
    val photo: String?
) : Serializable

data class Transfer(
    val date: String?,
    val type: String?,
    val teams: TransferTeams
) : Serializable

data class TransferTeams(
    @JsonProperty("in")
    val teamTo: TransferTeam,      // destination team
    @JsonProperty("out")
    val teamFrom: TransferTeam
) : Serializable

data class TransferTeam(
    val id: Int?,
    val name: String,
    val logo: String?
) : Serializable
