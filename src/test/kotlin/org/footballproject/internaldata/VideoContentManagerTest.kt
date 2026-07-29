package org.footballproject.internaldata

import org.footballproject.errors.ApiResourceNotFoundException
import org.footballproject.model.ContentKey
import org.footballproject.model.VideoContent
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.collections.setOf

class VideoContentManagerTest {

    @Test
    fun `throws exception if the file does not exists`() {
        val service = VideoContentManager("/non/existent/predictions.json", 30)
        assertThrows<ApiResourceNotFoundException> { service.loadContent() }
    }

    @Test
    fun `returns video urls for a valid match`(@TempDir tempDir: Path) {
        val videosSet = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "Match Highlights",
                "Match Highlights",
                "22/06/1990"
            )
        )
        val file = File(tempDir.toFile(), "predictions.json")
        file.writeText("""{"${ContentKey.MATCH.value}_1089175": [{"url": "https://www.youtube.com/watch?v=abc123", "esLabel": "Match Highlights", "enLabel": "Match Highlights", "uploadDate": "22/06/1990"}]}""")

        val service = VideoContentManager(file.absolutePath, 999999)

        val videos = service.getVideos(ContentKey.MATCH, 1089175)

        Assertions.assertThat(videos).isEqualTo(videosSet)
    }

    @Test
    fun `returns and empty list for invalid match id`(@TempDir tempDir: Path) {

        val file = File(tempDir.toFile(), "predictions.json")
        file.writeText("""{"${ContentKey.MATCH.value}_1089175": [{"url": "https://www.youtube.com/watch?v=abc123", "esLabel": "Match Highlights", "enLabel": "Match Highlights", "uploadDate": "22/06/1990"}]}""")

        val service = VideoContentManager(file.absolutePath, 30)
        val videos = service.getVideos(ContentKey.MATCH, 9999)

        Assertions.assertThat(videos).isEmpty()
    }


    @Test
    fun `add videos to the premier league`(@TempDir tempDir: Path) {
        val videosSet = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        val file = File(tempDir.toFile(), "predictions.json")
        file.writeText("{}")

        val service = VideoContentManager(file.absolutePath, 30)

        val result = service.newContent(
            ContentKey.LEAGUE,
            39,
            videosSet
        )

        Assertions.assertThat(service.loadContent()["${ContentKey.LEAGUE.value}_39"]).isNotEmpty
    }


}