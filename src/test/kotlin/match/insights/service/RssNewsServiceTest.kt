package match.insights.service


import io.mockk.every
import io.mockk.mockk

import io.mockk.verify
import match.insights.client.RssNewsClient
import match.insights.newsdata.BBCNewsData
import match.insights.newsdata.GuardianNewsData
import match.insights.newsdata.MarcaNewsData
import match.insights.newsdata.MundoDeportivoNewsData
import match.insights.newsdata.NewsData
import match.insights.newsdata.NewsItem
import match.insights.newsdata.OleNewsData
import match.insights.newsdata.SkySportsNewsData
import match.insights.props.NewsSource
import match.insights.props.RssFeed
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test


class RssNewsServiceTest {

    private val bbcNewsData: BBCNewsData = mockk()
    private val guardianNewsData: GuardianNewsData = mockk()
    private val skySportsNewsData: SkySportsNewsData = mockk()
    private val marcaNewsData: MarcaNewsData = mockk()
    private val mundoDeportivoNewsData: MundoDeportivoNewsData = mockk()
    private val oleNewsData: OleNewsData = mockk()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: RssNewsClient
    private lateinit var newsSource: NewsSource
    private lateinit var rssService: RssNewsService

    private fun getNewsSource(): NewsSource {
        return listOf(
            RssFeed(
                name = "BBC Football",
                url = "/rss",
                language = "en",
                parserKey = "bBCNewsData"
            ),
            RssFeed(
                name = "Guardian Football",
                url = "/rss",
                language = "en",
                parserKey = "guardianNewsData"
            ),
            RssFeed(
                name = "Sky Sports Football",
                url = "/rss",
                language = "en",
                parserKey = "skySportsNewsData"
            ),
            RssFeed(
                name = "Marca Futbol",
                url = "/rss",
                language = "es",
                parserKey = "marcaNewsData"
            ),
            RssFeed(
                name = "Mundo Deportivo Futbol",
                url = "/rss",
                language = "es",
                parserKey = "mundoDeportivoNewsData"
            ),
            RssFeed(
                name = "Ole Ultimas",
                url = "/rss",
                language = "es",
                parserKey = "oleNewsData"
            )
        ).map { feed ->
            RssFeed(
                name = feed.name,
                url = mockWebServer.url("/rss").toString(),
                language = feed.language,
                parserKey = feed.parserKey
            )

        }.let { NewsSource(it) }

    }

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        client = RssNewsClient()
        newsSource = getNewsSource()
        rssService = RssNewsService(
            client,
            newsSource,
            mapOf(
                "bBCNewsData" to bbcNewsData,
                "guardianNewsData" to guardianNewsData,
                "skySportsNewsData" to skySportsNewsData,
                "marcaNewsData" to marcaNewsData,
                "mundoDeportivoNewsData" to mundoDeportivoNewsData,
                "oleNewsData" to oleNewsData
            )
        )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

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

    @Test
    fun `should get news in spanish`() {
        repeat(3) {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(rssXml)
                    .addHeader("Content-Type", "application/rss+xml")
            )
        }

        every { marcaNewsData.parse(any()) } returns NewsData(
            "marca data", "marca", listOf(NewsItem("A", "B", "C", "D"))
        )
        every { oleNewsData.parse(any()) } returns NewsData(
            "ole data", "ole", listOf(NewsItem("A", "B", "C", "D"))
        )
        every { mundoDeportivoNewsData.parse(any()) } returns NewsData(
            "mund dep data", "mundo dep", listOf(NewsItem("A", "B", "C", "D"))
        )

        val result = rssService.getNews("es")

        assertThat(result).hasSize(3)

        verify { marcaNewsData.parse(any()) }
        verify { oleNewsData.parse(any()) }
        verify { mundoDeportivoNewsData.parse(any()) }

        repeat(3) {
            val request = mockWebServer.takeRequest()
            assertThat(request.path).isEqualTo("/rss")
        }
    }


    @Test
    fun `should get news in English`() {
        repeat(3) {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(rssXml)
                    .addHeader("Content-Type", "application/rss+xml")
            )
        }

        every { skySportsNewsData.parse(any()) } returns NewsData(
            "sky data", "sky", listOf(NewsItem("A", "B", "C", "D"))
        )
        every { guardianNewsData.parse(any()) } returns NewsData(
            "guardian data", "guardian", listOf(NewsItem("A", "B", "C", "D"))
        )
        every { bbcNewsData.parse(any()) } returns NewsData(
            "bbc data", "bbc ", listOf(NewsItem("A", "B", "C", "D"))
        )

        val result = rssService.getNews("en")

        assertThat(result).hasSize(3)

        verify { skySportsNewsData.parse(any()) }
        verify { guardianNewsData.parse(any()) }
        verify { bbcNewsData.parse(any()) }

        repeat(3) {
            val request = mockWebServer.takeRequest()
            assertThat(request.path).isEqualTo("/rss")
        }
    }


}
