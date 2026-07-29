package match.insights.data.client.raw

class ClientRawData {

    companion object {
        val todayMatches = """
            {"response":[{"fixture":{"id":1327475,"referee":"Elijio Arreguin","timezone":"UTC","date":"2025-07-27T00:00:00+00:00","timestamp":1753574400,"periods":{"first":1753574400,"second":1753578000},"venue":{"id":20751,"name":"Protective Stadium","city":"Birmingham, Alabama"},"status":{"long":"Match Finished","short":"FT","elapsed":90,"extra":6}},"league":{"id":1095,"name":"USL League One Cup","country":"USA","logo":"https://media.api-sports.io/football/leagues/1095.png","flag":"https://media.api-sports.io/flags/us.svg","season":2025,"round":"Group Stage - 8","standings":false},"teams":{"home":{"id":3989,"name":"Birmingham Legion","logo":"https://media.api-sports.io/football/teams/3989.png","winner":true},"away":{"id":9025,"name":"Forward Madison","logo":"https://media.api-sports.io/football/teams/9025.png","winner":false}},"goals":{"home":2,"away":1},"score":{"halftime":{"home":1,"away":0},"fulltime":{"home":2,"away":1},"extratime":{"home":null,"away":null},"penalty":{"home":null,"away":null}}}]}
        """.trimIndent()


        val matchDetails = """
        {
  "response": [
    {
      "fixture": {
        "id": 12345,
        "date": "2023-04-15T18:00:00+00:00",
        "venue": { "name": "Stadium Name", "city": "City" }
      },
      "league": {
        "id": 39,
        "name": "Premier League",
        "season": 2023,
        "logo": "league-logo-url"
      },
      "teams": {
        "home": { "id": 1, "name": "Team A", "logo": "url" },
        "away": { "id": 2, "name": "Team B", "logo": "url" }
      },
      "goals": { "home": 2, "away": 1 },
      "score": {
        "halftime": { "home": 1, "away": 1 },
        "fulltime": { "home": 2, "away": 1 }
      }
    }
  ]
}""".trimIndent()

        val oddsResponse = """
            {
              "response": [
                {
                  "bookmakers": [
                    {
                      "name": "Bet365",
                      "bets": [
                        {
                          "name": "Match Winner",
                          "values": [
                            { "value": "Home", "odd": "1.85" },
                            { "value": "Draw", "odd": "3.50" },
                            { "value": "Away", "odd": "4.20" }
                          ]
                        },
                        {
                          "name": "Odd/Even - First Half",
                          "values": [
                            { "value": "Odd", "odd": "2.00" },
                            { "value": "Even", "odd": "1.85" }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }

        """.trimIndent()


        val matchEvents = """
            {
              "get": "fixtures/events",
              "parameters": {
                "fixture": 12345
              },
              "errors": [],
              "results": 5,
              "paging": {
                "current": 1,
                "total": 1
              },
              "response": [
                {
                  "time": {
                    "elapsed": 12,
                    "extra": null
                  },
                  "team": {
                    "id": 33,
                    "name": "Team A",
                    "logo": "https://media.api-sports.io/football/teams/33.png"
                  },
                  "player": {
                    "id": 101,
                    "name": "John Doe"
                  },
                  "assist": {
                    "id": 202,
                    "name": "Alex Smith"
                  },
                  "type": "Goal",
                  "detail": "Normal Goal",
                  "comments": null
                },
                {
                  "time": {
                    "elapsed": 30,
                    "extra": null
                  },
                  "team": {
                    "id": 44,
                    "name": "Team B",
                    "logo": "https://media.api-sports.io/football/teams/44.png"
                  },
                  "player": {
                    "id": 102,
                    "name": "Michael Johnson"
                  },
                  "assist": null,
                  "type": "Card",
                  "detail": "Yellow Card",
                  "comments": "Foul on player"
                },
                {
                  "time": {
                    "elapsed": 45,
                    "extra": 2
                  },
                  "team": {
                    "id": 33,
                    "name": "Team A",
                    "logo": "https://media.api-sports.io/football/teams/33.png"
                  },
                  "player": {
                    "id": 103,
                    "name": "Robert Brown"
                  },
                  "assist": null,
                  "type": "Goal",
                  "detail": "Penalty",
                  "comments": "Penalty scored in injury time"
                },
                {
                  "time": {
                    "elapsed": 78,
                    "extra": null
                  },
                  "team": {
                    "id": 44,
                    "name": "Team B",
                    "logo": "https://media.api-sports.io/football/teams/44.png"
                  },
                  "player": {
                    "id": 104,
                    "name": "Chris Lee"
                  },
                  "assist": null,
                  "type": "Substitution",
                  "detail": "Player Substitution",
                  "comments": "Replaced injured player"
                },
                {
                  "time": {
                    "elapsed": 90,
                    "extra": 4
                  },
                  "team": {
                    "id": 33,
                    "name": "Team A",
                    "logo": "https://media.api-sports.io/football/teams/33.png"
                  },
                  "player": {
                    "id": 105,
                    "name": "David Wilson"
                  },
                  "assist": null,
                  "type": "Card",
                  "detail": "Red Card",
                  "comments": "Serious foul play"
                }
              ]
            }

        """.trimIndent()

        val teamDetailsRaw = """
{
  "get": "teams",
  "parameters": { "id": "33" },
  "errors": [],
  "results": 1,
  "paging": { "current": 1, "total": 1 },
  "response": [
    {
      "team": {
        "id": 33,
        "name": "Manchester United",
        "code": "MUN",
        "country": "England",
        "founded": 1878,
        "national": false,
        "logo": "https://media.api-sports.io/football/teams/33.png"
      },
      "venue": {
        "id": 556,
        "name": "Old Trafford",
        "address": "Sir Matt Busby Way",
        "city": "Manchester",
        "capacity": 76212,
        "surface": "grass",
        "image": "https://media.api-sports.io/football/venues/556.png"
      }
    }
  ]
}
""".trimIndent()


        val coachResponse = """
            {
              "get": "coachs",
              "parameters": {
                "team": "33"
              },
              "errors": [],
              "results": 1,
              "paging": {
                "current": 1,
                "total": 1
              },
              "response": [
                {
                  "id": 1234,
                  "name": "Erik ten Hag",
                  "firstname": "Erik",
                  "lastname": "ten Hag",
                  "age": 53,
                  "birth": {
                    "date": "1970-02-02",
                    "place": "Haaksbergen",
                    "country": "Netherlands"
                  },
                  "nationality": "Netherlands",
                  "team": {
                    "id": 33,
                    "name": "Manchester United",
                    "logo": "https://media.api-sports.io/football/teams/33.png"
                  },
                  "career": [
                    {
                      "team": {
                        "id": 50,
                        "name": "Ajax",
                        "logo": "https://media.api-sports.io/football/teams/50.png"
                      },
                      "start": "2017-12-28",
                      "end": "2022-05-15"
                    }
                  ]
                }
              ]
            }

            
            
        """.trimIndent()


        val playersResponse = """
{
  "get": "players",
  "parameters": {
    "team": "33",
    "season": "2024",
    "page": "1"
  },
  "errors": [],
  "results": 2,
  "paging": {
    "current": 1,
    "total": 1
  },
  "response": [
    {
      "player": {
        "id": 201,
        "name": "Rodrigo Rey",
        "firstname": "Rodrigo",
        "lastname": "Rey",
        "age": 33,
        "height": "187 cm",
        "weight": "83 kg",
        "photo": "https://media.api-sports.io/football/players/201.png"
      },
      "statistics": [
        {
          "team": { "id": 33, "name": "Independiente" },
          "games": { "position": "G", "appearences": 20, "minutes": 1800 },
          "goals": { "conceded": 22, "saves": 65 },
          "cards": { "yellow": 1, "red": 0 },
          "penalty": { "saved": 2, "scored": 0, "missed": 0 }
        }
      ]
    },
    {
      "player": {
        "id": 204,
        "name": "Gabriel Ávalos",
        "firstname": "Gabriel",
        "lastname": "Ávalos",
        "age": 29,
        "height": "185 cm",
        "weight": "80 kg",
        "photo": "https://media.api-sports.io/football/players/204.png"
      },
      "statistics": [
        {
          "team": { "id": 33, "name": "Independiente" },
          "games": { "position": "F", "appearences": 22, "minutes": 1700 },
          "goals": { "total": 8, "assists": 2 },
          "cards": { "yellow": 3, "red": 0 },
          "penalty": { "saved": 0, "scored": 2, "missed": 1 }
        }
      ]
    }
  ]
}
""".trimIndent()


    }


}