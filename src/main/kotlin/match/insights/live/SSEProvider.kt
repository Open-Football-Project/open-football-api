package match.insights.live

import match.insights.props.SSEProps
import match.insights.response.LiveMatchesResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class SSEProvider(private val sseProps: SSEProps) {

    fun newEmitter(fetchMatches: () -> List<LiveMatchesResponse>): SseEmitter {
        val emitter = SseEmitter(sseProps.matchTimeOutMs)
        val executor = Executors.newSingleThreadScheduledExecutor()

        executor.scheduleAtFixedRate({
            runCatching {
                emitter.send(fetchMatches())
            }.onFailure {
                emitter.complete()
            }

        }, sseProps.initialDelayInMinutes, sseProps.refreshIntervalMinutes, TimeUnit.MINUTES)

        emitter.onCompletion { executor.shutdown() }
        emitter.onTimeout { executor.shutdown() }
        emitter.onError { executor.shutdown() }
        return emitter
    }
}