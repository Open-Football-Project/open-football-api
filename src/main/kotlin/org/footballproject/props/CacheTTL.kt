package org.footballproject.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cache")
data class CacheTTL(
    val ttlSeconds: Map<String, Long>
)