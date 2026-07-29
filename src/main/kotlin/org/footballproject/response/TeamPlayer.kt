package org.footballproject.response

import org.footballproject.clientData.SquadPlayer

class TeamPlayer(
    val name: String,
    val age: Int?,
    val playerNumber: Int?,
    val position: String?,
    val photo: String?,
    val playerId: Int
) {

    companion object {
        fun fromSquadPlayer(player: SquadPlayer): TeamPlayer =
            TeamPlayer(
                player.name,
                player.age,
                playerNumber = player.number,
                player.position,
                photo = player.photo,
                playerId = player.id ?: -1
            )
    }
}