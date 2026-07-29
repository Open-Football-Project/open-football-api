package org.footballproject.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.footballproject.model.Poll
import org.footballproject.model.PollVotingOption
import org.footballproject.props.PollDefinition
import org.footballproject.props.PollsProps
import org.footballproject.repository.PollRepository
import org.footballproject.request.VotingPoll
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test

class PollsServiceTest {

    val pollRepository: PollRepository = mockk()
    val pollsProps = PollsProps(
        mapOf(
            "match-winner" to PollDefinition(
                "Match Winner?",
                mapOf(
                    "a" to "a"
                )
            )
        )
    )

    val underTest = PollsService(pollRepository, pollsProps)

    @Test
    fun shouldSaveAMatchWinnerPoll() {
        val poll = Poll(
            pollTitle = "Match Winner?",
            "match-winner", 233, listOf(
                PollVotingOption("a", "a", 1),
            )
        )



        every { pollRepository.getPoll(any(), any()) } returns null
        every { pollRepository.savePoll(poll, any()) } just runs

        underTest.vote(VotingPoll(233, "match-winner", "a"))

        verify { pollRepository.savePoll(poll, any()) }
    }

    @Test
    fun shouldUpdateAMatchWinnerPoll() {
        val poll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 2)
            )
        )

        val updatedPoll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 3)
            )
        )

        every { pollRepository.getPoll(any(), any()) } returns poll
        every { pollRepository.savePoll(updatedPoll, any()) } just runs

        underTest.vote(VotingPoll(233, "match-winner", "home"))

        verify { pollRepository.savePoll(updatedPoll, any()) }
    }

    @Test
    fun shouldGetAllPollsForMatch() {
        val poll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 2)
            )
        )


        every { pollRepository.getPoll("match-winner", 233) } returns poll

        val result = underTest.getPolls(233)

        assertThat(result).isEqualTo(listOf(poll))

        verify { pollRepository.getPoll("match-winner", 233) }

    }
}