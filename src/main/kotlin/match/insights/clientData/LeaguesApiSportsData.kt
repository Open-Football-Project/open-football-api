package match.insights.clientData

import java.io.Serializable

data class LeagueWithStandings(
    val id: Int,
    val name: String? = "Unknown League",
    val country: String? = "Unknown Country",
    val logo: String? = "",
    val flag: String? = "",
    val season: Int,
    val standings: List<List<LeagueStandings>>
) : Serializable

data class LeagueStandings(
    val rank: Int,
    val team: Team,
    val points: Int,
    val goalsDiff: Int,
    val form: String? = "",
    val status: String? = "",
    val description: String? = "",
    val group: String? = "",
    val all: LeagueRecordStats? = LeagueRecordStats(),
    val home: LeagueRecordStats? = LeagueRecordStats(),
    val away: LeagueRecordStats? = LeagueRecordStats(),
    val update: String? = ""
) : Serializable

data class LeagueRecordStats(
    val played: Int? = 0,
    val win: Int? = 0,
    val draw: Int? = 0,
    val lose: Int? = 0,
    val goals: LeagueGoals? = LeagueGoals()
) : Serializable


data class LeagueGoals(
    val `for`: Int? = 0,
    val against: Int? = 0
) : Serializable

