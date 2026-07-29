package org.footballproject.data.client.raw

class ClientRawPlayerTrophies {
    companion object {
        val playerTrophies = """
            {
              "get": "trophies",
              "parameters": { "player": "142" },
              "errors": [],
              "results": 5,
              "paging": { "current": 1, "total": 1 },
              "response": [
                { "league": "Qatar-UAE Super Cup", "country": "Asia", "season": "2024", "place": "Winner" },
                { "league": "QSL Cup", "country": "Qatar", "season": "2023/2024", "place": "2nd Place" },
                { "league": "Emir Cup", "country": "Qatar", "season": "2023", "place": "Winner" },
                { "league": "Trophée des Champions", "country": "France", "season": "2022/2023", "place": "Winner" },
                { "league": "La Liga", "country": "Spain", "season": "2014/2015", "place": "Winner" }
              ]
            }

        """.trimIndent()
    }
}