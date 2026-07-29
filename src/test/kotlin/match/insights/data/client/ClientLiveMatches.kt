package match.insights.data.client

import match.insights.clientData.Event
import match.insights.clientData.EventPlayer
import match.insights.clientData.Fixture
import match.insights.clientData.Goal
import match.insights.clientData.League
import match.insights.clientData.LiveFixtureResponse
import match.insights.clientData.MatchStatus
import match.insights.clientData.Player
import match.insights.clientData.Score
import match.insights.clientData.Team
import match.insights.clientData.Teams
import match.insights.clientData.Time
import match.insights.clientData.Venue


class ClientLiveMatches {
    companion object {
        val liveFixtures: List<LiveFixtureResponse> = listOf(
            LiveFixtureResponse(
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
                    )
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
                events = listOf(
                    Event(
                        time = Time(1),
                        team = Team(),
                        player = EventPlayer(1, "plaxer x"),
                        assist = EventPlayer(2, "plaxer y"),
                        type = "red card",
                        detail = "bla bla bla",
                        comments = "whatever it is"
                    )
                )
            )

        )
    }
}