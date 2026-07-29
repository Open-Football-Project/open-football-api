package match.insights.data.client.raw

class LiveFixtureWithEvents {

    companion object {
        val response = """
            {
              "get": "fixtures",
              "parameters": {
                "live": "all"
              },
              "errors": [],
              "results": 2,
              "paging": {
                "current": 1,
                "total": 1
              },
              "response": [
                {
                  "fixture": {
                    "id": 1353530,
                    "referee": "Lucas Canetto Bellote, Brazil",
                    "timezone": "UTC",
                    "date": "2025-09-25T22:00:00+00:00",
                    "timestamp": 1758837600,
                    "periods": {
                      "first": 1758837600,
                      "second": null
                    },
                    "venue": {
                      "id": 230,
                      "name": "Estádio Major Antônio Couto Pereira",
                      "city": "Curitiba, Paraná"
                    },
                    "status": {
                      "long": "First Half",
                      "short": "1H",
                      "elapsed": 30,
                      "extra": null
                    }
                  },
                  "league": {
                    "id": 72,
                    "name": "Serie B",
                    "country": "Brazil",
                    "logo": "https://media.api-sports.io/football/leagues/72.png",
                    "flag": "https://media.api-sports.io/flags/br.svg",
                    "season": 2025,
                    "round": "Regular Season - 28",
                    "standings": true
                  },
                  "teams": {
                    "home": {
                      "id": 147,
                      "name": "Coritiba",
                      "logo": "https://media.api-sports.io/football/teams/147.png",
                      "winner": null
                    },
                    "away": {
                      "id": 140,
                      "name": "Criciuma",
                      "logo": "https://media.api-sports.io/football/teams/140.png",
                      "winner": null
                    }
                  },
                  "goals": {
                    "home": 0,
                    "away": 0
                  },
                  "score": {
                    "halftime": { "home": 0, "away": 0 },
                    "fulltime": { "home": null, "away": null },
                    "extratime": { "home": null, "away": null },
                    "penalty": { "home": null, "away": null }
                  },
                  "events": [
                    {
                      "time": { "elapsed": 5, "extra": null },
                      "team": {
                        "id": 147,
                        "name": "Coritiba",
                        "logo": "https://media.api-sports.io/football/teams/147.png"
                      },
                      "player": { "id": 2150, "name": "Josue" },
                      "assist": { "id": null, "name": null },
                      "type": "Card",
                      "detail": "Yellow Card",
                      "comments": null
                    }
                  ]
                },
                {
                  "fixture": {
                    "id": 1446720,
                    "referee": "Alexis Herrera, Venezuela",
                    "timezone": "UTC",
                    "date": "2025-09-25T22:00:00+00:00",
                    "timestamp": 1758837600,
                    "periods": {
                      "first": 1758837600,
                      "second": null
                    },
                    "venue": {
                      "id": 20813,
                      "name": "MorumBIS",
                      "city": "São Paulo, São Paulo"
                    },
                    "status": {
                      "long": "First Half",
                      "short": "1H",
                      "elapsed": 29,
                      "extra": null
                    }
                  },
                  "league": {
                    "id": 13,
                    "name": "CONMEBOL Libertadores",
                    "country": "World",
                    "logo": "https://media.api-sports.io/football/leagues/13.png",
                    "flag": null,
                    "season": 2025,
                    "round": "Quarter-finals",
                    "standings": true
                  },
                  "teams": {
                    "home": {
                      "id": 126,
                      "name": "Sao Paulo",
                      "logo": "https://media.api-sports.io/football/teams/126.png",
                      "winner": null
                    },
                    "away": {
                      "id": 1158,
                      "name": "LDU de Quito",
                      "logo": "https://media.api-sports.io/football/teams/1158.png",
                      "winner": null
                    }
                  },
                  "goals": {
                    "home": 0,
                    "away": 0
                  },
                  "score": {
                    "halftime": { "home": 0, "away": 0 },
                    "fulltime": { "home": null, "away": null },
                    "extratime": { "home": null, "away": null },
                    "penalty": { "home": null, "away": null }
                  },
                  "events": [
                    {
                      "time": { "elapsed": 14, "extra": null },
                      "team": {
                        "id": 1158,
                        "name": "LDU de Quito",
                        "logo": "https://media.api-sports.io/football/teams/1158.png"
                      },
                      "player": { "id": 11540, "name": "F. Cornejo" },
                      "assist": { "id": null, "name": null },
                      "type": "Card",
                      "detail": "Yellow Card",
                      "comments": "Tripping"
                    }
                  ]
                }
              ]
            }

        """.trimIndent()

        val responseWithNullDetail = """
            {
              "get": "fixtures",
              "parameters": {
                "live": "all"
              },
              "errors": [],
              "results": 1,
              "paging": {
                "current": 1,
                "total": 1
              },
              "response": [
                {
                  "fixture": {
                    "id": 1353530,
                    "referee": "Lucas Canetto Bellote, Brazil",
                    "timezone": "UTC",
                    "date": "2025-09-25T22:00:00+00:00",
                    "timestamp": 1758837600,
                    "periods": {
                      "first": 1758837600,
                      "second": null
                    },
                    "venue": {
                      "id": 230,
                      "name": "Estádio Major Antônio Couto Pereira",
                      "city": "Curitiba, Paraná"
                    },
                    "status": {
                      "long": "First Half",
                      "short": "1H",
                      "elapsed": 30,
                      "extra": null
                    }
                  },
                  "league": {
                    "id": 72,
                    "name": "Serie B",
                    "country": "Brazil",
                    "logo": "https://media.api-sports.io/football/leagues/72.png",
                    "flag": "https://media.api-sports.io/flags/br.svg",
                    "season": 2025,
                    "round": "Regular Season - 28",
                    "standings": true
                  },
                  "teams": {
                    "home": {
                      "id": 147,
                      "name": "Coritiba",
                      "logo": "https://media.api-sports.io/football/teams/147.png",
                      "winner": null
                    },
                    "away": {
                      "id": 140,
                      "name": "Criciuma",
                      "logo": "https://media.api-sports.io/football/teams/140.png",
                      "winner": null
                    }
                  },
                  "goals": {
                    "home": 0,
                    "away": 0
                  },
                  "score": {
                    "halftime": { "home": 0, "away": 0 },
                    "fulltime": { "home": null, "away": null },
                    "extratime": { "home": null, "away": null },
                    "penalty": { "home": null, "away": null }
                  },
                  "events": [
                    {
                      "time": { "elapsed": 5, "extra": null },
                      "team": {
                        "id": 147,
                        "name": "Coritiba",
                        "logo": "https://media.api-sports.io/football/teams/147.png"
                      },
                      "player": { "id": 2150, "name": "Josue" },
                      "assist": { "id": null, "name": null },
                      "type": "Var",
                      "detail": null,
                      "comments": null
                    }
                  ]
                }
              ]
            }

        """.trimIndent()
    }
}