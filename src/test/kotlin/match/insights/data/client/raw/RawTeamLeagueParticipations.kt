package match.insights.data.client.raw

class RawTeamLeagueParticipations {
    companion object {
        val teamLeagueParticipations = """
            {
              "get": "leagues",
              "parameters": { "team": "435", "season": "2024" },
              "errors": [],
              "results": 3,
              "paging": { "current": 1, "total": 1 },
              "response": [
                {
                  "league": {
                    "id": 128,
                    "name": "Liga Profesional Argentina",
                    "type": "League",
                    "logo": "https://media.api-sports.io/football/leagues/128.png"
                  },
                  "country": {
                    "name": "Argentina",
                    "code": "AR",
                    "flag": "https://media.api-sports.io/flags/ar.svg"
                  },
                  "seasons": [
                    {
                      "year": 2024,
                      "start": "2024-05-12",
                      "end": "2024-12-15",
                      "current": false,
                      "coverage": {
                        "fixtures": {
                          "events": true,
                          "lineups": true,
                          "statistics_fixtures": true,
                          "statistics_players": true
                        },
                        "standings": true,
                        "players": true,
                        "top_scorers": true,
                        "top_assists": true,
                        "top_cards": true,
                        "injuries": false,
                        "predictions": true,
                        "odds": false
                      }
                    }
                  ]
                },
                {
                  "league": {
                    "id": 130,
                    "name": "Copa Argentina",
                    "type": "Cup",
                    "logo": "https://media.api-sports.io/football/leagues/130.png"
                  },
                  "country": {
                    "name": "Argentina",
                    "code": "AR",
                    "flag": "https://media.api-sports.io/flags/ar.svg"
                  },
                  "seasons": [
                    {
                      "year": 2024,
                      "start": "2024-01-25",
                      "end": "2024-12-04",
                      "current": false,
                      "coverage": {
                        "fixtures": {
                          "events": true,
                          "lineups": true,
                          "statistics_fixtures": false,
                          "statistics_players": false
                        },
                        "standings": false,
                        "players": true,
                        "top_scorers": true,
                        "top_assists": true,
                        "top_cards": true,
                        "injuries": false,
                        "predictions": true,
                        "odds": false
                      }
                    }
                  ]
                },
                {
                  "league": {
                    "id": 667,
                    "name": "Friendlies Clubs",
                    "type": "Cup",
                    "logo": "https://media.api-sports.io/football/leagues/667.png"
                  },
                  "country": {
                    "name": "World",
                    "code": null,
                    "flag": null
                  },
                  "seasons": [
                    {
                      "year": 2024,
                      "start": "2024-01-04",
                      "end": "2024-12-30",
                      "current": false,
                      "coverage": {
                        "fixtures": {
                          "events": true,
                          "lineups": true,
                          "statistics_fixtures": true,
                          "statistics_players": true
                        },
                        "standings": false,
                        "players": true,
                        "top_scorers": true,
                        "top_assists": true,
                        "top_cards": true,
                        "injuries": false,
                        "predictions": true,
                        "odds": false
                      }
                    }
                  ]
                }
              ]
            }

        """.trimIndent()
    }
}