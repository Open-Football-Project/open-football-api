package org.footballproject.controller

import org.footballproject.response.RssNewsItem
import org.footballproject.service.RssNewsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news")
class NewsController(
    private val rssNewsService: RssNewsService
) {

    @GetMapping("/{lang}")
    fun getNews(@PathVariable lang: String): List<RssNewsItem> {
        val news = rssNewsService.getNews(lang)
        return news.shuffled()
    }
}