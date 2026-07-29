package match.insights.data.client.raw

import match.insights.clientData.RankingCards
import match.insights.clientData.RankingGames
import match.insights.clientData.RankingGoals
import match.insights.clientData.RankingPlayer
import match.insights.clientData.RankingPlayerStatistic
import match.insights.clientData.RankingPlayerStats
import match.insights.clientData.RankingTeam

class ClientRankingsData {

    companion object {

        val topScorers = listOf(
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 123,
                    name = "E. Haaland",
                    firstname = "Erling",
                    lastname = "Haaland",
                    age = 25,
                    photo = "https://media.api-sports.io/football/players/123.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 50,
                            name = "Manchester City",
                            logo = "https://media.api-sports.io/football/teams/50.png"
                        ),
                        goals = RankingGoals(
                            total = 11,
                            assists = 2
                        ),
                        games = RankingGames(appearences = 9)
                    )
                )
            ),
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 124,
                    name = "A. Semenyo",
                    firstname = "Antoine",
                    lastname = "Semenyo",
                    age = 24,
                    photo = "https://media.api-sports.io/football/players/124.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 52,
                            name = "Bournemouth",
                            logo = "https://media.api-sports.io/football/teams/52.png"
                        ),
                        goals = RankingGoals(
                            total = 6,
                            assists = 3
                        ),
                        games = RankingGames(appearences = 9)
                    )
                )
            )
        )

        val topAssists = listOf(
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 301,
                    name = "M. Kudus",
                    firstname = "Mohammed",
                    lastname = "Kudus",
                    age = 25,
                    photo = "https://media.api-sports.io/football/players/301.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 47,
                            name = "Tottenham",
                            logo = "https://media.api-sports.io/football/teams/47.png"
                        ),
                        goals = RankingGoals(
                            total = 3,
                            assists = 4
                        ),
                        games = RankingGames(appearences = 9)
                    )
                )
            ),
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 302,
                    name = "Q. Hartman",
                    firstname = "Quentin",
                    lastname = "Hartman",
                    age = 26,
                    photo = "https://media.api-sports.io/football/players/302.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 44,
                            name = "Burnley",
                            logo = "https://media.api-sports.io/football/teams/44.png"
                        ),
                        goals = RankingGoals(
                            total = 1,
                            assists = 4
                        ),
                        games = RankingGames(appearences = 8)
                    )
                )
            )
        )


        val topYellowCards = listOf(
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 400,
                    name = "K. Dewsbury-Hall",
                    firstname = "Kiernan",
                    lastname = "Dewsbury-Hall",
                    age = 27,
                    photo = "https://media.api-sports.io/football/players/400.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 49,
                            name = "Everton",
                            logo = "https://media.api-sports.io/football/teams/49.png"
                        ),
                        cards = RankingCards(
                            yellow = 5,
                            red = 0
                        )
                    )
                )
            ),
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 401,
                    name = "T. Adams",
                    firstname = "Tyler",
                    lastname = "Adams",
                    age = 25,
                    photo = "https://media.api-sports.io/football/players/401.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 52,
                            name = "Bournemouth",
                            logo = "https://media.api-sports.io/football/teams/52.png"
                        ),
                        cards = RankingCards(
                            yellow = 4,
                            red = 0
                        )
                    )
                )
            )
        )

      
        val topRedCards = listOf(
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 501,
                    name = "E. Konsa",
                    firstname = "Ezri",
                    lastname = "Konsa",
                    age = 27,
                    photo = "https://media.api-sports.io/football/players/501.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 66,
                            name = "Aston Villa",
                            logo = "https://media.api-sports.io/football/teams/66.png"
                        ),
                        cards = RankingCards(
                            yellow = 1,
                            red = 1
                        )
                    )
                )
            ),
            RankingPlayerStats(
                player = RankingPlayer(
                    id = 502,
                    name = "A. Gordon",
                    firstname = "Anthony",
                    lastname = "Gordon",
                    age = 25,
                    photo = "https://media.api-sports.io/football/players/502.png"
                ),
                statistics = listOf(
                    RankingPlayerStatistic(
                        team = RankingTeam(
                            id = 67,
                            name = "Newcastle",
                            logo = "https://media.api-sports.io/football/teams/67.png"
                        ),
                        cards = RankingCards(
                            yellow = 2,
                            red = 1
                        )
                    )
                )
            )
        )
    }

}