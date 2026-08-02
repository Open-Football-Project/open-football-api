package org.footballproject.live

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.LiveData
import org.footballproject.clientData.Fixture
import org.footballproject.clientData.Goal
import org.footballproject.clientData.League
import org.footballproject.clientData.LiveFixtureResponse
import org.footballproject.clientData.MatchStatus
import org.footballproject.clientData.Score
import org.footballproject.clientData.Teams
import org.footballproject.props.ChartsProps
import org.footballproject.service.LiveChartsBetsService
import org.footballproject.service.LiveChartsService
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class LiveChartsSchedulerTest {

    private val liveData: LiveData = mockk()
    private val liveChartsService: LiveChartsService = mockk(relaxed = true)
    private val liveChartsBetsService: LiveChartsBetsService = mockk(relaxed = true)
    private val trackedLeagueId = 39
    private val chartsProps = ChartsProps(
        ttlSeconds = 14400,
        pollingMilli = 180000,
        trackedLeagueIds = listOf(trackedLeagueId),
        schedulingEnabled = true,
        oddsPollingMilli = 300000
    )

    private val underTest = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps)

    private fun liveFixture(fixtureId: Int, minute: Int, leagueId: Int = trackedLeagueId) = LiveFixtureResponse(
        fixture = Fixture(id = fixtureId, status = MatchStatus(elapsed = minute)),
        league = League(id = leagueId, season = 2026),
        teams = Teams(),
        goals = Goal(),
        score = Score(),
        events = emptyList()
    )

    private fun oddsEligibleFixture(
        fixtureId: Int,
        minute: Int,
        leagueId: Int = trackedLeagueId,
        statusShort: String = org.footballproject.model.MatchStatus.FIRST_HALF.code
    ) = LiveFixtureResponse(
        fixture = Fixture(id = fixtureId, status = MatchStatus(short = statusShort, elapsed = minute)),
        league = League(id = leagueId, season = 2026),
        teams = Teams(),
        goals = Goal(),
        score = Score(),
        events = emptyList()
    )

    private fun fixtureWithElapsed(
        fixtureId: Int,
        elapsed: Int?,
        date: String = "2026-06-25T10:00:00+00:00",
        statusShort: String = org.footballproject.model.MatchStatus.FIRST_HALF.code,
        leagueId: Int = trackedLeagueId
    ) = LiveFixtureResponse(
        fixture = Fixture(id = fixtureId, date = date, status = MatchStatus(short = statusShort, elapsed = elapsed)),
        league = League(id = leagueId, season = 2026),
        teams = Teams(),
        goals = Goal(),
        score = Score(),
        events = emptyList()
    )

    private class MutableClock(private var current: Instant) : Clock() {
        override fun getZone(): ZoneId = ZoneOffset.UTC
        override fun withZone(zone: ZoneId): Clock = this
        override fun instant(): Instant = current
        fun advanceTo(instant: Instant) {
            current = instant
        }
    }

    @Test
    fun shouldCaptureLiveIndicatorsForLiveMatches() {
        val fixture = liveFixture(fixtureId = 233, minute = 23)

        every { liveData.allLiveMatches() } returns listOf(fixture)
        every { liveChartsService.captureLiveIndicators(any()) } just Runs

        underTest.captureLiveCharts()

        verify { liveData.allLiveMatches() }
        verify {
            liveChartsService.captureLiveIndicators(
                any()
            )
        }
    }

    @Test
    fun shouldNotStopProcessingOtherFixturesWhenOneFixtureFails() {
        val failingFixture = liveFixture(fixtureId = 111, minute = 10)
        val healthyFixture = liveFixture(fixtureId = 222, minute = 15)

        every { liveData.allLiveMatches() } returns listOf(failingFixture, healthyFixture)
        every { liveChartsService.captureLiveIndicators(failingFixture) } throws RuntimeException("API down")
        every { liveChartsService.captureLiveIndicators(healthyFixture) } just Runs

        underTest.captureLiveCharts()

        verify { liveChartsService.captureLiveIndicators(failingFixture) }
        verify { liveChartsService.captureLiveIndicators(healthyFixture) }
    }

    @Test
    fun shouldOnlyCaptureLiveIndicatorsForTrackedLeagues() {
        val trackedFixture = liveFixture(fixtureId = 233, minute = 23, leagueId = trackedLeagueId)
        val untrackedFixture = liveFixture(fixtureId = 999, minute = 23, leagueId = 9999)

        every { liveData.allLiveMatches() } returns listOf(trackedFixture, untrackedFixture)
        every { liveChartsService.captureLiveIndicators(any()) } just Runs

        underTest.captureLiveCharts()

        verify { liveChartsService.captureLiveIndicators(trackedFixture) }
        verify(exactly = 0) { liveChartsService.captureLiveIndicators(untrackedFixture) }
    }

    @Test
    fun shouldDoNothingWhenSchedulingIsDisabled() {
        val disabledProps = chartsProps.copy(schedulingEnabled = false)
        val underTestDisabled = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, disabledProps)
        val fixture = liveFixture(fixtureId = 233, minute = 23)

        every { liveData.allLiveMatches() } returns listOf(fixture)

        underTestDisabled.captureLiveCharts()

        verify(exactly = 0) { liveChartsService.captureLiveIndicators(any()) }
    }

    @Test
    fun shouldSkipTheTickWhenIdleAndTheBackoffWindowHasNotElapsedYet() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)

        every { liveData.allLiveMatches() } returns emptyList()

        scheduler.captureLiveCharts()
        clock.advanceTo(Instant.parse("2026-06-25T10:10:00Z"))
        scheduler.captureLiveCharts()

        verify(exactly = 1) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldCheckAgainOnceTheIdleBackoffWindowHasElapsed() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)

        every { liveData.allLiveMatches() } returns emptyList()

        scheduler.captureLiveCharts()
        clock.advanceTo(Instant.parse("2026-06-25T10:30:00Z"))
        scheduler.captureLiveCharts()

        verify(exactly = 2) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldKeepCallingLiveDataThroughoutTheActiveWindowEvenWhenATickFindsNoMatches() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)
        val trackedFixture = liveFixture(fixtureId = 233, minute = 23)

        every { liveData.allLiveMatches() } returns listOf(trackedFixture)
        scheduler.captureLiveCharts()

        every { liveData.allLiveMatches() } returns emptyList()
        clock.advanceTo(Instant.parse("2026-06-25T11:00:00Z"))
        scheduler.captureLiveCharts()
        clock.advanceTo(Instant.parse("2026-06-25T11:40:00Z"))
        scheduler.captureLiveCharts()

        verify(exactly = 3) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldCallCaptureLiveOddsForFirstHalfFixtures() {
        val fixture = oddsEligibleFixture(fixtureId = 233, minute = 23)
        every { liveData.allLiveMatches() } returns listOf(fixture)

        underTest.captureLiveOdds()

        verify { liveChartsBetsService.captureLiveOdds(fixture) }
    }

    @Test
    fun shouldCallCaptureLiveOddsForHalfTimeFixtures() {
        val fixture = oddsEligibleFixture(
            fixtureId = 233, minute = 45,
            statusShort = org.footballproject.model.MatchStatus.HALF_TIME.code
        )
        every { liveData.allLiveMatches() } returns listOf(fixture)

        underTest.captureLiveOdds()

        verify { liveChartsBetsService.captureLiveOdds(fixture) }
    }

    @Test
    fun shouldNotCallCaptureLiveOddsForUntrackedLeague() {
        val tracked = oddsEligibleFixture(fixtureId = 233, minute = 23, leagueId = trackedLeagueId)
        val untracked = oddsEligibleFixture(fixtureId = 999, minute = 23, leagueId = 9999)
        every { liveData.allLiveMatches() } returns listOf(tracked, untracked)

        underTest.captureLiveOdds()

        verify { liveChartsBetsService.captureLiveOdds(tracked) }
        verify(exactly = 0) { liveChartsBetsService.captureLiveOdds(untracked) }
    }

    @Test
    fun shouldReturnEarlyFromCaptureLiveOddsWhenNoTrackedMatchesAreActive() {
        every { liveData.allLiveMatches() } returns emptyList()

        underTest.captureLiveOdds()

        verify(exactly = 0) { liveChartsBetsService.captureLiveOdds(any()) }
    }

    @Test
    fun shouldNotStopCaptureLiveOddsForOtherFixturesWhenOneFixtureFails() {
        val failing = oddsEligibleFixture(fixtureId = 111, minute = 10)
        val healthy = oddsEligibleFixture(fixtureId = 222, minute = 15)
        every { liveData.allLiveMatches() } returns listOf(failing, healthy)
        every { liveChartsBetsService.captureLiveOdds(failing) } throws RuntimeException("API down")

        underTest.captureLiveOdds()

        verify { liveChartsBetsService.captureLiveOdds(failing) }
        verify { liveChartsBetsService.captureLiveOdds(healthy) }
    }

    @Test
    fun shouldNotShrinkTheActiveWindowWhenATickInsideItFindsNoMatches() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)
        val trackedFixture = liveFixture(fixtureId = 233, minute = 23)

        every { liveData.allLiveMatches() } returns listOf(trackedFixture)
        scheduler.captureLiveCharts()

        every { liveData.allLiveMatches() } returns emptyList()
        clock.advanceTo(Instant.parse("2026-06-25T10:10:00Z"))
        scheduler.captureLiveCharts()

        clock.advanceTo(Instant.parse("2026-06-25T10:20:00Z"))
        scheduler.captureLiveCharts()

        verify(exactly = 3) { liveData.allLiveMatches() }
    }


    @Test
    fun shouldSkipCaptureLiveOddsTickWhenItsOwnIdleBackoffHasNotElapsed() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)

        every { liveData.allLiveMatches() } returns emptyList()

        scheduler.captureLiveOdds()
        clock.advanceTo(Instant.parse("2026-06-25T10:10:00Z"))
        scheduler.captureLiveOdds()

        verify(exactly = 1) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldCheckCaptureLiveOddsAgainOnceItsOwnIdleBackoffHasElapsed() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)

        every { liveData.allLiveMatches() } returns emptyList()

        scheduler.captureLiveOdds()
        clock.advanceTo(Instant.parse("2026-06-25T10:30:00Z"))
        scheduler.captureLiveOdds()

        verify(exactly = 2) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldKeepCallingCaptureLiveOddsThroughoutItsActiveWindowEvenWhenSomeTicksHaveNoMatches() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)
        val trackedFixture = oddsEligibleFixture(fixtureId = 233, minute = 23)

        every { liveData.allLiveMatches() } returns listOf(trackedFixture)
        scheduler.captureLiveOdds()

        every { liveData.allLiveMatches() } returns emptyList()
        clock.advanceTo(Instant.parse("2026-06-25T11:00:00Z"))
        scheduler.captureLiveOdds()
        clock.advanceTo(Instant.parse("2026-06-25T11:40:00Z"))
        scheduler.captureLiveOdds()

        verify(exactly = 3) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldNotShrinkOddsActiveWindowWhenATickInsideItFindsNoMatches() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)
        val trackedFixture = oddsEligibleFixture(fixtureId = 233, minute = 23)

        every { liveData.allLiveMatches() } returns listOf(trackedFixture)
        scheduler.captureLiveOdds()

        every { liveData.allLiveMatches() } returns emptyList()
        clock.advanceTo(Instant.parse("2026-06-25T10:10:00Z"))
        scheduler.captureLiveOdds()
        clock.advanceTo(Instant.parse("2026-06-25T10:20:00Z"))
        scheduler.captureLiveOdds()

        verify(exactly = 3) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldNotSuspendCaptureLiveOddsWhenChartsIdleBackoffIsActive() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)

        every { liveData.allLiveMatches() } returns emptyList()
        scheduler.captureLiveCharts()

        clock.advanceTo(Instant.parse("2026-06-25T10:10:00Z"))
        scheduler.captureLiveOdds()

        verify(exactly = 2) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldNotSuspendCaptureLiveChartsWhenOddsIdleBackoffIsActive() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:00:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)

        every { liveData.allLiveMatches() } returns emptyList()
        scheduler.captureLiveOdds()

        clock.advanceTo(Instant.parse("2026-06-25T10:10:00Z"))
        scheduler.captureLiveCharts()

        verify(exactly = 2) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldReturnFixtureUnchangedWhenElapsedIsAlreadyPresent() {
        val fixture = fixtureWithElapsed(fixtureId = 233, elapsed = 23)

        val result = underTest.withEstimatedElapsed(fixture, Instant.parse("2026-06-25T11:00:00Z"))

        assert(result == fixture)
    }

    @Test
    fun shouldEstimateElapsedFromKickoffTimeWhenElapsedIsNull() {
        val fixture = fixtureWithElapsed(
            fixtureId = 233,
            elapsed = null,
            date = "2026-06-25T10:00:00+00:00"
        )

        val result = underTest.withEstimatedElapsed(fixture, Instant.parse("2026-06-25T10:35:00Z"))

        assert(result.fixture.status?.elapsed == 35)
    }

    @Test
    fun shouldClampEstimatedElapsedToZeroWhenNowIsBeforeKickoff() {
        val fixture = fixtureWithElapsed(
            fixtureId = 233,
            elapsed = null,
            date = "2026-06-25T10:40:00+00:00"
        )

        val result = underTest.withEstimatedElapsed(fixture, Instant.parse("2026-06-25T10:35:00Z"))

        assert(result.fixture.status?.elapsed == 0)
    }

    @Test
    fun shouldLeaveFixtureUnchangedWhenElapsedIsNullAndDateCannotBeParsed() {
        val fixture = fixtureWithElapsed(
            fixtureId = 233,
            elapsed = null,
            date = "Unknown Date"
        )

        val result = underTest.withEstimatedElapsed(fixture, Instant.parse("2026-06-25T10:35:00Z"))

        assert(result == fixture)
        assert(result.fixture.status?.elapsed == null)
    }

    @Test
    fun shouldLeaveFixtureUnchangedWhenStatusIsNull() {
        val fixture = LiveFixtureResponse(
            fixture = Fixture(id = 233, date = "2026-06-25T10:00:00+00:00", status = null),
            league = League(id = trackedLeagueId, season = 2026),
            teams = Teams(),
            goals = Goal(),
            score = Score(),
            events = emptyList()
        )

        val result = underTest.withEstimatedElapsed(fixture, Instant.parse("2026-06-25T10:35:00Z"))

        assert(result == fixture)
    }

    @Test
    fun shouldForwardTheEstimatedElapsedFixtureToCaptureLiveIndicators() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:35:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)
        val fixture = fixtureWithElapsed(fixtureId = 233, elapsed = null, date = "2026-06-25T10:00:00+00:00")

        every { liveData.allLiveMatches() } returns listOf(fixture)
        every { liveChartsService.captureLiveIndicators(any()) } just Runs

        scheduler.captureLiveCharts()

        verify { liveChartsService.captureLiveIndicators(match { it.fixture.status?.elapsed == 35 }) }
    }

    @Test
    fun shouldForwardTheEstimatedElapsedFixtureToCaptureLiveOdds() {
        val clock = MutableClock(Instant.parse("2026-06-25T10:35:00Z"))
        val scheduler = LiveChartScheduler(liveData, liveChartsService, liveChartsBetsService, chartsProps, clock)
        val fixture = fixtureWithElapsed(fixtureId = 233, elapsed = null, date = "2026-06-25T10:00:00+00:00")

        every { liveData.allLiveMatches() } returns listOf(fixture)
        every { liveChartsBetsService.captureLiveOdds(any()) } just Runs

        scheduler.captureLiveOdds()

        verify { liveChartsBetsService.captureLiveOdds(match { it.fixture.status?.elapsed == 35 }) }
    }
}
