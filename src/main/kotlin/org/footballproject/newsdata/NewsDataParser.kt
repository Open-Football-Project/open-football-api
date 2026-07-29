package org.footballproject.newsdata

import org.w3c.dom.Element

interface NewsDataParser {
    fun parse(element: Element): NewsData
}