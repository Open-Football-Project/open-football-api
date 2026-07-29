package match.insights.live


import match.insights.props.SSEProps
import match.insights.response.LiveMatchesResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SEEProviderTest {

    private val sseProps = SSEProps(0, 1, 7200000)
    private val sseProvider = SSEProvider(sseProps)

    private fun liveMatches(): List<LiveMatchesResponse> = listOf()

    @Test
    fun shouldStartLiveMatchesStream() {
        val result = sseProvider.newEmitter { liveMatches() }

        assertThat(result).isNotNull
        assertThat(result.timeout).isEqualTo((2 * 60 * 60 * 1000).toLong())
    }

}