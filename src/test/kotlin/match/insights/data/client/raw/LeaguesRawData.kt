package match.insights.data.client.raw

class LeaguesRawData {
    companion object {
        val allLeagues = """
            {
              "get": "leagues",
              "parameters": [],
              "errors": [],
              "results": 1787,
              "paging": { "current": 1, "total": 36 },
              "response": [
                {
                  "league": {
                    "id": 39,
                    "name": "Premier League",
                    "type": "League",
                    "logo": "https://media.api-sports.io/football/leagues/39.png"
                  },
                  "country": {
                    "name": "England",
                    "code": "GB",
                    "flag": "https://media.api-sports.io/flags/gb.svg"
                  },
                  "seasons": [
                    { "year": 2024, "start": "2024-08-10", "end": "2025-05-25", "current": true }
                  ]
                }
              ]
            }
            
        """.trimIndent()

        val leagueStandingsMock = """
            {
              "get": "standings",
              "parameters": {
                "league": "128",
                "season": "2024"
              },
              "errors": [],
              "results": 1,
              "paging": {
                "current": 1,
                "total": 1
              },
              "response": [
                {
                  "league": {
                    "id": 128,
                    "name": "Copa de la Liga",
                    "country": "Argentina",
                    "logo": "https://media.api-sports.io/football/leagues/128.png",
                    "flag": "https://media.api-sports.io/flags/ar.svg",
                    "season": 2024,
                    "standings": [
                      [
                        {
                          "rank": 1,
                          "team": {
                            "id": 435,
                            "name": "River Plate",
                            "logo": "https://media.api-sports.io/football/teams/435.png"
                          },
                          "points": 20,
                          "goalsDiff": 10,
                          "form": "WWDLW",
                          "status": "same",
                          "description": "Quarter-finals",
                          "group": "Group A",
                          "all": {
                            "played": 10,
                            "win": 6,
                            "draw": 2,
                            "lose": 2,
                            "goals": { "for": 18, "against": 8 }
                          },
                          "home": {
                            "played": 5,
                            "win": 4,
                            "draw": 1,
                            "lose": 0,
                            "goals": { "for": 12, "against": 4 }
                          },
                          "away": {
                            "played": 5,
                            "win": 2,
                            "draw": 1,
                            "lose": 2,
                            "goals": { "for": 6, "against": 4 }
                          },
                          "update": "2024-09-01T00:00:00+00:00"
                        },
                        {
                          "rank": 2,
                          "team": {
                            "id": 436,
                            "name": "Boca Juniors",
                            "logo": "https://media.api-sports.io/football/teams/436.png"
                          },
                          "points": 18,
                          "goalsDiff": 8,
                          "form": "WLWDW",
                          "status": "same",
                          "description": "Quarter-finals",
                          "group": "Group A",
                          "all": {
                            "played": 10,
                            "win": 5,
                            "draw": 3,
                            "lose": 2,
                            "goals": { "for": 15, "against": 7 }
                          },
                          "home": {
                            "played": 5,
                            "win": 3,
                            "draw": 1,
                            "lose": 1,
                            "goals": { "for": 9, "against": 4 }
                          },
                          "away": {
                            "played": 5,
                            "win": 2,
                            "draw": 2,
                            "lose": 1,
                            "goals": { "for": 6, "against": 3 }
                          },
                          "update": "2024-09-01T00:00:00+00:00"
                        }
                      ],
                      [
                        {
                          "rank": 1,
                          "team": {
                            "id": 437,
                            "name": "Racing Club",
                            "logo": "https://media.api-sports.io/football/teams/437.png"
                          },
                          "points": 19,
                          "goalsDiff": 12,
                          "form": "WWLWW",
                          "status": "same",
                          "description": "Quarter-finals",
                          "group": "Group B",
                          "all": {
                            "played": 10,
                            "win": 6,
                            "draw": 1,
                            "lose": 3,
                            "goals": { "for": 20, "against": 8 }
                          },
                          "home": {
                            "played": 5,
                            "win": 4,
                            "draw": 0,
                            "lose": 1,
                            "goals": { "for": 12, "against": 3 }
                          },
                          "away": {
                            "played": 5,
                            "win": 2,
                            "draw": 1,
                            "lose": 2,
                            "goals": { "for": 8, "against": 5 }
                          },
                          "update": "2024-09-01T00:00:00+00:00"
                        },
                        {
                          "rank": 2,
                          "team": {
                            "id": 438,
                            "name": "Independiente",
                            "logo": "https://media.api-sports.io/football/teams/438.png"
                          },
                          "points": 17,
                          "goalsDiff": 7,
                          "form": "WWDWL",
                          "status": "same",
                          "description": "Quarter-finals",
                          "group": "Group B",
                          "all": {
                            "played": 10,
                            "win": 5,
                            "draw": 2,
                            "lose": 3,
                            "goals": { "for": 14, "against": 7 }
                          },
                          "home": {
                            "played": 5,
                            "win": 3,
                            "draw": 1,
                            "lose": 1,
                            "goals": { "for": 9, "against": 3 }
                          },
                          "away": {
                            "played": 5,
                            "win": 2,
                            "draw": 1,
                            "lose": 2,
                            "goals": { "for": 5, "against": 4 }
                          },
                          "update": "2024-09-01T00:00:00+00:00"
                        }
                      ]
                    ]
                  }
                }
              ]
            }
            """.trimIndent()


    }
}