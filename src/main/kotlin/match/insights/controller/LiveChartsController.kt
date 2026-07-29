package match.insights.controller

import match.insights.model.LiveChartPoint
import match.insights.response.BetMarketInfo
import match.insights.response.FixtureChartsResponse
import match.insights.response.LiveChartableMatch
import match.insights.service.LiveChartsBetsService
import match.insights.service.LiveChartsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/charts")
class LiveChartsController(
    private val liveChartsService: LiveChartsService,
    private val liveChartsBetsService: LiveChartsBetsService
) {

    @GetMapping("/all/{fixtureId}")
    fun charts(@PathVariable fixtureId: Int): Map<String, List<LiveChartPoint>> =
        liveChartsService.fixtureIndicators(fixtureId)

    @GetMapping("/all")
    fun allCharts(): List<FixtureChartsResponse> =
        liveChartsService.allFixturesIndicators()

    @GetMapping("/matches")
    fun liveMatches(): List<LiveChartableMatch> =
        liveChartsService.trackableLiveMatches()

    @GetMapping("/leagues")
    fun activeLeagueIds(): List<Int> =
        liveChartsService.activeLeagueIds()

    @GetMapping("/markets/{fixtureId}")
    fun betMarkets(@PathVariable fixtureId: Int): List<List<BetMarketInfo>> =
        liveChartsBetsService.getAllLiveMarkets(fixtureId)

    @GetMapping("/odds/fixtures")
    fun oddsFixtures(): List<LiveChartableMatch> =
        liveChartsBetsService.trackedFixtures()
}
