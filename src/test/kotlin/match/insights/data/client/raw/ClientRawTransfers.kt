package match.insights.data.client.raw

class ClientRawTransfers {
    companion object {

        val rawTransfers = """
        {
          "get": "transfers",
          "parameters": { "player": "142" },
          "errors": [],
          "results": 1,
          "paging": { "current": 1, "total": 1 },
          "response": [
            {
              "player": { "id": 142, "name": "Rafinha" },
              "update": "2024-07-02T11:12:02+00:00",
              "transfers": [
                {
                  "date": "2022-09-04",
                  "type": "N/A",
                  "teams": {
                    "in": { "id": 2905, "name": "Al-Arabi SC", "logo": "https://media.api-sports.io/football/teams/2905.png" },
                    "out": { "id": 85, "name": "Paris Saint Germain", "logo": "https://media.api-sports.io/football/teams/85.png" }
                  }
                },
                {
                  "date": "2022-01-01",
                  "type": "Loan",
                  "teams": {
                    "in": { "id": 548, "name": "Real Sociedad", "logo": "https://media.api-sports.io/football/teams/548.png" },
                    "out": { "id": 85, "name": "Paris Saint Germain", "logo": "https://media.api-sports.io/football/teams/85.png" }
                  }
                },
                {
                  "date": "2018-01-22",
                  "type": "Loan",
                  "teams": {
                    "in": { "id": 505, "name": "Inter", "logo": "https://media.api-sports.io/football/teams/505.png" },
                    "out": { "id": 529, "name": "Barcelona", "logo": "https://media.api-sports.io/football/teams/529.png" }
                  }
                }
              ]
            }
          ]
        }

    """.trimIndent()
    }
}