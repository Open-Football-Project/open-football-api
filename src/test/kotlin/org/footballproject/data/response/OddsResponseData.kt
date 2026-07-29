package org.footballproject.data.response

import org.footballproject.response.Bet
import org.footballproject.response.SingleOdd

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