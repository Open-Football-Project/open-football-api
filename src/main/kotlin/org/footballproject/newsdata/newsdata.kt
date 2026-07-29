package org.footballproject.newsdata


data class NewsItem(
    val title: String,
    val description: String,
    val url: String,
    val image: String,
    val text: String? = ""
)

data class NewsData(val title: String, val description: String, val items: List<NewsItem>)