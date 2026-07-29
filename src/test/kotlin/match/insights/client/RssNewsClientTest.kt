package match.insights.client

import match.insights.errors.ApiRssFeedException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RssNewsClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var underTest: RssNewsClient

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        underTest = RssNewsClient()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should fetch and parse RSS xml successfully`() {
        val rssXml = """
            <?xml version="1.0" encoding="UTF-8" ?>
            <rss version="2.0">
              <channel>
                <title>BBC Football</title>
                <item>
                  <title>Match News</title>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(rssXml)
                .addHeader("Content-Type", "application/rss+xml")
        )

        val url = mockWebServer.url("/rss").toString()

        val result = underTest.fetchXml(url)

        assertThat(result).isNotNull
        assertThat(result!!.nodeName).isEqualTo("rss")
    }

    @Test
    fun `should throw exception when response is not 200`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(403)
        )

        val url = mockWebServer.url("/rss").toString()

        assertThatThrownBy {
            underTest.fetchXml(url)
        }.isInstanceOf(ApiRssFeedException::class.java)
    }

    @Test
    fun `should throw exception when xml is invalid`() {
        val invalidXml = "<rss><channel><title>Broken"

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(invalidXml)
                .addHeader("Content-Type", "application/rss+xml")
        )

        val url = mockWebServer.url("/rss").toString()

        assertThatThrownBy {
            underTest.fetchXml(url)
        }.isInstanceOf(ApiRssFeedException::class.java)
    }
}
