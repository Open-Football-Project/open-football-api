package match.insights.service

import match.insights.model.Poll
import match.insights.model.PollVotingOption
import match.insights.props.PollsProps
import match.insights.repository.PollRepository
import match.insights.request.VotingPoll
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PollsService(
    private val pollRepository: PollRepository,
    private val pollsProps: PollsProps
) {

    fun vote(votingPoll: VotingPoll) {
        val foundPoll: Poll? = pollRepository.getPoll(votingPoll.pollKey, votingPoll.fixtureId)
        foundPoll?.let {
            pollRepository.savePoll(updatedPoll(votingPoll, foundPoll), pollTTL)
        } ?: newPoll(votingPoll)?.let {
            pollRepository.savePoll(it, pollTTL)
        }
    }

    fun getPolls(fixtureId: Int): List<Poll> = pollsProps.pollKeys().mapNotNull {
        pollRepository.getPoll(it, fixtureId)
    }

    fun availablePolls() = pollsProps.availablePolls()


    private fun newPoll(votingPoll: VotingPoll): Poll? {
        val pollDefinition = pollsProps.availablePoll(votingPoll.pollKey)

        return pollDefinition?.let { pollDefinition ->

            Poll(
                fixtureId = votingPoll.fixtureId,
                pollKey = votingPoll.pollKey,
                pollTitle = pollDefinition.title,
                pollVotingOptions = pollDefinition.options.map { (name, title) ->
                    PollVotingOption(
                        optionName = name,
                        optionTitle = title,
                        value = if (name == votingPoll.optionName) 1 else 0
                    )
                })
        }
    }


    private fun updatedPoll(votingPoll: VotingPoll, poll: Poll): Poll {
        return poll.copy(
            pollVotingOptions = poll.pollVotingOptions.map { option ->
                option.copy(
                    value = if (option.optionName == votingPoll.optionName) (option.value + 1) else option.value
                )
            }
        )
    }

    companion object {
        val pollTTL = Duration.ofMinutes(120);
    }
}