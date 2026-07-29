package match.insights.newsdata

import org.springframework.stereotype.Component
import org.w3c.dom.Element

@Component
class BBCNewsData : NewsDataParser {
    override fun parse(element: Element): NewsData {
        val items = mutableListOf<NewsItem>()
        val nodes = element.getElementsByTagName("item")

        for (i in 0 until nodes.length) {
            val itemElement = nodes.item(i) as Element
            val title = itemElement.getElementsByTagName("title").item(0).textContent
            val description = itemElement.getElementsByTagName("description").item(0).textContent
            val link = itemElement.getElementsByTagName("link").item(0).textContent

            val mediaNodes = itemElement.getElementsByTagName("media:thumbnail")
            val imageUrl = if (mediaNodes.length > 0) {
                (mediaNodes.item(0) as Element).getAttribute("url")
            } else ""

            items.add(NewsItem(title, description, link, image = imageUrl, text = description))
        }

        return NewsData(
            title = "BBC Football Feed", description = "BBC Football RSS", items = items
                .filterNot { it.image.isEmpty() || it.url.isEmpty() || it.title.isEmpty() || it.description.isEmpty() }
        )
    }
}
