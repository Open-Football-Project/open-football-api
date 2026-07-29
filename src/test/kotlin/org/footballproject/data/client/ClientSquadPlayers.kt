package org.footballproject.data.client

import org.footballproject.clientData.SquadPlayer

class ClientSquadPlayers {
    companion object {

        val squadPlayers = listOf(
            SquadPlayer(

                id = 6492,
                name = "J. Ledesma",
                age = 32,
                number = 25,
                position = "Goalkeeper",
                photo = "https://media.api-sports.io/football/players/6492.png"
            ),
            SquadPlayer(

                id = 6493,
                name = "X. Alonso",
                age = 21,
                number = 11,
                position = "Defender",
                photo = "https://media.api-sports.io/football/players/6491.png"
            ),
        )
    }
}