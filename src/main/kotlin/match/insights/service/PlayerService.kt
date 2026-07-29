package match.insights.service

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import match.insights.apidata.PlayersData
import match.insights.clientData.PlayerTransfer
import match.insights.clientData.PlayerTrophy
import match.insights.errors.ApiFailedException
import match.insights.errors.ErrorMessage
import match.insights.internaldata.VideoContentManager
import match.insights.model.ContentKey
import match.insights.model.VideoContent
import match.insights.response.PlayerHistory
import match.insights.response.PlayerHistoryInfo
import match.insights.response.PlayerMainInfo
import match.insights.response.PlayerTransferInfo
import match.insights.response.PlayerTrophyInfo
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val playersData: PlayersData,
    private val videoContentManager: VideoContentManager
) {
    fun playerHistory(playerId: Int): PlayerHistory {

        val data = playersHistoryData(playerId)

        return PlayerHistory(
            playerInfo(data["transfers"] as List<PlayerTransfer>),
            mapTrophies(data["trophies"] as List<PlayerTrophy>),
            mapTransfers(data["transfers"] as List<PlayerTransfer>)
        )
    }

    fun fullPlayerInfo(playerId: Int): PlayerMainInfo =
        playersData.playerInfo(playerId).let { response ->
            return if (response == null) throw ApiFailedException(ErrorMessage.RESOURCE_NOT_FOUND)
            else {
                val stats = response.statistics.firstOrNull()

                PlayerMainInfo(
                    playerId = response.player.id ?: -1,
                    name = response.player.name?.ifBlank { "Unknown" } ?: "Unknown",
                    age = response.player.age ?: -1,
                    nationality = response.player.nationality ?: "Unknown",
                    position = stats?.games?.position ?: "Unknown",
                    height = response.player.height ?: "Unknown",
                    weight = response.player.weight ?: "Unknown",
                    teamId = stats?.team?.id ?: -1,
                    teamName = stats?.team?.name ?: "Unknown",
                    teamLogo = stats?.team?.logo,
                    photo = response.player.photo,
                    injured = response.player.injured,
                    videos = videoContentManager.getVideos(ContentKey.PLAYER, playerId)
                )
            }

        }

    fun playerInfo(transfers: List<PlayerTransfer>): PlayerHistoryInfo? =
        transfers.getOrNull(0)?.let { PlayerHistoryInfo(it.player.id, it.player.name, it.player.photo) }


    fun mapTransfers(transfers: List<PlayerTransfer>): List<PlayerTransferInfo> = transfers.flatMap { playerTransfer ->
        playerTransfer.transfers.map { transfer ->
            PlayerTransferInfo(
                date = transfer.date,
                fromTeamId = transfer.teams.teamFrom.id,
                fromTeamName = transfer.teams.teamFrom.name,
                fromTeamLogo = transfer.teams.teamFrom.logo,
                toTeamId = transfer.teams.teamTo.id,
                toTeamName = transfer.teams.teamTo.name,
                toTeamLogo = transfer.teams.teamTo.logo
            )
        }
    }

    fun mapTrophies(trophies: List<PlayerTrophy>): List<PlayerTrophyInfo> = trophies.map { playerTrophy ->
        PlayerTrophyInfo(
            league = playerTrophy.league,
            country = playerTrophy.country,
            season = playerTrophy.season,
            place = playerTrophy.place
        )

    }

    fun addVideoContent(playerId: Int, videos: Set<VideoContent>): Set<VideoContent> =
        videoContentManager.newContent(ContentKey.PLAYER, playerId, videos)

    private fun playersHistoryData(playerId: Int) = runBlocking {
        val jobs = mapOf(
            "trophies" to async { playersData.trophies(playerId) },
            "transfers" to async { playersData.transfers(playerId) })
        jobs.mapValues { (_, job) -> job.await() }
    }


}