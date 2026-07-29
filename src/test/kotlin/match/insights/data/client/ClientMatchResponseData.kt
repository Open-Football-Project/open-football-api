package match.insights.data.client

import match.insights.clientData.Fixture
import match.insights.clientData.Goal
import match.insights.clientData.League
import match.insights.clientData.MatchResponse
import match.insights.clientData.MatchStatus
import match.insights.clientData.Score
import match.insights.clientData.Team
import match.insights.clientData.Teams
import match.insights.clientData.Venue

class ClientMatchResponseData {

    companion object {
        val matchResponse = MatchResponse(
            fixture = Fixture(
                id = 123456,
                date = "2025-08-02T16:30:00+00:00",
                status = MatchStatus(
                    long = "Match Finished",
                    short = "FT",
                    elapsed = 90,
                    extra = null
                ),
                venue = Venue(
                    name = "Old Trafford",
                    city = "Manchester"
                ),
                round = "first-round"

            ),
            league = League(
                id = 39,
                name = "Premier League",
                country = "England",
                logo = "https://media.api-sports.io/football/leagues/39.png",
                flag = "https://media.api-sports.io/flags/gb.svg",
                season = 2025,
                round = "Regular Season - 1"
            ),
            teams = Teams(
                home = Team(
                    id = 33,
                    name = "Manchester United",
                    logo = "https://media.api-sports.io/football/teams/33.png",
                    winner = true,
                    goals = 3
                ),
                away = Team(
                    id = 40,
                    name = "Liverpool",
                    logo = "https://media.api-sports.io/football/teams/40.png",
                    winner = false,
                    goals = 1
                )
            ),
            goals = Goal(
                home = 3,
                away = 1
            ),
            score = Score(
                halftime = Goal(home = 2, away = 1),
                fulltime = Goal(home = 3, away = 1)
            ),
            venue = Venue(
                name = "Old Trafford",
                city = "Manchester"
            )
        )


        val matchResponseList = listOf(
            matchResponse, matchResponse.copy(
                fixture = matchResponse.fixture.copy(status = MatchStatus("First Half", short = "1H")),
                teams = matchResponse.teams.copy(
                    home =
                        Team(
                            id = 33,
                            name = "Manchester United",
                            logo = "https://media.api-sports.io/football/teams/33.png",
                            winner = true,
                            goals = 5
                        )
                ),
                league = matchResponse.league.copy(
                    id = 1,
                    name = "FA Cup",
                    country = "Unknown",
                    logo = "no logo",
                    flag = "no flag",
                    season = 2025,
                    round = "first-round"
                ),
                goals = matchResponse.goals?.copy(home = 5, away = 1),
            ),

            matchResponse.copy(
                fixture = matchResponse.fixture.copy(
                    date = "2025-04-02T16:30:00+00:00",
                    status = MatchStatus(
                        long = "Second Half",
                        short = "2H",
                        elapsed = 90,
                        extra = null
                    )
                ),
                teams = matchResponse.teams.copy(
                    home = Team(
                        id = 40,
                        name = "Liverpool",
                        logo = "https://media.api-sports.io/football/teams/40.png",
                        winner = false,
                        goals = 4
                    ),
                    away = Team(
                        id = 33,
                        name = "Manchester United",
                        logo = "https://media.api-sports.io/football/teams/33.png",
                        winner = true,
                        goals = 2
                    )
                ),
                goals = matchResponse.goals?.copy(home = 4, away = 2)
            ),
            matchResponse.copy(
                fixture = matchResponse.fixture.copy(
                    date = "2025-05-02T16:30:00+00:00",
                    status = MatchStatus(
                        long = "Not Started",
                        short = "NS",
                        elapsed = null,
                        extra = null
                    ),
                    round = "second-round"
                ),
                teams = matchResponse.teams.copy(
                    home = Team(
                        id = 40,
                        name = "Liverpool",
                        logo = "https://media.api-sports.io/football/teams/40.png",
                        winner = false,
                        goals = 2
                    ),
                    away = Team(
                        id = 33,
                        name = "Manchester United",
                        logo = "https://media.api-sports.io/football/teams/33.png",
                        winner = true,
                        goals = 2
                    )
                ),
                goals = matchResponse.goals?.copy(home = 2, away = 2)
            )
        )

    }
}