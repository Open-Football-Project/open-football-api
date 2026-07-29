package org.footballproject

import org.footballproject.internaldata.VideoContentManager
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class OpenFootballProjectTests {

    @MockBean
    private lateinit var tarotVideoPredictions: VideoContentManager

    @Test
    fun contextLoads() {
    }

}
