package org.footballproject.live

import org.footballproject.clientData.MatchStatus as ClientStatus
import org.footballproject.apidata.LiveData
import org.footballproject.clientData.LiveFixtureResponse
import org.footballproject.model.MatchStatus
import org.footballproject.props.ChartsProps
import org.footballproject.service.LiveChartsBetsService
import org.footballproject.service.LiveChartsService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
        processLiveOddsMatches(trackedMatches, now)
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
        processLiveMatches(trackedMatches, now)
    }

    private fun shouldSuspend(now: Instant, activeUntil: Instant, nextIdleCheckAt: Instant): Boolean {
        if (!chartsProps.schedulingEnabled) return true
        return (now in activeUntil..<nextIdleCheckAt)
    }

    private fun processLiveMatches(trackedMatches: List<LiveFixtureResponse>, now: Instant) {
        trackedMatches.map { withEstimatedElapsed(it, now) }.forEach { fixture ->
            try {
                liveChartsService.captureLiveIndicators(fixture)
            } catch (e: Exception) {
                log.error("Failed to capture live chart data for fixture ${fixture.fixture.id}", e)
            }
        }
    }


    private fun processLiveOddsMatches(trackedMatches: List<LiveFixtureResponse>, now: Instant) {
        trackedMatches.map { withEstimatedElapsed(it, now) }.forEach { fixture ->
            try {
                liveChartsBetsService.captureLiveOdds(fixture)
            } catch (e: Exception) {
                log.error("Failed to capture live odds for fixture ${fixture.fixture.id}", e)
            }
        }
    }

    fun withEstimatedElapsed(fixture: LiveFixtureResponse, now: Instant): LiveFixtureResponse {
        val status = fixture.fixture.status
        val fixtureInstant = fixtureDateToInstant(fixtureDate = fixture.fixture.date)

        if (status?.elapsed != null || fixtureInstant == null) return fixture

        val estimatedElapsed = Duration.between(fixtureInstant, now).toMinutes().toInt().coerceAtLeast(0)
        val estimatedStatus = status?.copy(elapsed = estimatedElapsed)
            ?: ClientStatus(short = MatchStatus.LIVE.code, elapsed = estimatedElapsed)

        return fixture.copy(fixture = fixture.fixture.copy(status = estimatedStatus))
    }

    private fun fixtureDateToInstant(fixtureDate: String) = runCatching {
        ZonedDateTime.parse(fixtureDate, DateTimeFormatter.ISO_DATE_TIME).toInstant()
    }.getOrNull()

    private fun isInAValidLeague(leagueId: Int): Boolean = chartsProps.trackedLeagueIds.contains(leagueId)

}