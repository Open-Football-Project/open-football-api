package org.footballproject.props

import org.springframework.boot.context.properties.ConfigurationProperties

data class RssFeed(
    val name: String,
    val url: String,
    val language: String,
    val parserKey: String
)


@ConfigurationProperties(prefix = "rss")
data class NewsSource(
    val feeds: List<RssFeed>
)
