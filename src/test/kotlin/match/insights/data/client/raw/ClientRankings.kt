package match.insights.data.client.raw

class ClientRankings {
    companion object {

        val topScorers = """
{
  "get": "players/topscorers",
  "parameters": {
    "league": "39",
    "season": "2025"
  },
  "errors": [],
  "results": 5,
  "response": [
    {
      "player": {
        "id": 123,
        "name": "E. Haaland",
        "firstname": "Erling",
        "lastname": "Haaland",
        "age": 25,
        "photo": "https://media.api-sports.io/football/players/123.png"
      },
      "statistics": [
        {
          "team": {
            "id": 50,
            "name": "Manchester City",
            "logo": "https://media.api-sports.io/football/teams/50.png"
          },
          "goals": {
            "total": 11,
            "assists": 2
          },
          "games": {
            "appearences": 9
          }
        }
      ]
    },
    {
      "player": {
        "id": 124,
        "name": "A. Semenyo",
        "firstname": "Antoine",
        "lastname": "Semenyo",
        "age": 24,
        "photo": "https://media.api-sports.io/football/players/124.png"
      },
      "statistics": [
        {
          "team": {
            "id": 52,
            "name": "Bournemouth",
            "logo": "https://media.api-sports.io/football/teams/52.png"
          },
          "goals": {
            "total": 6,
            "assists": 3
          },
          "games": {
            "appearences": 9
          }
        }
      ]
    }
  ]
}

""".trimIndent()
    }
}