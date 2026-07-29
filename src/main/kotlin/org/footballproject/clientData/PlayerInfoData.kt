package org.footballproject.clientData

import java.io.Serializable

data class PlayerInfoResponse(
    val player: PlayerInfo,
    val statistics: List<PlayerInfoStatistics>
) : Serializable


data class PlayerInfo(
    val id: Int?,
    val name: String?,
    val firstname: String?,
    val lastname: String?,
    val age: Int?,
    val birth: PlayerBirth?,
    val nationality: String?,
    val height: String?,
    val weight: String?,
    val injured: Boolean,
    val photo: String?
) : Serializable

data class PlayerBirth(
    val date: String?,
    val place: String?,
    val country: String?
) : Serializable


data class PlayerInfoStatistics(
    val team: PlayerInfoTeam,
    val league: PlayerInfoLeague,
    val games: PlayerInfoGames,
    val goals: PlayerInfoGoals,
    val cards: PlayerInfoCards,
    val passes: PlayerInfoPasses?,
    val shots: PlayerInfoShots?,
    val penalty: PlayerInfoPenalty?
) : Serializable

data class PlayerInfoTeam(
    val id: Int?,
    val name: String?,
    val logo: String?
) : Serializable


data class PlayerInfoLeague(
    val id: Int,
    val name: String?,
    val country: String?,
    val logo: String?,
    val flag: String?,
    val season: Int
) : Serializable

data class PlayerInfoGames(
    val appearences: Int?,
    val lineups: Int?,
    val minutes: Int?,
    val number: Int?,
    val position: String?,
    val rating: String?,
    val captain: Boolean
) : Serializable


data class PlayerInfoGoals(
    val total: Int?,
    val conceded: Int?,
    val assists: Int?,
    val saves: Int?
) : Serializable

data class PlayerInfoCards(
    val yellow: Int?,
    val yellowred: Int?,
    val red: Int?
) : Serializable

data class PlayerInfoPasses(
    val total: Int?,
    val key: Int?,
    val accuracy: String?
) : Serializable

data class PlayerInfoShots(
    val total: Int?,
    val on: Int?
) : Serializable


data class PlayerInfoPenalty(
    val won: Int?,
    val commited: Int?,
    val scored: Int?,
    val missed: Int?,
    val saved: Int?
) : Serializable







