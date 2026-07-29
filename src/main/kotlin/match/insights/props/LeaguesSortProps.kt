package match.insights.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "leagues-sorting")
data class LeaguesSortProps(
    val countries: List<String>,
    val international: List<String>,
    val england: List<String>,
    val argentina: List<String>,
    val spain: List<String>,
    val italy: List<String>,
    val usa: List<String>,
    val france: List<String>,
    val uruguay: List<String>,
    val germany: List<String>,
    val portugal: List<String>,
    val brazil: List<String>
)