package match.insights.apidata

import match.insights.client.ApiSportsClient
import match.insights.clientData.PlayerInfoResponse

import match.insights.clientData.PlayerTransfer
import match.insights.clientData.PlayerTrophy
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Year
import java.util.Date

@Component
class PlayersData(
    private val apiSportsClient: ApiSportsClient,
) {

    fun transfers(playerId: Int): List<PlayerTransfer> =
        apiSportsClient.fetchTransfers("/transfers?player=$playerId")

    fun trophies(playerId: Int): List<PlayerTrophy> =
        apiSportsClient.fetchPlayerTrophies("/trophies?player=$playerId")


    fun playerInfo(playerId: Int): PlayerInfoResponse? {
        val currentYearInfo = playerYearInfo(playerId, Year.now().value)

        return if (currentYearInfo.isNotEmpty())
            currentYearInfo[0]
        else
            playerYearInfo(playerId, Year.now().value - 1).getOrNull(0)
    }

    private fun playerYearInfo(playerId: Int, season: Int): List<PlayerInfoResponse> =
        apiSportsClient.fetchPlayerInfo("/players?id=${playerId}&season=${season}")
}