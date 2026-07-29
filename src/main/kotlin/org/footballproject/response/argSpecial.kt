package org.footballproject.response

import org.footballproject.model.ArgLeagueEntry

data class ArgSpecial(
    val annualTable: List<ArgLeagueEntry>,
    val promediosTable: List<ArgLeagueEntry>,
)