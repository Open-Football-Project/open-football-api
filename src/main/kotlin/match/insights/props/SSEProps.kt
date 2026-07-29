package match.insights.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "sse")
data class SSEProps(
    val initialDelayInMinutes: Long,
    val refreshIntervalMinutes: Long,
    val matchTimeOutMs: Long
)