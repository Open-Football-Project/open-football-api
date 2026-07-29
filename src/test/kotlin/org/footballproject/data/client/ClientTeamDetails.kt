package org.footballproject.data.client

import org.footballproject.clientData.CoachCareer
import org.footballproject.clientData.CoachResponse
import org.footballproject.clientData.Player
import org.footballproject.clientData.PlayerCardStats
import org.footballproject.clientData.PlayerGameStats
import org.footballproject.clientData.PlayerGoalStats
import org.footballproject.clientData.PlayerPenaltyStats
import org.footballproject.clientData.PlayerResponse
import org.footballproject.clientData.PlayerStatistic
import org.footballproject.clientData.Team
import org.footballproject.clientData.TeamResponse
import org.footballproject.clientData.Venue

class ClientTeamDetails {
    companion object {
        val details = TeamResponse(
            team = Team(
                id = 33,
                name = "Manchester United",
                country = "England",
                founded = 1878,
                logo = "https://media.api-sports.io/football/teams/33.png"
            ),
            venue = Venue(
                id = 556,
                name = "Old Trafford",
                city = "Manchester",
                capacity = 76212
            )
        )

        val mockPlayersResponse = listOf(
            PlayerResponse(
                player = Player(
                    id = 201,
                    name = "Rodrigo Rey",
                    age = 33,
                    height = "187 cm",
                    weight = "83 kg",
                ),
                statistics = listOf(
                    PlayerStatistic(
                        team = Team(33, "Independiente"),
                        games = PlayerGameStats(position = "G", appearences = 20, minutes = 1800),
                        goals = PlayerGoalStats(conceded = 22, saves = 65),
                        cards = PlayerCardStats(yellow = 1, red = 0),
                        penalty = PlayerPenaltyStats(scored = 0, missed = 0, saved = 2)
                    )
                )
            ),
            PlayerResponse(
                player = Player(
                    id = 204,
                    name = "Gabriel Ávalos",
                    age = 29,
                    height = "185 cm",
                    weight = "80 kg"
                ),
                statistics = listOf(
                    PlayerStatistic(
                        team = Team(33, "Independiente"),
                        games = PlayerGameStats(position = "F", appearences = 22, minutes = 1700),
                        goals = PlayerGoalStats(total = 8, assists = 2),
                        cards = PlayerCardStats(yellow = 3, red = 0),
                        penalty = PlayerPenaltyStats(scored = 2, missed = 1, saved = 0)
                    )
                )
            )
        )

        val coach = CoachResponse(
            id = 1234,
            name = "Erik ten Hag",
            firstname = "Erik",
            lastname = "ten Hag",
            age = 53,
            nationality = "Unkown",
            team = Team(id = 33),
            career = listOf(
                CoachCareer(
                    Team(id = 33),
                    "22/06/1990",
                    null
                )
            )
        )

    }
}