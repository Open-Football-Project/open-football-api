package org.footballproject.data.client.raw

class RawLeagueTeams {
    companion object {
        val leagueTeams = """
            {
              "get": "teams",
              "parameters": { "league": "128", "season": "2025" },
              "errors": [],
              "results": 3,
              "paging": { "current": 1, "total": 1 },
              "response": [
                {
                  "team": {
                    "id": 435,
                    "name": "River Plate",
                    "code": "RIV",
                    "country": "Argentina",
                    "founded": 1901,
                    "national": false,
                    "logo": "https://media.api-sports.io/football/teams/435.png"
                  },
                  "venue": {
                    "id": 19570,
                    "name": "Estadio Mâs Monumental",
                    "address": "Avenida Presidente José Figueroa Alcorta 7597, Núñez",
                    "city": "Buenos Aires",
                    "capacity": 83214,
                    "surface": "grass",
                    "image": "https://media.api-sports.io/football/venues/19570.png"
                  }
                },
                {
                  "team": {
                    "id": 451,
                    "name": "Boca Juniors",
                    "code": "BOC",
                    "country": "Argentina",
                    "founded": 1905,
                    "national": false,
                    "logo": "https://media.api-sports.io/football/teams/451.png"
                  },
                  "venue": {
                    "id": 46,
                    "name": "Estadio Alberto José Armando (La Bombonera)",
                    "address": "Brandsen 805, La Boca",
                    "city": "Buenos Aires",
                    "capacity": 49000,
                    "surface": "grass",
                    "image": "https://media.api-sports.io/football/venues/46.png"
                  }
                },
                {
                  "team": {
                    "id": 436,
                    "name": "Racing Club",
                    "code": "RAC",
                    "country": "Argentina",
                    "founded": 1903,
                    "national": false,
                    "logo": "https://media.api-sports.io/football/teams/436.png"
                  },
                  "venue": {
                    "id": 99,
                    "name": "Estadio Presidente Perón",
                    "address": "Calle Mozart y Orestes Omar Corbatta",
                    "city": "Avellaneda",
                    "capacity": 51500,
                    "surface": "grass",
                    "image": "https://media.api-sports.io/football/venues/99.png"
                  }
                }
              ]
            }

        """.trimIndent()
    }
}