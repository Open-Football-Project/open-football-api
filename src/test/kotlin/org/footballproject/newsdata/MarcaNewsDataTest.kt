package org.footballproject.newsdata

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class MarcaNewsDataTest {

    private fun loadXmlFromResource(): Element {
        val stream = this::class.java.getResourceAsStream("/newsfeeds/marca.xml")
            ?: throw IllegalArgumentException("Resource not found: /newsfeed/marca.xml")
        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(stream)
        return doc.documentElement
    }

    @Test
    fun `test Marca parser with sample XML`() {
        val parser = MarcaNewsData()
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
