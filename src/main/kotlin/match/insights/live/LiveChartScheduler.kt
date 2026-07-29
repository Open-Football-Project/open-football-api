package match.insights.live

import match.insights.apidata.LiveData
import match.insights.clientData.LiveFixtureResponse
import match.insights.model.MatchStatus
import match.insights.props.ChartsProps
import match.insights.service.LiveChartsBetsService
import match.insights.service.LiveChartsService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class LiveChartScheduler(
    private val liveData: LiveData,
    private val liveChartsService: LiveChartsService,
    private val liveChartsBetsService: LiveChartsBetsService,
    private val chartsProps: ChartsProps,
    private val clock: Clock = Clock.systemUTC()
) {

    private val log = LoggerFactory.getLogger(LiveChartScheduler::class.java)

    private var chartsActiveUntil: Instant = Instant.MIN
    private var chartsNextIdleCheckAt: Instant = Instant.MIN

    private var oddsActiveUntil: Instant = Instant.MIN
    private var oddsNextIdleCheckAt: Instant = Instant.MIN

    @Scheduled(fixedRateString = "\${charts.oddsPollingMilli}")
    fun captureLiveOdds() {
        val now = Instant.now(clock)
        if (shouldSuspend(now, oddsActiveUntil, oddsNextIdleCheckAt)) return

        val trackedMatches = liveData.allLiveMatches()
            .filter { isInAValidLeague(it.league.id) }

        if (trackedMatches.isEmpty()) {
            oddsNextIdleCheckAt = now.plusMillis(chartsProps.idleBackoffMilli)
            return
        }

        oddsActiveUntil = now.plusMillis(chartsProps.activeWindowMilli)
        processLiveOddsMatches(trackedMatches)
    }

    @Scheduled(fixedRateString = "\${charts.pollingMilli}")
    fun captureLiveCharts() {
        val now = Instant.now(clock)
        if (shouldSuspend(now, chartsActiveUntil, chartsNextIdleCheckAt)) return

        val trackedMatches = liveData.allLiveMatches()
            .filter { isInAValidLeague(it.league.id) }

        if (trackedMatches.isEmpty()) {
            chartsNextIdleCheckAt = now.plusMillis(chartsProps.idleBackoffMilli)
            return
        }

        chartsActiveUntil = now.plusMillis(chartsProps.activeWindowMilli)
        processLiveMatches(trackedMatches)
    }

    private fun shouldSuspend(now: Instant, activeUntil: Instant, nextIdleCheckAt: Instant): Boolean {
        if (!chartsProps.schedulingEnabled) return true
        return (now in activeUntil..<nextIdleCheckAt)
    }

    private fun processLiveMatches(trackedMatches: List<LiveFixtureResponse>) {
        trackedMatches.forEach { fixture ->
            try {
                liveChartsService.captureLiveIndicators(fixture)
            } catch (e: Exception) {
                log.error("Failed to capture live chart data for fixture ${fixture.fixture.id}", e)
            }
        }
    }


    private fun processLiveOddsMatches(trackedMatches: List<LiveFixtureResponse>) {
        trackedMatches.forEach { fixture ->
            try {
                liveChartsBetsService.captureLiveOdds(fixture)
            } catch (e: Exception) {
                log.error("Failed to capture live odds for fixture ${fixture.fixture.id}", e)
            }
        }
    }

    private fun isInAValidLeague(leagueId: Int): Boolean = chartsProps.trackedLeagueIds.contains(leagueId)

}