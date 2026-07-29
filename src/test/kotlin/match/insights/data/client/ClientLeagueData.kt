package match.insights.data.client


import match.insights.clientData.LeagueAndCountry
import match.insights.clientData.LeagueCountry
import match.insights.clientData.LeagueGoals
import match.insights.clientData.LeagueMinInfo
import match.insights.clientData.LeagueRecordStats
import match.insights.clientData.LeagueSeason
import match.insights.clientData.LeagueStandings
import match.insights.clientData.LeagueWithStandings

import match.insights.clientData.Team

class ClientLeagueData {
    companion object {
        val allLeagues: List<LeagueAndCountry> = listOf(
            LeagueAndCountry(

                league = LeagueMinInfo(
                    id = 334,
                    name = "Unknown League",
                    type = "Cup",
                    logo = "https://media.api-sports.io/football/leagues/334.png"
                ),
                country = LeagueCountry(
                    name = "unknown country",
                    code = null,
                    flag = "https://media.api-sports.io/flags/gb.svg"
                ),
                seasons = listOf(
                    LeagueSeason(
                        year = 2024,
                        start = "2025-01-01",
                        end = "2025-12-31",
                        current = true,
                        coverage = null
                    )
                )
            ),
            LeagueAndCountry(
                seasons = listOf(
                    LeagueSeason(
                        year = 2025,
                        start = "2025-01-01",
                        end = "2025-12-31",
                        current = true,
                        coverage = null
                    )
                ),
                league = LeagueMinInfo(
                    id = 1,
                    name = "world cup",
                    type = "Cup",
                    logo = "https://media.api-sports.io/football/leagues/1.png"
                ),
                country = LeagueCountry(
                    name = "World",
                    code = null,
                    flag = "https://media.api-sports.io/flags/gb.svg"
                )
            ),
            LeagueAndCountry(
                seasons = listOf(
                    LeagueSeason(
                        year = 2025,
                        start = "2025-01-01",
                        end = "2025-12-31",
                        current = true,
                        coverage = null
                    )
                ),
                league = LeagueMinInfo(
                    id = 39,
                    name = "Premier League",
                    type = "League",
                    logo = "https://media.api-sports.io/football/leagues/39.png"
                ),
                country = LeagueCountry(
                    name = "England",
                    code = "ENG",
                    flag = "https://media.api-sports.io/flags/gb.svg"
                )
            ),
            LeagueAndCountry(
                seasons = listOf(
                    LeagueSeason(
                        year = 2025,
                        start = "2025-01-01",
                        end = "2025-12-31",
                        current = true,
                        coverage = null
                    )
                ),
                league = LeagueMinInfo(
                    id = 140,
                    name = "La Liga",
                    type = "League",
                    logo = "https://media.api-sports.io/football/leagues/140.png"
                ),
                country = LeagueCountry(
                    name = "Spain",
                    code = "ESP",
                    flag = "https://media.api-sports.io/flags/es.svg"
                )
            )
        )

        val standing = LeagueStandings(
            rank = 1,
            team = Team(
                id = 33,
                name = "Manchester United",
                logo = "https://media.api-sports.io/football/teams/33.png",
                winner = null,
                goals = null
            ),
            points = 89,
            goalsDiff = 45,
            form = "WWDWW",
            status = "same",
            description = "Champions League",
            group = "default",
            all = LeagueRecordStats(
                played = 38,
                win = 28,
                draw = 5,
                lose = 5,
                goals = LeagueGoals(
                    `for` = 85,
                    against = 40
                )
            ),
            home = LeagueRecordStats(
                played = 19,
                win = 15,
                draw = 3,
                lose = 1,
                goals = LeagueGoals(
                    `for` = 50,
                    against = 15
                )
            ),
            away = LeagueRecordStats(
                played = 19,
                win = 13,
                draw = 2,
                lose = 4,
                goals = LeagueGoals(
                    `for` = 35,
                    against = 25
                )
            ),
            update = "2025-05-20T18:00:00+00:00"
        )

        val leagueStandings = LeagueWithStandings(
            id = 1,
            season = 2022,
            standings = listOf(listOf(standing))
        )
    }

}