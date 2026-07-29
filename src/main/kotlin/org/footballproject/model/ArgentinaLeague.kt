package org.footballproject.model

data class ArgLeagueEntry(
    val teamId: Int,
    val teamName: String,
    val teamLogo: String,
    val points: Int = 0,
    val played: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val goalDifference: Int = 0,
    val promedio: Double = 0.0
)
