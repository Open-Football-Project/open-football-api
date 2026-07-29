package match.insights.data.client

import match.insights.clientData.PlayerTrophy

class ClientPlayerTrophies {
    companion object {

        val trophies = listOf(
            PlayerTrophy(
                league = "Qatar-UAE Super Cup",
                country = "Asia",
                season = "2024",
                place = "Winner"
            ),
            PlayerTrophy(
                league = "QSL Cup",
                country = "Qatar",
                season = "2023/2024",
                place = "2nd Place"
            ),
            PlayerTrophy(
                league = "Emir Cup",
                country = "Qatar",
                season = "2023",
                place = "Winner"
            ),
            PlayerTrophy(
                league = "Trophée des Champions",
                country = "France",
                season = "2022/2023",
                place = "Winner"
            ),
            PlayerTrophy(
                league = "La Liga",
                country = "Spain",
                season = "2014/2015",
                place = "Winner"
            )
        )

    }
}