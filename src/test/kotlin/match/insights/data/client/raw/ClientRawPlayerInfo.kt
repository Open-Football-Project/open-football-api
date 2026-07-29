package match.insights.data.client.raw

class ClientRawPlayerInfo {
    companion object {
        val playerInfoResponse = """
    {
      "response": [
        {
          "player": {
            "id": 2273,
            "name": "Kepa",
            "firstname": "Kepa",
            "lastname": "Arrizabalaga Revuelta",
            "age": 31,
            "birth": {
              "date": "1994-10-03",
              "place": "Ondárroa",
              "country": "Spain"
            },
            "nationality": "Spain",
            "height": "189",
            "weight": "84",
            "injured": false,
            "photo": "https://media.api-sports.io/football/players/2273.png"
          },
          "statistics": [
            {
              "team": {
                "id": 42,
                "name": "Arsenal",
                "logo": "https://media.api-sports.io/football/teams/42.png"
              },
              "league": {
                "id": 39,
                "name": "Premier League",
                "country": "England",
                "logo": "https://media.api-sports.io/football/leagues/39.png",
                "flag": "https://media.api-sports.io/flags/gb-eng.svg",
                "season": 2025
              },
              "games": {
                "appearences": 0,
                "lineups": 0,
                "minutes": null,
                "number": 13,
                "position": "Goalkeeper",
                "rating": null,
                "captain": false
              },
              "goals": {
                "total": 0,
                "conceded": 0,
                "assists": 0,
                "saves": null
              },
              "cards": {
                "yellow": 0,
                "yellowred": 0,
                "red": 0
              }
            }
          ]
        }
      ]
    }
    """.trimIndent()

        // Real api-sports.io response for player 340124 (season 2025), trimmed to 2 of 6 statistics
        // entries. This is the exact player/season that crashed production (see apilogs:
        // "Failed to fetch player info for player 340124", MissingKotlinParameterException on
        // statistics[5].team.name). The second statistics entry below has team.id/name/logo all null.
        val playerInfoResponseWithMissingTeamData = """
    {
      "response": [
        {
          "player": {
            "id": 340124,
            "name": "A. Toivonen",
            "firstname": "Aaro",
            "lastname": "Toivonen",
            "age": 20,
            "birth": {
              "date": "2005-04-19",
              "place": null,
              "country": "Finland"
            },
            "nationality": "Finland",
            "height": null,
            "weight": null,
            "injured": false,
            "photo": "https://media.api-sports.io/football/players/340124.png"
          },
          "statistics": [
            {
              "team": {
                "id": 649,
                "name": "HJK Helsinki",
                "logo": "https://media.api-sports.io/football/teams/649.png"
              },
              "league": {
                "id": 244,
                "name": "Veikkausliiga",
                "country": "Finland",
                "logo": "https://media.api-sports.io/football/leagues/244.png",
                "flag": "https://media.api-sports.io/flags/fi.svg",
                "season": 2025
              },
              "games": {
                "appearences": 0,
                "lineups": 0,
                "minutes": 0,
                "number": null,
                "position": "Midfielder",
                "rating": null,
                "captain": false
              },
              "goals": {
                "total": 0,
                "conceded": null,
                "assists": null,
                "saves": null
              },
              "cards": {
                "yellow": 0,
                "yellowred": 0,
                "red": 0
              }
            },
            {
              "team": {
                "id": null,
                "name": null,
                "logo": null
              },
              "league": {
                "id": 244,
                "name": "Veikkausliiga",
                "country": "Finland",
                "logo": "https://media.api-sports.io/football/leagues/244.png",
                "flag": "https://media.api-sports.io/flags/fi.svg",
                "season": 2025
              },
              "games": {
                "appearences": 13,
                "lineups": 9,
                "minutes": 564,
                "number": 24,
                "position": "Midfielder",
                "rating": "6.71",
                "captain": false
              },
              "goals": {
                "total": 0,
                "conceded": 0,
                "assists": 2,
                "saves": null
              },
              "cards": {
                "yellow": 0,
                "yellowred": 0,
                "red": 0
              }
            }
          ]
        }
      ]
    }
    """.trimIndent()

        // Synthetic (not observed in production) - covers sibling fields with the same risk shape
        // as team.id/name above: player.id, player.name, and league.name all omitted.
        val playerInfoResponseWithMissingIdentityFields = """
    {
      "response": [
        {
          "player": {
            "firstname": "Kepa",
            "lastname": "Arrizabalaga Revuelta",
            "age": 31,
            "nationality": "Spain",
            "height": "189",
            "weight": "84",
            "injured": false,
            "photo": "https://media.api-sports.io/football/players/2273.png"
          },
          "statistics": [
            {
              "team": {
                "id": 42,
                "name": "Arsenal",
                "logo": "https://media.api-sports.io/football/teams/42.png"
              },
              "league": {
                "id": 39,
                "country": "England",
                "logo": "https://media.api-sports.io/football/leagues/39.png",
                "flag": "https://media.api-sports.io/flags/gb-eng.svg",
                "season": 2025
              },
              "games": {
                "appearences": 0,
                "lineups": 0,
                "minutes": null,
                "number": 13,
                "position": "Goalkeeper",
                "rating": null,
                "captain": false
              },
              "goals": {
                "total": 0,
                "conceded": 0,
                "assists": 0,
                "saves": null
              },
              "cards": {
                "yellow": 0,
                "yellowred": 0,
                "red": 0
              }
            }
          ]
        }
      ]
    }
    """.trimIndent()
    }
}