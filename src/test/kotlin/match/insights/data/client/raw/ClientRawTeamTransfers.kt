package match.insights.data.client.raw

class ClientRawTeamTransfers {
    companion object {
        val teamTransfers = """
  {
  "get": "transfers",
  "parameters": { "team": "50" },
  "errors": [],
  "results": 3,
  "paging": { "current": 1, "total": 1 },
  "response": [
    {
      "player": {
        "id": 10123,
        "name": "Mason Greenwood",
        "photo": "https://media.api-sports.io/football/players/10123.png"
      },
      "update": "2025-08-31T10:22:00+00:00",
      "transfers": [
        {
          "date": "2025-07-15",
          "type": "Loan",
          "teams": {
            "in": {
              "id": 50,
              "name": "Manchester City",
              "logo": "https://media.api-sports.io/football/teams/50.png"
            },
            "out": {
              "id": 33,
              "name": "Manchester United",
              "logo": "https://media.api-sports.io/football/teams/33.png"
            }
          }
        }
      ]
    },
    {
      "player": {
        "id": 10245,
        "name": "Phil Foden",
        "photo": "https://media.api-sports.io/football/players/10245.png"
      },
      "update": "2025-08-12T14:48:00+00:00",
      "transfers": [
        {
          "date": "2025-06-29",
          "type": "Return from Loan",
          "teams": {
            "in": {
              "id": 50,
              "name": "Manchester City",
              "logo": "https://media.api-sports.io/football/teams/50.png"
            },
            "out": {
              "id": 70,
              "name": "Leeds United",
              "logo": "https://media.api-sports.io/football/teams/70.png"
            }
          }
        },
        {
          "date": "2024-08-05",
          "type": "Loan",
          "teams": {
            "in": {
              "id": 70,
              "name": "Leeds United",
              "logo": "https://media.api-sports.io/football/teams/70.png"
            },
            "out": {
              "id": 50,
              "name": "Manchester City",
              "logo": "https://media.api-sports.io/football/teams/50.png"
            }
          }
        }
      ]
    },
    {
      "player": {
        "id": 12019,
        "name": "Julian Alvarez",
        "photo": "https://media.api-sports.io/football/players/12019.png"
      },
      "update": "2025-09-01T09:10:00+00:00",
      "transfers": [
        {
          "date": "2025-09-01",
          "type": "Free Transfer",
          "teams": {
            "in": {
              "id": 96,
              "name": "Atletico Madrid",
              "logo": "https://media.api-sports.io/football/teams/96.png"
            },
            "out": {
              "id": 50,
              "name": "Manchester City",
              "logo": "https://media.api-sports.io/football/teams/50.png"
            }
          }
        }
      ]
    }
  ]
}
          
        """.trimIndent()
    }
}