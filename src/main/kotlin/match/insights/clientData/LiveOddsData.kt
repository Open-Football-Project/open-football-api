package match.insights.clientData

import java.io.Serializable

data class LiveFixtureOdds(val odds: List<LiveOddsMarket>) : Serializable
data class LiveOddsMarket(val id: Int, val name: String, val values: List<LiveOddsValue>) : Serializable
data class LiveOddsValue(
    val value: String,
    val odd: String,
    val handicap: String?,
    val main: Boolean?,
    val suspended: Boolean
) : Serializable
