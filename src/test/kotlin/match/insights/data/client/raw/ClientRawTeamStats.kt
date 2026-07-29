package match.insights.data.client.raw

class ClientRawTeamStats {
    companion object {
        val teamStats = """
            {
              "get": "teams/statistics",
              "parameters": {
                "league": "128",
                "season": "2025",
                "team": "435"
              },
              "errors": [],
              "results": 11,
              "paging": { "current": 1, "total": 1 },
              "response": {
                "league": {
                  "id": 128,
                  "name": "Liga Profesional Argentina",
                  "country": "Argentina",
                  "logo": "https://media.api-sports.io/football/leagues/128.png",
                  "flag": "https://media.api-sports.io/flags/ar.svg",
                  "season": 2025
                },
                "team": {
                  "id": 435,
                  "name": "River Plate",
                  "logo": "https://media.api-sports.io/football/teams/435.png"
                },
                "form": "DWDWDWWLWDDDDWWWWLWWDDWDWWLLLLWL",
                "fixtures": {
                  "played": { "home": 18, "away": 14, "total": 32 },
                  "wins": { "home": 10, "away": 5, "total": 15 },
                  "draws": { "home": 3, "away": 7, "total": 10 },
                  "loses": { "home": 5, "away": 2, "total": 7 }
                },
                "goals": {
                  "for": {
                    "total": { "home": 28, "away": 17, "total": 45 },
                    "average": { "home": "1.6", "away": "1.2", "total": "1.4" },
                    "minute": {
                      "0-15": { "total": 7, "percentage": "15.56%" },
                      "16-30": { "total": 8, "percentage": "17.78%" }
                    },
                    "under_over": {
                      "0.5": { "over": 23, "under": 9 },
                      "1.5": { "over": 13, "under": 19 }
                    }
                  },
                  "against": {
                    "total": { "home": 16, "away": 8, "total": 24 },
                    "average": { "home": "0.9", "away": "0.6", "total": "0.8" },
                    "minute": {
                      "0-15": { "total": 6, "percentage": "26.09%" }
                    },
                    "under_over": {
                      "0.5": { "over": 17, "under": 15 }
                    }
                  }
                },
                "biggest": {
                  "streak": { "wins": 4, "draws": 4, "loses": 4 },
                  "wins": { "home": "4-1", "away": "0-4" },
                  "loses": { "home": "0-2", "away": "2-0" },
                  "goals": {
                    "for": { "home": 4, "away": 4 },
                    "against": { "home": 2, "away": 2 }
                  }
                },
                "clean_sheet": { "home": 7, "away": 8, "total": 15 },
                "failed_to_score": { "home": 4, "away": 5, "total": 9 },
                "penalty": {
                  "scored": { "total": 2, "percentage": "100.00%" },
                  "missed": { "total": 0, "percentage": "0%" },
                  "total": 2
                },
                "lineups": [
                  { "formation": "4-3-3", "played": 15 },
                  { "formation": "4-3-1-2", "played": 7 }
                ],
                "cards": {
                  "yellow": {
                    "0-15": { "total": 6, "percentage": "6.32%" },
                    "16-30": { "total": 13, "percentage": "13.68%" }
                  },
                  "red": {
                    "31-45": { "total": 2, "percentage": "40.00%" },
                    "46-60": { "total": 1, "percentage": "20.00%" }
                  }
                }
              }
            }

        """.trimIndent()
    }
}