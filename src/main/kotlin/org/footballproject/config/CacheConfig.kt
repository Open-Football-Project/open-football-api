package org.footballproject.config

import org.footballproject.props.CacheTTL
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class CacheConfig(private val cacheTTL: CacheTTL) {

    @Bean
    fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(redisConnectionFactory)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = JdkSerializationRedisSerializer()
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun cacheManager(redisConnectionFactory: LettuceConnectionFactory): CacheManager {
        val serializer = JdkSerializationRedisSerializer()

        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            ).entryTtl(Duration.ofSeconds(900))


        val cacheConfigs: Map<String, RedisCacheConfiguration> = cacheTTL.ttlSeconds.mapValues { (_, ttlSeconds) ->
            defaultConfig.entryTtl(Duration.ofSeconds(ttlSeconds))
        }

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigs)
            .build()
    }
}
