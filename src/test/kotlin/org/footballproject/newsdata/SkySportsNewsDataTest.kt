package org.footballproject.newsdata

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SkySportsNewsDataTest {

    private fun loadXmlFromResource(): org.w3c.dom.Element {
        val stream = this::class.java.getResourceAsStream("/newsfeeds/sky.xml")
            ?: throw IllegalArgumentException("Resource not found: /newsfeed/sky.xml")
        val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(stream)
        return doc.documentElement
    }

    @Test
    fun `test Sky parser with sample XML`() {
        val parser = SkySportsNewsData()
        val xmlRoot = loadXmlFromResource()
        val newsData: NewsData = parser.parse(xmlRoot)

        assertThat(newsData).isNotNull
        assertThat(newsData.description).isNotEmpty
        assertThat(newsData.title).isNotEmpty

        newsData.items.forEach {
            assertThat(it.title).isNotEmpty
            assertThat(it.description).isNotEmpty
            assertThat(it.url).isNotEmpty
            assertThat(it.image).isNotEmpty
        }
    }
}
