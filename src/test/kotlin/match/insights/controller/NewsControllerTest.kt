package match.insights.controller

import match.insights.TestCorsPropsConfig
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import match.insights.service.RssNewsService

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Import(TestCorsPropsConfig::class)
@WebMvcTest(NewsController::class)
class NewsControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var rssNewsService: RssNewsService


    @Test
    fun shouldGetTheLatestNews() {
        every { rssNewsService.getNews(any()) } returns emptyList()

        val response = mvc.perform(
            get("/api/news/en")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { rssNewsService.getNews(any()) }
    }


}