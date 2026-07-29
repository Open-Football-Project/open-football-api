package org.footballproject.response

import java.io.Serializable

data class RssNewsItem(
    val title: String,
    val url: String,
    val description: String,
    val image: String,
    val source: String
) : Serializable