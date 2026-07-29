package match.insights.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import match.insights.apidata.LiveData
import match.insights.apidata.MatchesData
import match.insights.clientData.Fixture
import match.insights.clientData.Goal
import match.insights.clientData.League
import match.insights.clientData.LiveFixtureResponse
import match.insights.clientData.LiveStatistic
import match.insights.clientData.LiveStatsTeamInfo
import match.insights.clientData.LiveTeamStats
import match.insights.clientData.Score
import match.insights.model.MatchStatus
import match.insights.clientData.Team
import match.insights.clientData.Teams
import match.insights.datamanipulation.livecharts.ControlChartManipulation
import match.insights.datamanipulation.livecharts.GoalThreatChartManipulation
import match.insights.datamanipulation.livecharts.MomentumChartManipulation
import match.insights.model.ControlBaseline
import match.insights.model.GoalThreatBaseline
import match.insights.model.LiveChartPoint
import match.insights.model.LiveIndicatorFixture
import match.insights.model.LiveIndicatorType
import match.insights.model.MomentumBaseline
import match.insights.props.ChartsProps
import match.insights.repository.LiveChartsRepository
import match.insights.response.LiveChartableMatch
import match.insights.response.FixtureChartsResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class LiveChartsServiceTest {

    private val matchesData: MatchesData = mockk()
    private val liveData: LiveData = mockk()
    private val liveChartsRepository: LiveChartsRepository = mockk(relaxed = true)
    private val liveMomentumManipulation: MomentumChartManipulation = mockk()
    private val liveControlManipulation: ControlChartManipulation = mockk()
    private val liveGoalThreatManipulation: GoalThreatChartManipulation = mockk()
    private val trackedLeagueId = 39
    private val chartsProps = ChartsProps(
        ttlSeconds = 14400,
        pollingMilli = 180000,
        trackedLeagueIds = listOf(trackedLeagueId),
        schedulingEnabled = true,
        momentumWindowSize = 5,
        controlWindowSize = 5,
        goalThreatWindowSize = 5
    )

    private val underTest = LiveChartsService(
        matchesData, liveData, liveChartsRepository, liveMomentumManipulation, liveControlManipulation,
        liveGoalThreatManipulation, chartsProps
    )

    private fun liveFixture(fixtureId: Int, minute: Int?, leagueId: Int = trackedLeagueId, statusShort: String = MatchStatus.FIRST_HALF.code) =
        LiveFixtureResponse(
            fixture = Fixture(
                id = fixtureId,
                status = match.insights.clientData.MatchStatus(short = statusShort, elapsed = minute)
            ),
            league = League(id = leagueId, season = 2026),
            teams = Teams(),
            goals = Goal(),
            score = Score(),
            events = emptyList()
        )

    private fun rawPoint(homeScore: Double, awayScore: Double) = LiveChartPoint(
        minute = 0,
        value = 0,
        capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
        homeMomentumScore = homeScore,
        awayMomentumScore = awayScore
    )

    private fun controlPoint(
        homePossessionPercent: Double,
        awayPossessionPercent: Double,
        homeTotalPasses: Double,
        awayTotalPasses: Double,
        homeAccuratePasses: Double,
        awayAccuratePasses: Double,
        minute: Int
    ) = LiveChartPoint(
        minute = minute,
        value = 50,
        capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
        homePossessionPercent = homePossessionPercent,
        awayPossessionPercent = awayPossessionPercent,
        homeTotalPasses = homeTotalPasses,
        awayTotalPasses = awayTotalPasses,
        homeAccuratePasses = homeAccuratePasses,
        awayAccuratePasses = awayAccuratePasses
    )

    private fun goalThreatPoint(homeScore: Double, awayScore: Double, minute: Int) = LiveChartPoint(
        minute = minute,
        value = 50,
        capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
        homeGoalThreatScore = homeScore,
        awayGoalThreatScore = awayScore
    )

    @Test
    fun shouldAppendAMomentumPointBuiltFromTheOscillatedScoreDelta() {
        val fixture = liveFixture(fixtureId = 233, minute = 23)
        val homeStats = LiveTeamStats(LiveStatsTeamInfo(1, "Home", ""), listOf(LiveStatistic("Shots on Goal", 3)))
        val awayStats = LiveTeamStats(LiveStatsTeamInfo(2, "Away", ""), listOf(LiveStatistic("Shots on Goal", 1)))
        val previousPoints = listOf(rawPoint(5.0, 2.0))
        val currentSnapshot = MomentumBaseline(25.0, 10.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)
        val baseline = MomentumBaseline(5.0, 2.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 0)
        val builtPoint = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"), 25.0, 10.0)

        every { matchesData.statistics(233) } returns listOf(homeStats, awayStats)
        every {
            liveMomentumManipulation.currentSnapShot(
                homeStats.statistics to awayStats.statistics,
                23
            )
        } returns currentSnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.MOMENTUM) } returns
                LiveIndicatorFixture(233, "Unknown Team", "Unknown Team", previousPoints)
        every { liveMomentumManipulation.previousSnapShot(previousPoints, currentSnapshot, 5) } returns baseline
        every { liveMomentumManipulation.confidenceScaledOscillate(20.0, 8.0, baseline.capturedAt) } returns 50
        every { liveMomentumManipulation.chartPoint(50, currentSnapshot) } returns builtPoint

        underTest.captureMomentum(fixture)

        verify {
            liveChartsRepository.append(
                233,
                LiveIndicatorType.MOMENTUM,
                "Unknown Team",
                "Unknown Team",
                builtPoint
            )
        }
    }

    @Test
    fun shouldPassTheHomeAndAwayTeamNamesFromTheFixtureWhenAppending() {
        val fixture = liveFixture(fixtureId = 233, minute = 23).copy(
            teams = Teams(home = Team(name = "Home FC"), away = Team(name = "Away FC"))
        )
        val emptySnapshot = MomentumBaseline(0.0, 0.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)
        val builtPoint = LiveChartPoint(23, 0, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveMomentumManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                23
            )
        } returns emptySnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.MOMENTUM) } returns null
        every { liveMomentumManipulation.previousSnapShot(emptyList(), emptySnapshot, 5) } returns emptySnapshot
        every { liveMomentumManipulation.confidenceScaledOscillate(0.0, 0.0, emptySnapshot.capturedAt) } returns 0
        every { liveMomentumManipulation.chartPoint(0, emptySnapshot) } returns builtPoint

        underTest.captureMomentum(fixture)

        verify { liveChartsRepository.append(233, LiveIndicatorType.MOMENTUM, "Home FC", "Away FC", builtPoint) }
    }

    @Test
    fun shouldTreatMissingHomeOrAwayStatsAsEmptyWhenTheClientReturnsFewerThanTwoTeams() {
        val fixture = liveFixture(fixtureId = 233, minute = 10)
        val emptySnapshot = MomentumBaseline(0.0, 0.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 10)
        val builtPoint = LiveChartPoint(10, 0, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveMomentumManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                10
            )
        } returns emptySnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.MOMENTUM) } returns null
        every { liveMomentumManipulation.previousSnapShot(emptyList(), emptySnapshot, 5) } returns emptySnapshot
        every { liveMomentumManipulation.confidenceScaledOscillate(0.0, 0.0, emptySnapshot.capturedAt) } returns 0
        every { liveMomentumManipulation.chartPoint(0, emptySnapshot) } returns builtPoint

        underTest.captureMomentum(fixture)

        verify {
            liveMomentumManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                10
            )
        }
    }

    @Test
    fun shouldSkipAllCapturesWhenElapsedIsNull() {
        val fixture = liveFixture(fixtureId = 233, minute = null)
        val momentumSnapshot = MomentumBaseline(0.0, 0.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 0)
        val controlSnapshot = ControlBaseline(
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
            elapsedMinutes = 0
        )
        val goalThreatSnapshot =
            GoalThreatBaseline(0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 0)
        val point = LiveChartPoint(0, 0, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(any()) } returns emptyList()
        every { liveMomentumManipulation.currentSnapShot(any(), any()) } returns momentumSnapshot
        every { liveMomentumManipulation.previousSnapShot(any(), any(), any()) } returns momentumSnapshot
        every { liveMomentumManipulation.confidenceScaledOscillate(any(), any(), any()) } returns 0
        every { liveMomentumManipulation.chartPoint(any(), any()) } returns point
        every { liveControlManipulation.currentSnapShot(any(), any()) } returns controlSnapshot
        every { liveControlManipulation.previousSnapShot(any(), any(), any()) } returns controlSnapshot
        every { liveControlManipulation.windowedPossession(any(), any()) } returns (0.0 to 0.0)
        every { liveControlManipulation.windowedPassPercentage(any(), any()) } returns (0.0 to 0.0)
        every { liveControlManipulation.weightedScore(any(), any()) } returns (0.0 to 0.0)
        every { liveControlManipulation.chartPoint(any(), any()) } returns point
        every { liveGoalThreatManipulation.currentSnapShot(any(), any()) } returns goalThreatSnapshot
        every { liveGoalThreatManipulation.previousSnapShot(any(), any(), any()) } returns goalThreatSnapshot
        every { liveGoalThreatManipulation.delta(any(), any()) } returns (0.0 to 0.0)
        every { liveGoalThreatManipulation.chartPoint(any(), any()) } returns point

        underTest.captureLiveIndicators(fixture)

        verify(exactly = 0) { liveChartsRepository.append(any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldSkipAllCapturesWhenStatusIsHalfTime() {
        val fixture = liveFixture(fixtureId = 233, minute = 45, statusShort = MatchStatus.HALF_TIME.code)

        underTest.captureLiveIndicators(fixture)

        verify(exactly = 0) { liveChartsRepository.append(any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldSkipAllCapturesWhenStatusIsBreakBeforeExtraTime() {
        val fixture = liveFixture(fixtureId = 233, minute = 90, statusShort = MatchStatus.BREAK_TIME.code)

        underTest.captureLiveIndicators(fixture)

        verify(exactly = 0) { liveChartsRepository.append(any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldSkipAllCapturesWhenStatusIsInterrupted() {
        val fixture = liveFixture(fixtureId = 233, minute = 90, statusShort = MatchStatus.INTERRUPTED.code)

        underTest.captureLiveIndicators(fixture)

        verify(exactly = 0) { liveChartsRepository.append(any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldAppendAControlPointBuiltFromTheWindowedShare() {
        val fixture = liveFixture(fixtureId = 233, minute = 60)
        val homeStats = LiveTeamStats(LiveStatsTeamInfo(1, "Home", ""), listOf(LiveStatistic("Ball Possession", "58%")))
        val awayStats = LiveTeamStats(LiveStatsTeamInfo(2, "Away", ""), listOf(LiveStatistic("Ball Possession", "42%")))
        val previousPoints = listOf(controlPoint(50.0, 50.0, 40.0, 30.0, 32.0, 21.0, minute = 40))
        val currentSnapshot = ControlBaseline(
            58.0,
            42.0,
            100.0,
            70.0,
            86.0,
            49.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
            elapsedMinutes = 60
        )
        val baselineSnapshot = ControlBaseline(
            50.0,
            50.0,
            40.0,
            30.0,
            32.0,
            21.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
            elapsedMinutes = 40
        )
        val builtPoint = LiveChartPoint(60, 61, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns listOf(homeStats, awayStats)
        every {
            liveControlManipulation.currentSnapShot(
                homeStats.statistics to awayStats.statistics,
                60
            )
        } returns currentSnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.CONTROL) } returns
                LiveIndicatorFixture(233, "Unknown Team", "Unknown Team", previousPoints)
        every { liveControlManipulation.previousSnapShot(previousPoints, currentSnapshot, 5) } returns baselineSnapshot
        every { liveControlManipulation.windowedPossession(currentSnapshot, baselineSnapshot) } returns (65.0 to 35.0)
        every {
            liveControlManipulation.windowedPassPercentage(
                currentSnapshot,
                baselineSnapshot
            )
        } returns (90.0 to 70.0)
        every { liveControlManipulation.weightedScore(65.0 to 35.0, 90.0 to 70.0) } returns (72.5 to 45.5)
        every { liveControlManipulation.chartPoint(61, currentSnapshot) } returns builtPoint

        underTest.captureControl(fixture)

        verify {
            liveChartsRepository.append(
                233,
                LiveIndicatorType.CONTROL,
                "Unknown Team",
                "Unknown Team",
                builtPoint
            )
        }
    }

    @Test
    fun shouldPassTheHomeAndAwayTeamNamesFromTheFixtureWhenAppendingControl() {
        val fixture = liveFixture(fixtureId = 233, minute = 23).copy(
            teams = Teams(home = Team(name = "Home FC"), away = Team(name = "Away FC"))
        )
        val emptySnapshot = ControlBaseline(
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
            elapsedMinutes = 23
        )
        val builtPoint = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveControlManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                23
            )
        } returns emptySnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.CONTROL) } returns null
        every { liveControlManipulation.previousSnapShot(emptyList(), emptySnapshot, 5) } returns emptySnapshot
        every { liveControlManipulation.windowedPossession(emptySnapshot, emptySnapshot) } returns (0.0 to 0.0)
        every { liveControlManipulation.windowedPassPercentage(emptySnapshot, emptySnapshot) } returns (0.0 to 0.0)
        every { liveControlManipulation.weightedScore(0.0 to 0.0, 0.0 to 0.0) } returns (0.0 to 0.0)
        every { liveControlManipulation.chartPoint(50, emptySnapshot) } returns builtPoint

        underTest.captureControl(fixture)

        verify { liveChartsRepository.append(233, LiveIndicatorType.CONTROL, "Home FC", "Away FC", builtPoint) }
    }

    @Test
    fun shouldTreatMissingHomeOrAwayStatsAsEmptyForControlWhenTheClientReturnsFewerThanTwoTeams() {
        val fixture = liveFixture(fixtureId = 233, minute = 10)
        val emptySnapshot = ControlBaseline(
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
            elapsedMinutes = 10
        )
        val builtPoint = LiveChartPoint(10, 50, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveControlManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                10
            )
        } returns emptySnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.CONTROL) } returns null
        every { liveControlManipulation.previousSnapShot(emptyList(), emptySnapshot, 5) } returns emptySnapshot
        every { liveControlManipulation.windowedPossession(emptySnapshot, emptySnapshot) } returns (0.0 to 0.0)
        every { liveControlManipulation.windowedPassPercentage(emptySnapshot, emptySnapshot) } returns (0.0 to 0.0)
        every { liveControlManipulation.weightedScore(0.0 to 0.0, 0.0 to 0.0) } returns (0.0 to 0.0)
        every { liveControlManipulation.chartPoint(50, emptySnapshot) } returns builtPoint

        underTest.captureControl(fixture)

        verify { liveControlManipulation.currentSnapShot(emptyList<LiveStatistic>() to emptyList<LiveStatistic>(), 10) }
    }


    @Test
    fun shouldAppendAGoalThreatPointBuiltFromTheWindowedDelta() {
        val fixture = liveFixture(fixtureId = 233, minute = 60)
        val homeStats = LiveTeamStats(LiveStatsTeamInfo(1, "Home", ""), listOf(LiveStatistic("expected_goals", "1.5")))
        val awayStats = LiveTeamStats(LiveStatsTeamInfo(2, "Away", ""), listOf(LiveStatistic("expected_goals", "0.8")))
        val previousPoints = listOf(goalThreatPoint(10.0, 4.0, minute = 40))
        val currentSnapshot =
            GoalThreatBaseline(30.0, 15.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 60)
        val baselineSnapshot =
            GoalThreatBaseline(10.0, 4.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 40)
        val builtPoint = LiveChartPoint(60, 65, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns listOf(homeStats, awayStats)
        every {
            liveGoalThreatManipulation.currentSnapShot(
                homeStats.statistics to awayStats.statistics,
                60
            )
        } returns currentSnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.GOAL_THREAT) } returns
                LiveIndicatorFixture(233, "Unknown Team", "Unknown Team", previousPoints)
        every {
            liveGoalThreatManipulation.previousSnapShot(
                previousPoints,
                currentSnapshot,
                5
            )
        } returns baselineSnapshot
        every { liveGoalThreatManipulation.delta(currentSnapshot, baselineSnapshot) } returns (20.0 to 11.0)
        every { liveGoalThreatManipulation.chartPoint(65, currentSnapshot) } returns builtPoint

        underTest.captureGoalThreat(fixture)

        verify {
            liveChartsRepository.append(
                233,
                LiveIndicatorType.GOAL_THREAT,
                "Unknown Team",
                "Unknown Team",
                builtPoint
            )
        }
    }

    @Test
    fun shouldPassTheHomeAndAwayTeamNamesFromTheFixtureWhenAppendingGoalThreat() {
        val fixture = liveFixture(fixtureId = 233, minute = 23).copy(
            teams = Teams(home = Team(name = "Home FC"), away = Team(name = "Away FC"))
        )
        val emptySnapshot =
            GoalThreatBaseline(0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)
        val builtPoint = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveGoalThreatManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                23
            )
        } returns emptySnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.GOAL_THREAT) } returns null
        every { liveGoalThreatManipulation.previousSnapShot(emptyList(), emptySnapshot, 5) } returns emptySnapshot
        every { liveGoalThreatManipulation.delta(emptySnapshot, emptySnapshot) } returns (0.0 to 0.0)
        every { liveGoalThreatManipulation.chartPoint(50, emptySnapshot) } returns builtPoint

        underTest.captureGoalThreat(fixture)

        verify { liveChartsRepository.append(233, LiveIndicatorType.GOAL_THREAT, "Home FC", "Away FC", builtPoint) }
    }

    @Test
    fun shouldTreatMissingHomeOrAwayStatsAsEmptyForGoalThreatWhenTheClientReturnsFewerThanTwoTeams() {
        val fixture = liveFixture(fixtureId = 233, minute = 10)
        val emptySnapshot =
            GoalThreatBaseline(0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 10)
        val builtPoint = LiveChartPoint(10, 50, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveGoalThreatManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                10
            )
        } returns emptySnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.GOAL_THREAT) } returns null
        every { liveGoalThreatManipulation.previousSnapShot(emptyList(), emptySnapshot, 5) } returns emptySnapshot
        every { liveGoalThreatManipulation.delta(emptySnapshot, emptySnapshot) } returns (0.0 to 0.0)
        every { liveGoalThreatManipulation.chartPoint(50, emptySnapshot) } returns builtPoint

        underTest.captureGoalThreat(fixture)

        verify {
            liveGoalThreatManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                10
            )
        }
    }


    @Test
    fun shouldCaptureMomentumControlAndGoalThreatWhenCapturingLiveIndicators() {
        val fixture = liveFixture(fixtureId = 233, minute = 23)
        val momentumSnapshot = MomentumBaseline(0.0, 0.0, Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)
        val controlSnapshot = ControlBaseline(
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            capturedAt = Instant.parse("2026-06-22T10:00:00Z"),
            elapsedMinutes = 23
        )
        val goalThreatSnapshot =
            GoalThreatBaseline(0.0, 0.0, capturedAt = Instant.parse("2026-06-22T10:00:00Z"), elapsedMinutes = 23)
        val momentumPoint = LiveChartPoint(23, 0, Instant.parse("2026-06-22T10:00:00Z"))
        val controlPointBuilt = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"))
        val goalThreatPointBuilt = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"))

        every { matchesData.statistics(233) } returns emptyList()
        every {
            liveMomentumManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                23
            )
        } returns momentumSnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.MOMENTUM) } returns null
        every { liveMomentumManipulation.previousSnapShot(emptyList(), momentumSnapshot, 5) } returns momentumSnapshot
        every { liveMomentumManipulation.confidenceScaledOscillate(0.0, 0.0, momentumSnapshot.capturedAt) } returns 0
        every { liveMomentumManipulation.chartPoint(0, momentumSnapshot) } returns momentumPoint
        every {
            liveControlManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                23
            )
        } returns controlSnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.CONTROL) } returns null
        every { liveControlManipulation.previousSnapShot(emptyList(), controlSnapshot, 5) } returns controlSnapshot
        every { liveControlManipulation.windowedPossession(controlSnapshot, controlSnapshot) } returns (0.0 to 0.0)
        every { liveControlManipulation.windowedPassPercentage(controlSnapshot, controlSnapshot) } returns (0.0 to 0.0)
        every { liveControlManipulation.weightedScore(0.0 to 0.0, 0.0 to 0.0) } returns (0.0 to 0.0)
        every { liveControlManipulation.chartPoint(50, controlSnapshot) } returns controlPointBuilt
        every {
            liveGoalThreatManipulation.currentSnapShot(
                emptyList<LiveStatistic>() to emptyList<LiveStatistic>(),
                23
            )
        } returns goalThreatSnapshot
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.GOAL_THREAT) } returns null
        every {
            liveGoalThreatManipulation.previousSnapShot(
                emptyList(),
                goalThreatSnapshot,
                5
            )
        } returns goalThreatSnapshot
        every { liveGoalThreatManipulation.delta(goalThreatSnapshot, goalThreatSnapshot) } returns (0.0 to 0.0)
        every { liveGoalThreatManipulation.chartPoint(50, goalThreatSnapshot) } returns goalThreatPointBuilt

        underTest.captureLiveIndicators(fixture)

        verify {
            liveChartsRepository.append(
                233,
                LiveIndicatorType.MOMENTUM,
                "Unknown Team",
                "Unknown Team",
                momentumPoint
            )
        }
        verify {
            liveChartsRepository.append(
                233,
                LiveIndicatorType.CONTROL,
                "Unknown Team",
                "Unknown Team",
                controlPointBuilt
            )
        }
        verify {
            liveChartsRepository.append(
                233,
                LiveIndicatorType.GOAL_THREAT,
                "Unknown Team",
                "Unknown Team",
                goalThreatPointBuilt
            )
        }
    }

    @Test
    fun shouldReturnCombinedIndicatorsKeyedByLowercaseTypeName() {
        val point = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"))
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.MOMENTUM) } returns
                LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(point))

        val result = underTest.fixtureIndicators(233)

        assertThat(result).containsExactlyEntriesOf(
            mapOf("momentum" to listOf(point), "control" to emptyList(), "goal_threat" to emptyList())
        )
    }

    @Test
    fun shouldReturnAnEmptyHistoryWhenTheFixtureHasNoEntryForAnIndicatorYet() {
        every { liveChartsRepository.getFixture(233, LiveIndicatorType.MOMENTUM) } returns null

        val result = underTest.fixtureIndicators(233)

        assertThat(result).containsExactlyEntriesOf(
            mapOf("momentum" to emptyList(), "control" to emptyList(), "goal_threat" to emptyList())
        )
    }

    @Test
    fun shouldReturnOnlyTrackedLeagueMatchesWithTheirBasicFields() {
        val trackedFixture = liveFixture(fixtureId = 233, minute = 23, leagueId = trackedLeagueId).copy(
            teams = Teams(home = Team(name = "Home FC"), away = Team(name = "Away FC"))
        )
        val untrackedFixture = liveFixture(fixtureId = 999, minute = 23, leagueId = 9999)

        every { liveData.allLiveMatches() } returns listOf(trackedFixture, untrackedFixture)

        val result = underTest.trackableLiveMatches()

        assertThat(result).containsExactly(
            LiveChartableMatch(fixtureId = 233, homeTeamName = "Home FC", awayTeamName = "Away FC")
        )
    }

    @Test
    fun shouldReturnAnEmptyListWhenNoTrackedLeagueMatchIsLive() {
        val untrackedFixture = liveFixture(fixtureId = 999, minute = 23, leagueId = 9999)

        every { liveData.allLiveMatches() } returns listOf(untrackedFixture)

        val result = underTest.trackableLiveMatches()

        assertThat(result).isEmpty()
    }

    @Test
    fun shouldReturnAllTrackedFixturesWithTheirIndicatorsWithoutCallingLiveData() {
        val firstPoint = LiveChartPoint(23, 50, Instant.parse("2026-06-22T10:00:00Z"))
        val secondPoint = LiveChartPoint(10, -20, Instant.parse("2026-06-22T10:05:00Z"))
        val firstEntry = LiveIndicatorFixture(233, "Home FC", "Away FC", listOf(firstPoint))
        val secondEntry = LiveIndicatorFixture(234, "Other Home", "Other Away", listOf(secondPoint))

        every { liveChartsRepository.getAllFixtures(LiveIndicatorType.MOMENTUM) } returns listOf(
            firstEntry,
            secondEntry
        )

        val result = underTest.allFixturesIndicators()

        assertThat(result).containsExactly(
            FixtureChartsResponse(
                233, "Home FC", "Away FC",
                mapOf("momentum" to listOf(firstPoint), "control" to emptyList(), "goal_threat" to emptyList())
            ),
            FixtureChartsResponse(
                234, "Other Home", "Other Away",
                mapOf("momentum" to listOf(secondPoint), "control" to emptyList(), "goal_threat" to emptyList())
            )
        )
        verify(exactly = 0) { liveData.allLiveMatches() }
    }

    @Test
    fun shouldReturnAnEmptyListWhenNoFixtureDataExistsInRedis() {
        every { liveChartsRepository.getAllFixtures(LiveIndicatorType.MOMENTUM) } returns emptyList()

        val result = underTest.allFixturesIndicators()

        assertThat(result).isEmpty()
    }

    @Test
    fun shouldReturnTheTrackedLeagueIdsFromConfig() {
        val result = underTest.activeLeagueIds()

        assertThat(result).containsExactly(trackedLeagueId)
    }
}
