package org.footballproject.repository

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.footballproject.model.Poll
import org.footballproject.model.PollVotingOption
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class PollRepositoryTest {

    private val redisTemplate: RedisTemplate<String, Any> = mockk()

    private val underTest = PollRepository(redisTemplate)
    private val valueOps = mockk<ValueOperations<String, Any>>()


    @Test
    fun shouldSaveAPoll() {
        val ttl = Duration.ofMinutes(120)
        val poll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 2)
            )
        )

        every { redisTemplate.opsForValue() } returns valueOps
        every {
            valueOps.set("poll:match-winner:${poll.fixtureId}", poll, ttl)
        } just runs

        underTest.savePoll(poll, ttl)

        verify {
            valueOps.set("poll:match-winner:${poll.fixtureId}", poll, ttl)
        }
    }

    @Test
    fun shouldGetAPoll() {
        val poll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 2)
            )
        )

        every { redisTemplate.opsForValue() } returns valueOps
        every {
            valueOps.get("poll:match-winner:${poll.fixtureId}")
        } returns poll

        underTest.getPoll("match-winner", 233)

        verify {
            valueOps.get("poll:match-winner:${poll.fixtureId}")
        }
    }
}