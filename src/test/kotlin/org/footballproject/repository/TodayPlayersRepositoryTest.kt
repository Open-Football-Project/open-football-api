package org.footballproject.repository

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.footballproject.clientData.SquadPlayer
import org.footballproject.model.PlayerPosition
import org.footballproject.model.PlayerScore
import org.footballproject.model.ScoreReason
import org.footballproject.model.ScoringSignal
import org.footballproject.model.TodayPlayersWatchlist
import org.footballproject.props.TodayPlayersProps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class TodayPlayersRepositoryTest {

    private val redisTemplate: RedisTemplate<String, Any> = mockk()
    private val valueOps = mockk<ValueOperations<String, Any>>()
    private val props = TodayPlayersProps(ttlSeconds = 86_400)

    private val underTest = TodayPlayersRepository(redisTemplate, props)

    private val messi = SquadPlayer(id = 154, name = "L. Messi", age = 38, number = 10, position = "Attacker", photo = null)

    private val watchlist = TodayPlayersWatchlist(
        fixtureId = 1576804,
        leagueId = 1,
        leagueName = "World Cup",
        homeTeamName = "Argentina",
        awayTeamName = "Egypt",
        home = mapOf(
            PlayerPosition.ATTACKER to listOf(
                PlayerScore(messi, 0.6, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(listOf("Anytime Goal Scorer")))
            )
        ),
        away = emptyMap()
    )

    @Test
    fun shouldSaveTheWatchlistUnderAFixtureScopedKeyWithTheConfiguredTtl() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.set("today-players:1576804", watchlist, Duration.ofSeconds(86_400)) } just runs

        underTest.save(watchlist)

        verify { valueOps.set("today-players:1576804", watchlist, Duration.ofSeconds(86_400)) }
    }

    @Test
    fun shouldReturnTheCachedWatchlistForAFixture() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("today-players:1576804") } returns watchlist

        val result = underTest.get(1576804)

        assertThat(result).isEqualTo(watchlist)
    }

    @Test
    fun shouldReturnNullWhenNoWatchlistIsCachedForThatFixture() {
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("today-players:1576804") } returns null

        val result = underTest.get(1576804)

        assertThat(result).isNull()
    }

    @Test
    fun shouldReportTrueWhenAWatchlistAlreadyExistsForTheFixture() {
        every { redisTemplate.hasKey("today-players:1576804") } returns true

        assertThat(underTest.exists(1576804)).isTrue()
    }

    @Test
    fun shouldReportFalseWhenNoWatchlistExistsForTheFixtureYet() {
        every { redisTemplate.hasKey("today-players:1576804") } returns false

        assertThat(underTest.exists(1576804)).isFalse()
    }

    @Test
    fun shouldReturnAllCurrentlyCachedWatchlistsAcrossFixtures() {
        every { redisTemplate.keys("today-players:*") } returns setOf("today-players:1576804")
        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get("today-players:1576804") } returns watchlist

        assertThat(underTest.getAllFixtures()).containsExactly(watchlist)
    }

    @Test
    fun shouldReturnEmptyListWhenNothingIsCachedYet() {
        every { redisTemplate.keys("today-players:*") } returns emptySet()

        assertThat(underTest.getAllFixtures()).isEmpty()
    }
}
