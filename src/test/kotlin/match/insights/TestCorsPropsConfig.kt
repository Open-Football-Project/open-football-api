package match.insights

import match.insights.props.CorsProps
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestCorsPropsConfig {
    @Bean
    fun corsProps() = CorsProps(listOf("http://localhost"))
}
