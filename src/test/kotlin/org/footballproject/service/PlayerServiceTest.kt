package org.footballproject.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.PlayersData
import org.footballproject.data.client.ClientPlayerInfo
import org.footballproject.data.client.ClientPlayerTrophies
import org.footballproject.data.client.ClientTransfersData
import org.footballproject.internaldata.VideoContentManager
import org.footballproject.model.ContentKey
import org.footballproject.model.VideoContent
import org.footballproject.errors.ApiFailedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class PlayerServiceTest {

    private val playersData: PlayersData = mockk()
    private val videoContentManager: VideoContentManager = mockk()
    private val service = PlayerService(playersData, videoContentManager)

    @Test
    fun `I get the player full history`() {
        every { playersData.trophies(any()) } returns ClientPlayerTrophies.trophies
        every { playersData.transfers(any()) } returns ClientTransfersData.transfers


        val result = service.playerHistory(1)

        assertThat(result.player).isNotNull
        assertThat(result.trophies).isNotEmpty
        assertThat(result.transfers).isNotEmpty

        verify(exactly = 1) { playersData.trophies(any()) }
        verify(exactly = 1) { playersData.transfers(any()) }
    }

    @Test
    fun `I get the player info from a list of transfers`() {
        val player = service.playerInfo(ClientTransfersData.transfers)

        assertThat(player?.name).isNotNull
        assertThat(player?.name).isNotEmpty
        assertThat(player?.photo).isNotEmpty
    }

    @Test
    fun `I get no player wihthout transfers`() {
        val player = service.playerInfo(emptyList())
        assertThat(player).isNull()
    }

    @Test
    fun `I get a list of transfers info`() {
        val transfers = service.mapTransfers(ClientTransfersData.transfers)

        assertThat(transfers).isNotEmpty

        assertThat(transfers[0].date).isNotEmpty
        assertThat(transfers[0].fromTeamId).isNotNull
        assertThat(transfers[0].toTeamId).isNotNull
        assertThat(transfers[0].fromTeamName).isNotEmpty
        assertThat(transfers[0].toTeamName).isNotEmpty
    }

    @Test
    fun `I get a list of trophies info`() {
        val trophies = service.mapTrophies(ClientPlayerTrophies.trophies)

        assertThat(trophies).isNotEmpty

        assertThat(trophies[0].place).isNotEmpty
        assertThat(trophies[0].country).isNotNull
        assertThat(trophies[0].league).isNotNull
        assertThat(trophies[0].season).isNotNull
    }

    @Test
    fun `I get the player main info by id`() {
        every { playersData.playerInfo(any()) } returns ClientPlayerInfo.playerInfoResponse
        every { videoContentManager.getVideos(any(), any()) } returns emptySet()

        val result = service.fullPlayerInfo(1234)

        assertThat(result.playerId).isEqualTo(ClientPlayerInfo.playerInfoResponse.player.id)
        assertThat(result.teamId).isEqualTo(ClientPlayerInfo.playerInfoResponse.statistics.firstOrNull()?.team?.id)
        assertThat(result.position).isEqualTo(ClientPlayerInfo.playerInfoResponse.statistics.firstOrNull()?.games?.position)
        assertThat(result.nationality).isEqualTo(ClientPlayerInfo.playerInfoResponse.player.nationality)
        assertThat(result.videos).isEmpty()

        verify { playersData.playerInfo(any()) }
        verify { videoContentManager.getVideos(any(), any()) }
    }

    @Test
    fun `I get video content included in player info`() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { playersData.playerInfo(any()) } returns ClientPlayerInfo.playerInfoResponse
        every { videoContentManager.getVideos(ContentKey.PLAYER, 1234) } returns videos

        val result = service.fullPlayerInfo(1234)

        assertThat(result.videos).isEqualTo(videos)

        verify { videoContentManager.getVideos(ContentKey.PLAYER, 1234) }
    }

    @Test
    fun `I get ApiFailedException when player data is not found`() {
        every { playersData.playerInfo(any()) } returns null

        assertThrows<ApiFailedException> {
            service.fullPlayerInfo(19012)
        }
    }

    @Test
    fun `I can add video content to a player`() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { videoContentManager.newContent(ContentKey.PLAYER, 1234, videos) } returns videos

        val result = service.addVideoContent(1234, videos)

        assertThat(result).isEqualTo(videos)
        verify { videoContentManager.newContent(ContentKey.PLAYER, 1234, videos) }
    }
}
