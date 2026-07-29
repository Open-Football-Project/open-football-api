package match.insights

import match.insights.internaldata.VideoContentManager
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class MatchInsightsTests {

    @MockBean
    private lateinit var tarotVideoPredictions: VideoContentManager

    @Test
    fun contextLoads() {
    }

}
