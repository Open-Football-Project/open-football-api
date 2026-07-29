package org.footballproject.newsdata

import org.springframework.stereotype.Component
import org.w3c.dom.Element

@Component
class MarcaNewsData : NewsDataParser {
    override fun parse(element: Element): NewsData {
        val items = mutableListOf<NewsItem>()
        val nodes = element.getElementsByTagName("item")

        for (i in 0 until nodes.length) {
            val itemElement = nodes.item(i) as Element
            val title = itemElement.getElementsByTagName("title").item(0)?.textContent ?: ""
            val description = itemElement.getElementsByTagName("description").item(0)?.textContent ?: ""
            val link = itemElement.getElementsByTagName("link").item(0)?.textContent ?: ""

            val mediaNodes = itemElement.getElementsByTagName("media:content")
            val imageUrl = if (mediaNodes.length > 0) {
                (0 until mediaNodes.length)
                    .map { mediaNodes.item(it) as Element }
                    .firstOrNull { it.getAttribute("type") == "image/jpeg" }
                    ?.getAttribute("url") ?: ""
            } else ""

            items.add(
                NewsItem(
                    title = title,
                    description = description,
                    url = link,
                    image = imageUrl,
                    text = description
                )
            )
        }


        val filteredItems = items.filterNot {
            it.title.isEmpty() || it.description.isEmpty() || it.url.isEmpty() || it.image.isEmpty()
        }

        return NewsData(
            title = "Marca Fútbol Feed",
            description = "Últimas noticias de fútbol de Marca",
            items = filteredItems
        )
    }
}
