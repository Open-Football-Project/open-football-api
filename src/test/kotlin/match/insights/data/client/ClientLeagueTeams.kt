package match.insights.data.client

import match.insights.clientData.Team
import match.insights.clientData.TeamResponse
import match.insights.clientData.Venue

class ClientLeagueTeams {
    companion object {
        val leagueTeams = listOf(
            TeamResponse(
                team = Team(
                    id = 435,
                    name = "River Plate",

                    country = "Argentina",
                    founded = 1901,

                    logo = "https://media.api-sports.io/football/teams/435.png"
                ),
                venue = Venue(
                    id = 19570,
                    name = "Estadio Mâs Monumental",
                    city = "Buenos Aires",
                    capacity = 83214,

                    )
            ),
            TeamResponse(
                team = Team(
                    id = 451,
                    name = "Boca Juniors",

                    country = "Argentina",
                    founded = 1905,

                    logo = "https://media.api-sports.io/football/teams/451.png"
                ),
                venue = Venue(
                    id = 46,
                    name = "Estadio Alberto José Armando (La Bombonera)",

                    city = "Buenos Aires",
                    capacity = 49000,

                    )
            ),

            )
    }
}