package match.insights.controller

import match.insights.request.VotingPoll
import match.insights.service.PollsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/polls")
class PollsController(private val pollsService: PollsService) {

    @PostMapping("/vote")
    @ResponseStatus(HttpStatus.CREATED)
    fun voteWinner(
        @RequestBody
        votingPoll: VotingPoll,
    ) = pollsService.vote(votingPoll)

    @GetMapping("/available")
    fun availablePolls() = pollsService.availablePolls()

}