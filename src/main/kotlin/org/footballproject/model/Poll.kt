package org.footballproject.model

import java.io.Serializable

data class Poll(
    val pollTitle: String,
    val pollKey: String,
    val fixtureId: Int,
    val pollVotingOptions: List<PollVotingOption>
) : Serializable

data class PollVotingOption(
    val optionName: String,
    val optionTitle: String,
    val value: Int
) : Serializable