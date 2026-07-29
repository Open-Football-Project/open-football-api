package org.footballproject.response

data class AvailablePollOption(
    val optionName: String,
    val optionTitle: String
)

data class AvailablePoll(
    val pollKey: String,
    val pollTitle: String,
    val pollOptions: List<AvailablePollOption>
)