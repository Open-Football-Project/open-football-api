package org.footballproject.data.client.raw

class ClientLiveOddsData {
    companion object {
        val liveOddsResponse = """
            {
              "get": "odds/live",
              "parameters": { "fixture": "1539007" },
              "errors": [],
              "results": 1,
              "paging": { "current": 1, "total": 1 },
              "response": [
                {
                  "league": {
                    "id": 39, "name": "Premier League", "country": "England",
                    "logo": "", "flag": "", "season": 2026, "round": "Regular Season - 38"
                  },
                  "fixture": {
                    "id": 1539007,
                    "timezone": "UTC",
                    "date": "2026-06-20T15:00:00+00:00",
                    "timestamp": 1750431600,
                    "status": { "elapsed": 23, "long": "First Half" }
                  },
                  "update": "2026-06-20T15:23:00+00:00",
                  "odds": [
                    {
                      "id": 59,
                      "name": "Fulltime Result",
                      "values": [
                        { "value": "Home", "odd": "1.615", "handicap": null, "main": null, "suspended": false },
                        { "value": "Draw", "odd": "4.250", "handicap": null, "main": null, "suspended": false },
                        { "value": "Away", "odd": "5.500", "handicap": null, "main": null, "suspended": false }
                      ]
                    },
                    {
                      "id": 33,
                      "name": "3-Way Handicap",
                      "values": [
                        { "value": "Home -1", "odd": "2.100", "handicap": "-1", "main": true,  "suspended": false },
                        { "value": "Draw -1", "odd": "5.000", "handicap": "-1", "main": true,  "suspended": false },
                        { "value": "Away +1", "odd": "1.900", "handicap": "-1", "main": true,  "suspended": false },
                        { "value": "Home -2", "odd": "4.500", "handicap": "-2", "main": false, "suspended": false }
                      ]
                    },
                    {
                      "id": 20,
                      "name": "Match Corners",
                      "values": [
                        { "value": "Over 9.5",  "odd": "1.850", "handicap": null, "main": null, "suspended": false },
                        { "value": "Under 9.5", "odd": "1.950", "handicap": null, "main": null, "suspended": true }
                      ]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
    }
}
