package org.footballproject.props

import org.footballproject.response.AvailablePoll
import org.footballproject.response.AvailablePollOption
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "availablepolls")
data class PollsProps(
    val polls: Map<String, PollDefinition> = emptyMap()
) {
    fun availablePolls(): List<AvailablePoll> =
        polls.map { (pollKey, def) ->
            AvailablePoll(
                pollKey = pollKey,
                pollTitle = def.title,
                pollOptions = def.options.map { (key, title) ->
                    AvailablePollOption(optionName = key, optionTitle = title)
                }
            )
        }

    fun availablePoll(pollKey: String): PollDefinition? = polls[pollKey]

    fun pollKeys(): List<String> = polls.map { it.key }

}

data class PollDefinition(
    val title: String,
    val options: Map<String, String>
)