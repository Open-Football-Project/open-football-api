package org.footballproject.repository

import org.footballproject.model.Poll

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class PollRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun savePoll(poll: Poll, ttl: Duration) {
        redisTemplate.opsForValue().set(buildKey(poll.pollKey, poll.fixtureId), poll, ttl)
    }

    fun getPoll(pollKey: String, fixtureId: Int): Poll? {
        return redisTemplate.opsForValue().get(buildKey(pollKey, fixtureId)) as? Poll
    }

    private fun buildKey(pollKey: String, fixtureId: Int) = "poll:$pollKey:$fixtureId"
}