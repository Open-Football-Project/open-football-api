package org.footballproject.clientData

import java.io.Serializable

data class Player(
    val id: Int,
    val name: String,
    val age: Int? = -1,
    val height: String? = "Unknown",
    val weight: String? = "Unknown",
    val photo: String? = "",
    val firstname: String? = "Unknown",
    val lastname: String? = "Unknown",
    val nationality: String? = "Unknown"
) : Serializable

data class PlayerStatistic(
    val team: Team,
    val games: PlayerGameStats,
    val goals: PlayerGoalStats,
    val cards: PlayerCardStats,
    val penalty: PlayerPenaltyStats
) : Serializable


data class PlayerGameStats(
    val position: String?,
    val appearences: Int?,
    val minutes: Int?
) : Serializable

data class PlayerGoalStats(
    val total: Int? = 0,
    val assists: Int? = 0,
    val conceded: Int? = 0,
    val saves: Int? = 0
) : Serializable

data class PlayerCardStats(
    val yellow: Int? = 0,
    val red: Int? = 0
) : Serializable

data class PlayerPenaltyStats(
    val saved: Int? = 0,
    val won: Int? = 0,
    val commited: Int? = 0,
    val scored: Int? = 0,
    val missed: Int? = 0
) : Serializable

data class PlayerTrophy(
    val league: String,
    val country: String,
    val season: String?,
    val place: String
) : Serializable

