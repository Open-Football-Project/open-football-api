package match.insights.response.game

import match.insights.clientData.LeagueAndCountry
import match.insights.clientData.PlayerTrophy

data class GuessThePlayerGameData(
    val isAvailable: Boolean = false,
    val playerId: Int = -1,
    val playerName: String = "",
    val playerNationality: String = "",
    val playerPosition: String = "",
    val playerPhoto: String? = "",
    val options: Set<String> = emptySet(),
    val hints: List<GuessThePlayerGameHint> = emptyList()
)

data class GuessThePlayerGameHint(
    val hintKey: GuessThePlayerGameHintKey,
    val description: String,
    val trophyCountry: String? = null,
    val trophySeason: String? = null,
    val trophyLeague: String? = null,
    val transferYear: Int? = null,
    val transferFromLogo: String? = null,
    val transferFromTeam: String? = null,
    val transferToLogo: String? = null,
    val transferToTeam: String? = null,
    val transferDate: String? = null,
)


enum class GuessThePlayerGameHintKey(val value: String) {
    TRANSFER("transfer"),
    TROPHY("trophy");
}