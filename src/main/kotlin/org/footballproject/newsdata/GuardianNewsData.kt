package org.footballproject.newsdata

import org.springframework.stereotype.Component
import org.w3c.dom.Element

@Component
class GuardianNewsData : NewsDataParser {
    override fun parse(element: Element): NewsData {
        val items = mutableListOf<NewsItem>()
        val nodes = element.getElementsByTagName("item")

        for (i in 0 until nodes.length) {
            val itemElement = nodes.item(i) as Element
            val title = itemElement.getElementsByTagName("title").item(0).textContent
            val description = itemElement.getElementsByTagName("description").item(0).textContent
            val link = itemElement.getElementsByTagName("link").item(0).textContent

            val mediaNodes = itemElement.getElementsByTagName("media:content")
            val imageUrl = if (mediaNodes.length > 0) {
                (0 until mediaNodes.length)
                    .map { mediaNodes.item(it) as Element }
                    .maxByOrNull { it.getAttribute("width").toIntOrNull() ?: 0 }
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

        return NewsData(
            title = "The Guardian Football Feed",
            description = "Latest Football news from The Guardian",
            items = items
                .filterNot { it.image.isEmpty() || it.url.isEmpty() || it.title.isEmpty() || it.description.isEmpty() }
        )
    }
}
