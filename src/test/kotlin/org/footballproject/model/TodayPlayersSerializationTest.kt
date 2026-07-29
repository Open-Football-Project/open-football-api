package org.footballproject.model

import org.footballproject.clientData.SquadPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * TodayPlayersRepository stores watchlists in Redis via JdkSerializationRedisSerializer
 * (see CacheConfig), which requires the full object graph to implement Serializable.
 * This exercises real Java serialization, not a mock, to catch that requirement directly.
 */
class TodayPlayersSerializationTest {

    private fun <T> roundTrip(value: T): T {
        val bytes = ByteArrayOutputStream().use { byteStream ->
            ObjectOutputStream(byteStream).use { it.writeObject(value) }
            byteStream.toByteArray()
        }
        return ObjectInputStream(ByteArrayInputStream(bytes)).use {
            @Suppress("UNCHECKED_CAST")
            it.readObject() as T
        }
    }

    @Test
    fun `a today-players watchlist survives real Java serialization, as required for Redis storage`() {
        val messi = SquadPlayer(id = 154, name = "L. Messi", age = 38, number = 10, position = "Attacker", photo = null)
        val salah = SquadPlayer(id = 301, name = "M. Salah", age = 33, number = 11, position = "Attacker", photo = null)
        val watchlist = TodayPlayersWatchlist(
            fixtureId = 1576804,
            leagueId = 1,
            leagueName = "Test League",
            homeTeamName = "Argentina",
            awayTeamName = "Egypt",
            home = mapOf(
                PlayerPosition.ATTACKER to listOf(
                    PlayerScore(messi, 0.6, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(listOf("Anytime Goal Scorer")))
                )
            ),
            away = mapOf(
                PlayerPosition.ATTACKER to listOf(
                    PlayerScore(salah, 0.5, ScoringSignal.SEASON_STAT, ScoreReason.SeasonForm(10, 5, 2, 7.0))
                )
            )
        )

        val result = roundTrip(watchlist)

        assertThat(result).isEqualTo(watchlist)
    }
}
