package match.insights.service

import match.insights.apidata.LiveData
import match.insights.apidata.MatchesData
import match.insights.clientData.LiveFixtureResponse
import match.insights.clientData.LiveStatistic
import match.insights.datamanipulation.livecharts.ControlChartManipulation
import match.insights.datamanipulation.livecharts.GoalThreatChartManipulation
import match.insights.datamanipulation.livecharts.MomentumChartManipulation
import match.insights.datamanipulation.livecharts.normalize
import match.insights.model.LiveChartPoint
import match.insights.model.LiveIndicatorType
import match.insights.props.ChartsProps
import match.insights.repository.LiveChartsRepository
import match.insights.response.LiveChartableMatch
import match.insights.model.MatchStatus
import match.insights.response.FixtureChartsResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LiveChartsService(
    private val matchesData: MatchesData,
    private val liveData: LiveData,
    private val liveChartsRepository: LiveChartsRepository,
    private val liveMomentumManipulation: MomentumChartManipulation,
    private val liveControlManipulation: ControlChartManipulation,
    private val liveGoalThreatManipulation: GoalThreatChartManipulation,
    private val chartsProps: ChartsProps
) {

    private val log = LoggerFactory.getLogger(LiveChartsService::class.java)

    fun fixtureIndicators(fixtureId: Int): Map<String, List<LiveChartPoint>> =
        LiveIndicatorType.values().associate { indicatorType ->
            indicatorType.name.lowercase() to (
                    liveChartsRepository
                        .getFixture(fixtureId, indicatorType)?.points ?: emptyList())
        }

    fun allFixturesIndicators(): List<FixtureChartsResponse> {
        val fixturesByIndicator = fixturesByIndicatorMap()
        val allFixtures = fixturesByIndicator.values.flatten()

        return allFixtures.distinctBy { it.fixtureId }.map { fixture ->
            FixtureChartsResponse(
                fixtureId = fixture.fixtureId,
                homeTeamName = fixture.homeTeamName,
                awayTeamName = fixture.awayTeamName,
                indicators = fixturesByIndicator.entries.associate { (type, fixtures) ->
                    type.name.lowercase() to (fixtures.find { it.fixtureId == fixture.fixtureId }?.points
                        ?: emptyList())
                }
            )
        }
    }

    fun activeLeagueIds(): List<Int> = chartsProps.trackedLeagueIds

    fun trackableLiveMatches(): List<LiveChartableMatch> =
        liveData.allLiveMatches()
            .filter { it.league.id in chartsProps.trackedLeagueIds }
            .map {
                LiveChartableMatch(
                    fixtureId = it.fixture.id,
                    homeTeamName = it.teams.home?.name ?: "Unknown Team",
                    awayTeamName = it.teams.away?.name ?: "Unknown Team"
                )
            }

    fun captureMomentum(fixture: LiveFixtureResponse) {
        if (!isAValidCapture(fixture)) {
            log.warn("Skipping live chart capture for fixture ${fixture.fixture.id}: elapsed is null")
            return
        }

        val currentSnapshot = liveMomentumManipulation.currentSnapShot(
            homeAwayStats(fixture),
            fixture.fixture.status?.elapsed ?: 0
        )
        val previousPoints = getPreviousPoints(fixtureId = fixture.fixture.id, LiveIndicatorType.MOMENTUM)

        val baseline = liveMomentumManipulation.previousSnapShot(
            previousPoints,
            current = currentSnapshot,
            chartsProps.momentumWindowSize
        )

        val value = liveMomentumManipulation.confidenceScaledOscillate(
            currentSnapshot.homeScore - baseline.homeScore,
            currentSnapshot.awayScore - baseline.awayScore,
            baseline.capturedAt
        )

        liveChartsRepository.append(
            fixture.fixture.id,
            LiveIndicatorType.MOMENTUM,
            fixture.teams.home?.name ?: "Unknown Team",
            fixture.teams.away?.name ?: "Unknown Team",
            liveMomentumManipulation.chartPoint(
                value,
                currentSnapshot
            )
        )
    }

    fun captureControl(fixture: LiveFixtureResponse) {
        if (!isAValidCapture(fixture)) {
            log.warn("Skipping live chart capture for fixture ${fixture.fixture.id}: elapsed is null")
            return
        }

        val currentSnapshot = liveControlManipulation.currentSnapShot(
            homeAwayStats(fixture),
            fixture.fixture.status?.elapsed ?: 0
        )
        val previousPoints = getPreviousPoints(fixtureId = fixture.fixture.id, LiveIndicatorType.CONTROL)

        val baseline = liveControlManipulation.previousSnapShot(
            previousPoints,
            currentSnapshot,
            chartsProps.controlWindowSize
        )

        val possession = liveControlManipulation.windowedPossession(currentSnapshot, baseline)
        val passPercentage = liveControlManipulation.windowedPassPercentage(currentSnapshot, baseline)
        val weighted = liveControlManipulation.weightedScore(possession, passPercentage)
        val (homePercent, _) = normalize(weighted.first, weighted.second)

        liveChartsRepository.append(
            fixture.fixture.id,
            LiveIndicatorType.CONTROL,
            fixture.teams.home?.name ?: "Unknown Team",
            fixture.teams.away?.name ?: "Unknown Team",
            liveControlManipulation.chartPoint(homePercent, currentSnapshot)
        )
    }

    fun captureGoalThreat(fixture: LiveFixtureResponse) {
        if (!isAValidCapture(fixture)) {
            log.warn("Skipping live chart capture for fixture ${fixture.fixture.id}: elapsed is null")
            return
        }

        val currentSnapshot = liveGoalThreatManipulation.currentSnapShot(
            homeAwayStats(fixture),
            fixture.fixture.status?.elapsed ?: 0
        )
        val previousPoints = getPreviousPoints(fixtureId = fixture.fixture.id, LiveIndicatorType.GOAL_THREAT)

        val baseline = liveGoalThreatManipulation.previousSnapShot(
            previousPoints,
            currentSnapshot,
            chartsProps.goalThreatWindowSize
        )

        val (homeDelta, awayDelta) = liveGoalThreatManipulation.delta(currentSnapshot, baseline)
        val (homePercent, _) = normalize(homeDelta, awayDelta)

        liveChartsRepository.append(
            fixture.fixture.id,
            LiveIndicatorType.GOAL_THREAT,
            fixture.teams.home?.name ?: "Unknown Team",
            fixture.teams.away?.name ?: "Unknown Team",
            liveGoalThreatManipulation.chartPoint(homePercent, currentSnapshot)
        )
    }

    fun captureLiveIndicators(fixture: LiveFixtureResponse) {
        captureMomentum(fixture)
        captureControl(fixture)
        captureGoalThreat(fixture)
    }

    private fun fixturesByIndicatorMap() = LiveIndicatorType.values()
        .associateWith { liveChartsRepository.getAllFixtures(it) }

    private fun homeAwayStats(fixture: LiveFixtureResponse): Pair<List<LiveStatistic>, List<LiveStatistic>> {
        val stats = matchesData.statistics(fixture.fixture.id)
        val homeStats = stats.getOrNull(0)?.statistics ?: emptyList()
        val awayStats = stats.getOrNull(1)?.statistics ?: emptyList()

        return Pair(homeStats, awayStats)
    }

    private fun getPreviousPoints(fixtureId: Int, indicatorType: LiveIndicatorType): List<LiveChartPoint> {
        return liveChartsRepository.getFixture(fixtureId, indicatorType)?.points ?: emptyList()
    }

    private fun isAValidCapture(fixture: LiveFixtureResponse): Boolean {
        if (fixture.fixture.status?.elapsed == null) return false
        return MatchStatus.entries.find { it.code == fixture.fixture.status?.short }
            ?.isValidLiveChartStatus() ?: false

    }
}
