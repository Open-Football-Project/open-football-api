package org.footballproject.service

import org.footballproject.client.RssNewsClient
import org.footballproject.errors.ApiRssFeedException
import org.footballproject.errors.ErrorMessage
import org.footballproject.newsdata.NewsDataParser
import org.footballproject.newsdata.NewsItem
import org.footballproject.props.NewsSource
import org.footballproject.props.RssFeed
import org.footballproject.response.RssNewsItem
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service


@Service
class RssNewsService(
    private val rssNewsClient: RssNewsClient, private val newsSource: NewsSource,
    private val parsers: Map<String, NewsDataParser>
) {

    @Cacheable(value = ["getNews"], key = "#lang")
    fun getNews(lang: String): List<RssNewsItem> = runCatching {
        return newsSource.feeds.filter { feed -> feed.language == lang }.flatMap {
            newsFromSource(it)
                .take(15)
                .map { item ->
                    RssNewsItem(
                        title = tagsCleanUp(item.title),
                        url = tagsCleanUp(item.url),
                        description = tagsCleanUp(item.description),
                        image = tagsCleanUp(item.image),
                        source = it.url
                    )
                }
        }
    }.getOrElse {
        throw ApiRssFeedException(ErrorMessage.RSS_CLIENT_FAILED)
    }


    private fun newsFromSource(source: RssFeed): List<NewsItem> {
        val parser = parsers[source.parserKey]
        val element = rssNewsClient.fetchXml(source.url)
        return if (parser == null || element == null) return emptyList() else parser.parse(element).items
    }


    private fun tagsCleanUp(str: String) = str.replace(Regex("<.*?>"), "").trim()

}
