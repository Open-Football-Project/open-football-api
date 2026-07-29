package match.insights.clientData

import java.io.Serializable

data class FixtureOdds(val bookmakers: List<Bookmaker>) : Serializable

data class Bookmaker(val name: String, val bets: List<Bet>) : Serializable

data class Bet(val name: String, val values: List<OddValue>) : Serializable

data class OddValue(val value: String, val odd: String) : Serializable
