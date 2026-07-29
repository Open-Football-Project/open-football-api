package org.footballproject.newsdata

import org.springframework.stereotype.Component
import org.w3c.dom.Element

@Component
class MundoDeportivoNewsData : NewsDataParser {
    override fun parse(element: Element): NewsData {
        val items = mutableListOf<NewsItem>()
        val nodes = element.getElementsByTagName("item")

        for (i in 0 until nodes.length) {
            val itemElement = nodes.item(i) as Element
            val title = itemElement.getElementsByTagName("title").item(0)?.textContent?.trim() ?: ""
            val description = itemElement.getElementsByTagName("description").item(0)?.textContent?.trim() ?: ""
            val link = itemElement.getElementsByTagName("link").item(0)?.textContent?.trim() ?: ""

            val mediaNodes = itemElement.getElementsByTagName("media:content")
            val enclosureNodes = itemElement.getElementsByTagName("enclosure")

            val imageUrl = when {
                mediaNodes.length > 0 -> {
                    (0 until mediaNodes.length)
                        .map { mediaNodes.item(it) as Element }
                        .firstOrNull { it.getAttribute("type") == "image/jpeg" }
                        ?.getAttribute("url") ?: ""
                }

                enclosureNodes.length > 0 -> {
                    (0 until enclosureNodes.length)
                        .map { enclosureNodes.item(it) as Element }
                        .firstOrNull { it.getAttribute("type").startsWith("image") }
                        ?.getAttribute("url") ?: ""
                }

                else -> ""
            }

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
            title = "Mundo Deportivo Feed",
            description = "Últimas noticias de fútbol de Mundo Deportivo",
            items = filteredItems
        )
    }
}
