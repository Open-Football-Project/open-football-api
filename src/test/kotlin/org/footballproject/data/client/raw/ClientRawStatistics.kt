package org.footballproject.data.client.raw

class ClientRawStatistics {
    companion object {
        val mockStatisticsJson = """
{
  "get": "fixtures/statistics",
  "parameters": {
    "fixture": "1457376"
  },
  "errors": [],
  "results": 2,
  "paging": {
    "current": 1,
    "total": 1
  },
  "response": [
    {
      "team": {
        "id": 5539,
        "name": "Puerto Rico",
        "logo": "https://media.api-sports.io/football/teams/5539.png"
      },
      "statistics": [
        { "type": "Shots on Goal", "value": 3 },
        { "type": "Shots off Goal", "value": 1 },
        { "type": "Total Shots", "value": 5 },
        { "type": "Blocked Shots", "value": 1 },
        { "type": "Shots insidebox", "value": 1 },
        { "type": "Shots outsidebox", "value": 4 },
        { "type": "Fouls", "value": 5 },
        { "type": "Corner Kicks", "value": 1 },
        { "type": "Offsides", "value": 3 },
        { "type": "Ball Possession", "value": "32%" },
        { "type": "Yellow Cards", "value": null },
        { "type": "Red Cards", "value": null },
        { "type": "Goalkeeper Saves", "value": 5 },
        { "type": "Total passes", "value": 329 },
        { "type": "Passes accurate", "value": 273 },
        { "type": "Passes %", "value": "83%" },
        { "type": "expected_goals", "value": null },
        { "type": "goals_prevented", "value": null }
      ]
    },
    {
      "team": {
        "id": 26,
        "name": "Argentina",
        "logo": "https://media.api-sports.io/football/teams/26.png"
      },
      "statistics": [
        { "type": "Shots on Goal", "value": 11 },
        { "type": "Shots off Goal", "value": 7 },
        { "type": "Total Shots", "value": 24 },
        { "type": "Blocked Shots", "value": 6 },
        { "type": "Shots insidebox", "value": 15 },
        { "type": "Shots outsidebox", "value": 9 },
        { "type": "Fouls", "value": 24 },
        { "type": "Corner Kicks", "value": 1 },
        { "type": "Offsides", "value": 3 },
        { "type": "Ball Possession", "value": "68%" },
        { "type": "Yellow Cards", "value": null },
        { "type": "Red Cards", "value": null },
        { "type": "Goalkeeper Saves", "value": 3 },
        { "type": "Total passes", "value": 719 },
        { "type": "Passes accurate", "value": 659 },
        { "type": "Passes %", "value": "92%" },
        { "type": "expected_goals", "value": null },
        { "type": "goals_prevented", "value": null }
      ]
    }
  ]
}
""".trimIndent()

    }
}