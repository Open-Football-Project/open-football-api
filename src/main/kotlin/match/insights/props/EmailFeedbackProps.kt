package match.insights.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "feedback")
data class EmailFeedbackProps(
    val from: String,
    val recipients: Set<String>
)