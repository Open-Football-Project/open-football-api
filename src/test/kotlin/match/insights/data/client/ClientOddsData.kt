package match.insights.data.client

import match.insights.clientData.Bet
import match.insights.clientData.Bookmaker
import match.insights.clientData.FixtureOdds
import match.insights.clientData.OddValue

class ClientOddsData {
    companion object {
        val mockMultiBookmakerResponse = listOf(
            FixtureOdds(
                bookmakers = listOf(
                    Bookmaker(
                        name = "AlphaBook", bets = listOf(
                            Bet(name = "Match Winner", values = listOf(
                                OddValue("Home", "2.00"), OddValue("Draw", "3.00"), OddValue("Away", "3.50")
                            )),
                            Bet(name = "Goals Over/Under", values = listOf(
                                OddValue("Over 1.5", "1.25"), OddValue("Under 1.5", "3.80"),
                                OddValue("Over 2.5", "1.70"), OddValue("Under 2.5", "2.10"),
                                OddValue("Over 3.5", "2.50"), OddValue("Under 3.5", "1.55")
                            )),
                            Bet(name = "Both Teams Score", values = listOf(
                                OddValue("Yes", "1.70"), OddValue("No", "2.05")
                            )),
                            Bet(name = "Double Chance", values = listOf(
                                OddValue("Home/Draw", "1.20"), OddValue("Home/Away", "1.25"), OddValue("Draw/Away", "2.10")
                            )),
                            Bet(name = "First Half Winner", values = listOf(
                                OddValue("Home", "2.30"), OddValue("Draw", "2.25"), OddValue("Away", "4.40")
                            )),
                            Bet(name = "Both Teams Score - First Half", values = listOf(
                                OddValue("Yes", "2.80"), OddValue("No", "1.38")
                            )),
                            Bet(name = "Both Teams To Score - Second Half", values = listOf(
                                OddValue("Yes", "2.60"), OddValue("No", "1.45")
                            )),
                            Bet(name = "Goals Over/Under First Half", values = listOf(
                                OddValue("Over 0.5", "1.55"), OddValue("Under 0.5", "2.40"),
                                OddValue("Over 1.5", "3.10"), OddValue("Under 1.5", "1.35")
                            )),
                            Bet(name = "Corners Over Under", values = listOf(
                                OddValue("Over 9.5", "1.85"), OddValue("Under 9.5", "1.95")
                            ))
                        )
                    ),
                    Bookmaker(
                        name = "BetaBook", bets = listOf(
                            Bet(name = "Match Winner", values = listOf(
                                OddValue("Home", "1.90"), OddValue("Draw", "3.20"), OddValue("Away", "5.00")
                            )),
                            Bet(name = "Goals Over/Under", values = listOf(
                                OddValue("Over 2.5", "1.65"), OddValue("Under 2.5", "2.20")
                            )),
                            Bet(name = "Both Teams Score", values = listOf(
                                OddValue("Yes", "1.75"), OddValue("No", "2.00")
                            )),
                            Bet(name = "Double Chance", values = listOf(
                                OddValue("Home/Draw", "1.22"), OddValue("Home/Away", "1.28"), OddValue("Draw/Away", "2.05")
                            )),
                            Bet(name = "First Half Winner", values = listOf(
                                OddValue("Home", "2.25"), OddValue("Draw", "2.30"), OddValue("Away", "4.50")
                            )),
                            Bet(name = "Both Teams Score - First Half", values = listOf(
                                OddValue("Yes", "2.90"), OddValue("No", "1.35")
                            )),
                            Bet(name = "Both Teams To Score - Second Half", values = listOf(
                                OddValue("Yes", "2.55"), OddValue("No", "1.48")
                            )),
                            Bet(name = "Goals Over/Under First Half", values = listOf(
                                OddValue("Over 0.5", "1.60"), OddValue("Under 0.5", "2.30")
                            )),
                            Bet(name = "Corners Over Under", values = listOf(
                                OddValue("Over 9.5", "1.90"), OddValue("Under 9.5", "1.90")
                            ))
                        )
                    ),
                    Bookmaker(
                        name = "GammaBook", bets = listOf(
                            Bet(name = "Match Winner", values = listOf(
                                OddValue("Home", "2.05"), OddValue("Draw", "3.10"), OddValue("Away", "3.60")
                            )),
                            Bet(name = "Goals Over/Under", values = listOf(
                                OddValue("Over 2.5", "1.80"), OddValue("Under 2.5", "2.00")
                            )),
                            Bet(name = "Both Teams Score", values = listOf(
                                OddValue("Yes", "1.68"), OddValue("No", "2.10")
                            )),
                            Bet(name = "Double Chance", values = listOf(
                                OddValue("Home/Draw", "1.18"), OddValue("Home/Away", "1.22"), OddValue("Draw/Away", "2.08")
                            )),
                            Bet(name = "First Half Winner", values = listOf(
                                OddValue("Home", "2.35"), OddValue("Draw", "2.28"), OddValue("Away", "4.30")
                            )),
                            Bet(name = "Both Teams Score - First Half", values = listOf(
                                OddValue("Yes", "2.75"), OddValue("No", "1.40")
                            )),
                            Bet(name = "Both Teams To Score - Second Half", values = listOf(
                                OddValue("Yes", "2.65"), OddValue("No", "1.43")
                            )),
                            Bet(name = "Goals Over/Under First Half", values = listOf(
                                OddValue("Over 0.5", "1.58"), OddValue("Under 0.5", "2.35")
                            )),
                            Bet(name = "Corners Over Under", values = listOf(
                                OddValue("Over 9.5", "1.88"), OddValue("Under 9.5", "1.92")
                            ))
                        )
                    )
                )
            )
        )

        val mockResponse = listOf(
            FixtureOdds(
                bookmakers = listOf(
                    Bookmaker(
                        name = "Bet365", bets = listOf(
                            Bet(
                                name = "Match Winner", values = listOf(
                                    OddValue("Home Team", "1.05"), OddValue("Draw", "2.0"), OddValue("Away Team", "3.8")
                                )
                            ), Bet(
                                name = "Odd/Even - First Half", values = listOf(
                                    OddValue("Odd", "2.05"), OddValue("Even", "1.8")
                                )
                            )
                        )
                    )
                )
            )
        )


    }
}