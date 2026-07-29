package match.insights.data.client.raw

class ClientRawLineups {
    companion object {
        val mockLineupJson = """
{
  "get": "fixtures/lineups",
  "parameters": {
    "fixture": "999999"
  },
  "errors": [],
  "results": 2,
  "response": [
    {
      "team": {
        "id": 33,
        "name": "Manchester United",
        "logo": "https://media.api-sports.io/football/teams/33.png"
      },
      "coach": {
        "id": 501,
        "name": "Erik ten Hag",
        "photo": "https://media.api-sports.io/football/coachs/501.png"
      },
      "formation": "4-2-3-1",
      "startXI": [
        { "player": { "id": 1, "name": "André Onana", "number": 24, "pos": "G", "grid": "1:1" } },
        { "player": { "id": 2, "name": "Diogo Dalot", "number": 20, "pos": "D", "grid": "2:4" } },
        { "player": { "id": 3, "name": "Harry Maguire", "number": 5, "pos": "D", "grid": "2:2" } },
        { "player": { "id": 4, "name": "Lisandro Martínez", "number": 6, "pos": "D", "grid": "2:3" } },
        { "player": { "id": 5, "name": "Luke Shaw", "number": 23, "pos": "D", "grid": "2:1" } },
        { "player": { "id": 6, "name": "Casemiro", "number": 18, "pos": "M", "grid": "3:2" } },
        { "player": { "id": 7, "name": "Bruno Fernandes", "number": 8, "pos": "M", "grid": "4:2" } },
        { "player": { "id": 8, "name": "Marcus Rashford", "number": 10, "pos": "F", "grid": "5:3" } },
        { "player": { "id": 9, "name": "Rasmus Højlund", "number": 11, "pos": "F", "grid": "5:2" } }
      ],
      "substitutes": [
        { "player": { "id": 10, "name": "Christian Eriksen", "number": 14, "pos": "M", "grid": null } },
        { "player": { "id": 11, "name": "Scott McTominay", "number": 39, "pos": "M", "grid": null } }
      ]
    },
    {
      "team": {
        "id": 40,
        "name": "Liverpool",
        "logo": "https://media.api-sports.io/football/teams/40.png"
      },
      "coach": {
        "id": 601,
        "name": "Jürgen Klopp",
        "photo": "https://media.api-sports.io/football/coachs/601.png"
      },
      "formation": "4-3-3",
      "startXI": [
        { "player": { "id": 12, "name": "Alisson Becker", "number": 1, "pos": "G", "grid": "1:1" } },
        { "player": { "id": 13, "name": "Trent Alexander-Arnold", "number": 66, "pos": "D", "grid": "2:4" } },
        { "player": { "id": 14, "name": "Virgil van Dijk", "number": 4, "pos": "D", "grid": "2:2" } },
        { "player": { "id": 15, "name": "Ibrahima Konaté", "number": 5, "pos": "D", "grid": "2:3" } },
        { "player": { "id": 16, "name": "Andrew Robertson", "number": 26, "pos": "D", "grid": "2:1" } },
        { "player": { "id": 17, "name": "Alexis Mac Allister", "number": 10, "pos": "M", "grid": "3:2" } },
        { "player": { "id": 18, "name": "Mohamed Salah", "number": 11, "pos": "F", "grid": "5:3" } },
        { "player": { "id": 19, "name": "Darwin Núñez", "number": 9, "pos": "F", "grid": "5:2" } }
      ],
      "substitutes": [
        { "player": { "id": 20, "name": "Joe Gomez", "number": 2, "pos": "D", "grid": null } },
        { "player": { "id": 21, "name": "Diogo Jota", "number": 20, "pos": "F", "grid": null } }
      ]
    }
  ]
}
""".trimIndent()

    }
}