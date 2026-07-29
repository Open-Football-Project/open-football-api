package org.footballproject.newsdata

import org.springframework.stereotype.Component
import org.w3c.dom.Element

@Component
class SkySportsNewsData : NewsDataParser {
    override fun parse(element: Element): NewsData {
        val items = mutableListOf<NewsItem>()
        val nodes = element.getElementsByTagName("item")

        for (i in 0 until nodes.length) {
            val itemElement = nodes.item(i) as Element
            val title = itemElement.getElementsByTagName("title").item(0).textContent
            val descriptionNode = itemElement.getElementsByTagName("description").item(0)
            val description = descriptionNode?.textContent ?: ""
            val link = itemElement.getElementsByTagName("link").item(0).textContent


            val enclosureNodes = itemElement.getElementsByTagName("enclosure")
            val imageUrl = if (enclosureNodes.length > 0) {
                // pick the first enclosure with type image/jpg
                (0 until enclosureNodes.length)
                    .map { enclosureNodes.item(it) as Element }
                    .firstOrNull { it.getAttribute("type") == "image/jpg" }
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
            title = "Sky Sports Feed",
            description = "Latest Sports News from Sky Sports",
            items = items
                .filterNot { it.image.isEmpty() || it.url.isEmpty() || it.title.isEmpty() || it.description.isEmpty() }
        )
    }
}
