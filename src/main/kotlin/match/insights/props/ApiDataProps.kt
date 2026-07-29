package match.insights.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api-data")
data class ApiDataProps(
    val url: String,
    val key: String,
)