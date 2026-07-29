package match.insights.repository

import match.insights.model.TodayPlayersWatchlist
import match.insights.props.TodayPlayersProps
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class TodayPlayersRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val props: TodayPlayersProps
) {

    fun save(watchlist: TodayPlayersWatchlist) {
        redisTemplate.opsForValue()
            .set(buildKey(watchlist.fixtureId), watchlist, Duration.ofSeconds(props.ttlSeconds))
    }

    fun get(fixtureId: Int): TodayPlayersWatchlist? =
        redisTemplate.opsForValue().get(buildKey(fixtureId)) as? TodayPlayersWatchlist

    fun exists(fixtureId: Int): Boolean = redisTemplate.hasKey(buildKey(fixtureId)) == true

    fun getAllFixtures(): List<TodayPlayersWatchlist> =
        (redisTemplate.keys(ALL_FIXTURES_PATTERN) ?: emptySet())
            .mapNotNull { redisTemplate.opsForValue().get(it) as? TodayPlayersWatchlist }

    private fun buildKey(fixtureId: Int) = "today-players:$fixtureId"

    companion object {
        private const val ALL_FIXTURES_PATTERN = "today-players:*"
    }
}
