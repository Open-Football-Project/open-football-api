package org.footballproject.service

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.footballproject.apidata.TeamData
import org.footballproject.apidata.PlayersData
import org.footballproject.clientData.PlayerResponse
import org.footballproject.clientData.PlayerTransfer
import org.footballproject.clientData.PlayerTrophy
import org.footballproject.response.game.GuessThePlayerGameData
import org.footballproject.response.game.GuessThePlayerGameHint
import org.footballproject.response.game.GuessThePlayerGameHintKey

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter

@Service
class PlayerGameService(
    private val teamData: TeamData,
    private val playersData: PlayersData
) {

    fun generatePlayerGame(teamId: Int): GuessThePlayerGameData =
        getPlayers(teamId).let { playersData ->
            if (playersData.isEmpty()) GuessThePlayerGameData()
            else getRandomPlayerGame(getRandomPlayer(playersData), playersData)
        }


    fun getRandomPlayerGame(
        randomPlayer: PlayerResponse?,
        players: Map<Int, List<PlayerResponse>>
    ): GuessThePlayerGameData {
        val invalidOptions = playerInvalidOptions(players, randomPlayer?.player?.name ?: "")

        return if (randomPlayer == null || invalidOptions.isEmpty()) {
            GuessThePlayerGameData()
        } else {
            GuessThePlayerGameData(
                isAvailable = true,
                playerId = randomPlayer.player.id,
                playerName = randomPlayer.player.name,
                playerNationality = randomPlayer.player.nationality ?: "Unknown",
                playerPhoto = randomPlayer.player.photo,
                playerPosition = randomPlayer.statistics.firstOrNull()?.games?.position ?: "Unknown",
                options = (setOf(randomPlayer.player.name) + invalidOptions.take(4).toSet()).shuffled().toSet(),
                hints = generatePlayerHints(
                    playersHintsData(randomPlayer.player.id)
                )
            )
        }
    }


    fun getRandomPlayer(responseMap: Map<Int, List<PlayerResponse>>): PlayerResponse? {
        val players = if (responseMap.isEmpty()) emptyList() else responseMap.values.flatten()

        if (players.isEmpty()) return null

        val interestingPlayer = players.filter {
            isAnInterestingPlayer(it)
        }

        return if (interestingPlayer.isNotEmpty()) {
            interestingPlayer.random()
        } else {
            players.maxByOrNull { p ->
                p.statistics.sumOf { it.goals.total ?: 0 }
            } ?: players.random()
        }
    }


    fun generatePlayerHints(
        hintsData: Map<String, Any>
    ): List<GuessThePlayerGameHint> {
        val trophies = hintsData["trophies"] as List<PlayerTrophy> ?: emptyList()
        val transfers = hintsData["transfers"] as List<PlayerTransfer> ?: emptyList()

        val trophiesHints = if (trophies.isNotEmpty()) trophies.filter { it.place.equals("Winner", ignoreCase = true) }
            .map { trophy ->
                GuessThePlayerGameHint(
                    GuessThePlayerGameHintKey.TROPHY,
                    description = "${trophy.league} ${if (trophy.season != null) "(${trophy.season})" else ""}".trim(),
                    trophyCountry = trophy.country,
                    trophy.season,
                    trophy.league

                )
            } else emptyList()

        val transferHints = if (transfers.isNotEmpty()) transfers.flatMap { it.transfers }
            .map { transfer ->
                GuessThePlayerGameHint(
                    description = "From ${transfer.teams.teamFrom.name} to ${transfer.teams.teamTo.name}",
                    hintKey = GuessThePlayerGameHintKey.TRANSFER,
                    transferDate = transfer.date,
                    transferYear = transferYear(transfer.date),
                    transferToTeam = transfer.teams.teamTo.name,
                    transferToLogo = transfer.teams.teamTo.logo,
                    transferFromTeam = transfer.teams.teamFrom.name,
                    transferFromLogo = transfer.teams.teamFrom.logo,
                )

            } else emptyList()

        val hints = (trophiesHints + transferHints)

        return if (hints.isEmpty()) emptyList() else hints.shuffled().take(5)
    }

    fun playerInvalidOptions(
        players: Map<Int, List<PlayerResponse>>,
        selectedPlayerName: String
    ): Set<String> =
        players.values.flatten().map { it.player.name }.filter { it != selectedPlayerName }.toSet()


    fun isAnInterestingPlayer(player: PlayerResponse): Boolean {
        return player.statistics.any { stat ->
            val games = stat.games
            val goals = stat.goals
            val cards = stat.cards
            val penalty = stat.penalty

            ((games.appearences ?: 0) >= 50) ||
                    ((games.minutes ?: 0) >= 1500) ||
                    ((goals.total ?: 0) >= 5) ||
                    ((goals.assists ?: 0) >= 5) ||
                    ((cards.yellow ?: 0) >= 6) ||
                    ((cards.red ?: 0) >= 2) ||
                    ((penalty.scored ?: 0) >= 3) ||
                    ((penalty.saved ?: 0) >= 3)
        }
    }


    fun getPlayers(teamId: Int): Map<Int, List<PlayerResponse>> =
        playersData(teamId).let {
            if (it["notSafeData"] != null && it["notSafeData"]?.isNotEmpty() == true
                && it["notSafeData"]?.any { (_, players) -> players.size >= 2 } == true
            ) return it["notSafeData"] ?: emptyMap()

            if (it["safeData"] != null && it["safeData"]?.isNotEmpty() == true
                && it["safeData"]?.any { (_, players) -> players.size >= 2 } == true
            ) return it["safeData"] ?: emptyMap()
            return emptyMap()
        }


    private fun playersData(teamId: Int) = runBlocking {
        val jobs = mapOf(
            "safeData" to async { teamData.teamSquad(teamId = teamId, season = safeRandomYear()) },
            "notSafeData" to async { teamData.teamSquad(teamId = teamId, season = randomYear()) }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }

    private fun playersHintsData(playerId: Int) = runBlocking {
        val jobs = mapOf(
            "trophies" to async { playersData.trophies(playerId) },
            "transfers" to async { playersData.transfers(playerId) }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }


    private fun randomYear(): Int =
        (1990..Year.now().value).random()

    private fun safeRandomYear(): Int =
        (2015..Year.now().value).random()

    private fun transferYear(transferDate: String?): Int? =
        transferDate?.let {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_DATE).year
            } catch (e: Exception) {
                null
            }
        }


}