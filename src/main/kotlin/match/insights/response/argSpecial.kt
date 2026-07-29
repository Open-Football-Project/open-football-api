package match.insights.response

import match.insights.model.ArgLeagueEntry

data class ArgSpecial(
    val annualTable: List<ArgLeagueEntry>,
    val promediosTable: List<ArgLeagueEntry>,
)