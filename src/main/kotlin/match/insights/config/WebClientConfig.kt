package match.insights.config

import match.insights.props.ApiDataProps
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(builder: RestClient.Builder, props: ApiDataProps): RestClient {
        return builder.baseUrl(props.url)
            .defaultHeader("x-apisports-key", props.key).build()
    }
}