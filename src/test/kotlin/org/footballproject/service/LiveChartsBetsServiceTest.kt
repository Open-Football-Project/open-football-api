package org.footballproject.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.OddsData
import org.footballproject.clientData.Fixture
import org.footballproject.clientData.Goal
import org.footballproject.clientData.League
import org.footballproject.clientData.LiveFixtureResponse
import org.footballproject.clientData.LiveOddsMarket
import org.footballproject.clientData.LiveOddsValue
import org.footballproject.clientData.MatchStatus
import org.footballproject.clientData.Score
import org.footballproject.clientData.Team
import org.footballproject.clientData.Teams
import org.footballproject.model.LiveOddsFixture
import org.footballproject.model.LiveOddsMarketEntry
import org.footballproject.model.LiveOddsSnapshot
import org.footballproject.props.ChartsProps
import org.footballproject.repository.LiveChartsBetsRepository
import org.footballproject.response.LiveChartableMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class LiveChartsBetsServiceTest {

    private val oddsData: OddsData = mockk()
    private val liveChartsBetsRepository: LiveChartsBetsRepository = mockk(relaxed = true)

    private val chartsProps = ChartsProps(
        ttlSeconds = 14400,
        pollingMilli = 180000,
        schedulingEnabled = true,
        oddsTrackedMarketNames = setOf("fulltime_result", "3_way_handicap", "some_other_market")
    )

    private val underTest = LiveChartsBetsService(oddsData, liveChartsBetsRepository, chartsProps)

    private fun fixture(fixtureId: Int = 1539007, minute: Int = 23) = LiveFixtureResponse(
        fixture = Fixture(
            id = fixtureId,
            status = MatchStatus(short = org.footballproject.model.MatchStatus.FIRST_HALF.code, elapsed = minute)
        ),
        league = League(id = 39, season = 2026),
        teams = Teams(home = Team(name = "Home FC"), away = Team(name = "Away FC")),
        goals = Goal(),
        score = Score(),
        events = emptyList()
    )

    private fun market(id: Int, name: String, vararg values: LiveOddsValue) =
        LiveOddsMarket(id = id, name = name, values = values.toList())

    private fun value(label: String, odd: String, suspended: Boolean = false, main: Boolean? = null) =
        LiveOddsValue(value = label, odd = odd, handicap = null, main = main, suspended = suspended)

    private fun snapshotAt(minute: Int, label: String, odd: String) =
        LiveOddsSnapshot(label = label, odd = odd, minute = minute, capturedAt = Instant.parse("2026-06-22T10:00:00Z"))

    @Test
    fun shouldDropSuspendedValuesBeforeStoring() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.5", suspended = true), value("Away", "3.0"))
        )

        underTest.captureLiveOdds(fixture())

        verify {
            liveChartsBetsRepository.appendSnapshots(
                1539007, 59, "fulltime_result", "Home FC", "Away FC",
                match { it.map { s -> s.label to s.odd } == listOf("Away" to "3.0") }
            )
        }
    }

    @Test
    fun shouldDropZeroOddValuesBeforeStoring() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "0"), value("Away", "2.5"))
        )

        underTest.captureLiveOdds(fixture())

        verify {
            liveChartsBetsRepository.appendSnapshots(
                1539007, 59, "fulltime_result", "Home FC", "Away FC",
                match { it.map { s -> s.label to s.odd } == listOf("Away" to "2.5") }
            )
        }
    }

    @Test
    fun shouldKeepOnlyMainTrueValuesWhenAnyValueIsMain() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(
                33,
                "3-Way Handicap",
                value("Home -0.5", "1.9", main = true),
                value("Away +0.5", "2.1", main = false)
            )
        )

        underTest.captureLiveOdds(fixture())

        verify {
            liveChartsBetsRepository.appendSnapshots(
                1539007, 33, "3_way_handicap", "Home FC", "Away FC",
                match { it.map { s -> s.label to s.odd } == listOf("Home -0.5" to "1.9") }
            )
        }
    }

    @Test
    fun shouldKeepAllValuesWhenNoneAreMarkedMain() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.7"), value("Away", "4.5"))
        )

        underTest.captureLiveOdds(fixture())

        verify {
            liveChartsBetsRepository.appendSnapshots(
                1539007, 59, "fulltime_result", "Home FC", "Away FC",
                match { it.map { s -> s.label to s.odd } == listOf("Home" to "1.7", "Away" to "4.5") }
            )
        }
    }

    @Test
    fun shouldSkipMarketWhenNoValidValuesRemainAfterSanitization() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.5", suspended = true), value("Away", "0"))
        )

        underTest.captureLiveOdds(fixture())

        verify(exactly = 0) { liveChartsBetsRepository.appendSnapshots(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldKeepMarketWhenOnlySomeValuesAreInvalidAfterSanitization() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.8"), value("Away", "4.0", suspended = true))
        )

        underTest.captureLiveOdds(fixture())

        verify { liveChartsBetsRepository.appendSnapshots(1539007, 59, "fulltime_result", "Home FC", "Away FC", any()) }
    }

    @Test
    fun shouldCaptureEveryMarketReturnedByTheApi() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.8")),
            market(999, "Some Other Market", value("Yes", "2.0"))
        )

        underTest.captureLiveOdds(fixture())

        verify { liveChartsBetsRepository.appendSnapshots(1539007, 59, "fulltime_result", "Home FC", "Away FC", any()) }
        verify {
            liveChartsBetsRepository.appendSnapshots(1539007, 999, "some_other_market", "Home FC", "Away FC", any())
        }
    }

    @Test
    fun shouldSkipMarketsNotInTrackedMarketNames() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(36, "Over/Under Line", value("Over", "1.8"))
        )

        underTest.captureLiveOdds(fixture())

        verify(exactly = 0) { liveChartsBetsRepository.appendSnapshots(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldSkipFixtureWhenLiveOddsResponseIsEmpty() {
        every { oddsData.liveOdds(1539007) } returns emptyList()

        underTest.captureLiveOdds(fixture())

        verify(exactly = 0) { liveChartsBetsRepository.appendSnapshots(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun shouldUseElapsedMinuteFromFixtureInStoredSnapshots() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.8"))
        )

        underTest.captureLiveOdds(fixture(minute = 55))

        verify {
            liveChartsBetsRepository.appendSnapshots(
                1539007, 59, "fulltime_result", "Home FC", "Away FC",
                match { it.all { s -> s.minute == 55 } }
            )
        }
    }

    @Test
    fun shouldNormalizeMarketNameToLowercaseWithUnderscores() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "  Fulltime Result! ", value("Home", "1.8"))
        )

        underTest.captureLiveOdds(fixture())

        verify {
            liveChartsBetsRepository.appendSnapshots(1539007, 59, "fulltime_result", "Home FC", "Away FC", any())
        }
    }

    @Test
    fun shouldPassTeamNamesFromFixtureIntoStoredSnapshots() {
        every { oddsData.liveOdds(1539007) } returns listOf(
            market(59, "Fulltime Result", value("Home", "1.8"))
        )

        underTest.captureLiveOdds(
            fixture().copy(teams = Teams(home = Team(name = "Netherlands"), away = Team(name = "Sweden")))
        )

        verify {
            liveChartsBetsRepository.appendSnapshots(1539007, 59, "fulltime_result", "Netherlands", "Sweden", any())
        }
    }


    @Test
    fun shouldReturnEmptyListWhenNoMarketsStoredForFixture() {
        every { liveChartsBetsRepository.getAllMarkets(1539007) } returns emptyList()

        assertThat(underTest.getAllLiveMarkets(1539007)).isEmpty()
    }

    @Test
    fun shouldChunkStoredMarketsIntoGroupsOfThree() {
        every { liveChartsBetsRepository.getAllMarkets(1539007) } returns listOf(
            LiveOddsMarketEntry(59, "fulltime_result", "Home FC", "Away FC", listOf(snapshotAt(23, "Home", "1.7"))),
            LiveOddsMarketEntry(20, "match_corners", "Home FC", "Away FC", listOf(snapshotAt(23, "Over 10.5", "2.0"))),
            LiveOddsMarketEntry(37, "total_corners", "Home FC", "Away FC", listOf(snapshotAt(23, "Over 9.5", "1.9"))),
            LiveOddsMarketEntry(36, "over_under_line", "Home FC", "Away FC", listOf(snapshotAt(23, "Over 2.5", "1.8")))
        )

        val result = underTest.getAllLiveMarkets(1539007)

        assertThat(result).hasSize(2)
        assertThat(result[0].map { it.id to it.name }).containsExactly(
            59 to "fulltime_result",
            20 to "match_corners",
            37 to "total_corners"
        )
        assertThat(result[1].map { it.id to it.name }).containsExactly(36 to "over_under_line")
    }

    @Test
    fun shouldRestructureEachMarketHistoryAsOneSeriesPerLabel() {
        every { liveChartsBetsRepository.getAllMarkets(1539007) } returns listOf(
            LiveOddsMarketEntry(
                59, "fulltime_result", "Home FC", "Away FC",
                listOf(
                    snapshotAt(23, "Home", "1.578"),
                    snapshotAt(23, "Draw", "3.5"),
                    snapshotAt(23, "Away", "5.0"),
                    snapshotAt(28, "Home", "1.615"),
                    snapshotAt(28, "Draw", "3.6"),
                    snapshotAt(28, "Away", "5.5")
                )
            )
        )

        val history = underTest.getAllLiveMarkets(1539007).flatten().first().history

        assertThat(history.keys).containsExactlyInAnyOrder("Home", "Draw", "Away")
        assertThat(history["Home"]!!.map { it.odd }).containsExactly("1.578", "1.615")
        assertThat(history["Draw"]!!.map { it.odd }).containsExactly("3.5", "3.6")
        assertThat(history["Home"]!!.map { it.minute }).containsExactly(23, 28)
    }

    @Test
    fun shouldReturnEmptyListWhenNoFixturesHaveTrackedOdds() {
        every { liveChartsBetsRepository.getTrackedFixtures() } returns emptySet()

        assertThat(underTest.trackedFixtures()).isEmpty()
    }

    @Test
    fun shouldReturnTrackedFixturesFromRepository() {
        every { liveChartsBetsRepository.getTrackedFixtures() } returns setOf(
            LiveOddsFixture(1539007, "Netherlands", "Sweden")
        )

        assertThat(underTest.trackedFixtures()).containsExactly(
            LiveChartableMatch(fixtureId = 1539007, homeTeamName = "Netherlands", awayTeamName = "Sweden")
        )
    }
}
