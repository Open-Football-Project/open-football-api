package org.footballproject.data.client.raw

class ClientCurrentSquad {
    companion object {

        val mockJson = """
{
  "get": "players/squads",
  "parameters": {"team": "435"},
  "errors": [],
  "results": 1,
  "paging": {"current": 1, "total": 1},
  "response": [
    {
      "team": {
        "id": 435,
        "name": "River Plate",
        "logo": "https://media.api-sports.io/football/teams/435.png"
      },
      "players": [
        {
          "id": 6492,
          "name": "J. Ledesma",
          "age": 32,
          "number": 25,
          "position": "Goalkeeper",
          "photo": "https://media.api-sports.io/football/players/6492.png"
        },
        {
          "id": 2463,
          "name": "F. Armani",
          "age": 39,
          "number": 1,
          "position": "Goalkeeper",
          "photo": "https://media.api-sports.io/football/players/2463.png"
        },
        {
          "id": 9933,
          "name": "M. Borja",
          "age": 32,
          "number": 9,
          "position": "Attacker",
          "photo": "https://media.api-sports.io/football/players/9933.png"
        }
      ]
    }
  ]
}
""".trimIndent()


    }
}