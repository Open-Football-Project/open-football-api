package match.insights.data.response

import match.insights.response.Bet
import match.insights.response.SingleOdd

class OddsResponseData {
    companion object {
        val bets = listOf<Bet>(
            Bet(
                betName = "Match Winner", values = listOf(
                    SingleOdd(label = "Home Team", odd = 1.95),
                )
            ), Bet(
                betName = "Odd/Even - First Half", values = listOf(
                    SingleOdd(label = "Odd", odd = 2.05)
                )
            )
        )
    }
}