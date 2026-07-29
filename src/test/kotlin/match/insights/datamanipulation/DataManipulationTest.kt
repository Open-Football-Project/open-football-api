package match.insights.datamanipulation

import match.insights.clientData.Bet
import match.insights.clientData.Bookmaker
import match.insights.clientData.FixtureOdds
import match.insights.clientData.OddValue
import org.assertj.core.api.Assertions.within
import match.insights.data.client.ClientMatchResponseData
import match.insights.data.client.ClientOddsData
import match.insights.data.response.OddsResponseData
import match.insights.response.FairOdd
import match.insights.model.Odd
import match.insights.model.OddFeeling
import match.insights.model.TeamRestStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataManipulationTest {
    val underTest = DataManipulation()

    @Test
    fun willBuildHomeAndAwayFormFromMatchResults() {
        val matches = mapOf(
            33 to ClientMatchResponseData.matchResponseList,
            40 to ClientMatchResponseData.matchResponseList
        )

        val result = underTest.buildLiveHomeAwayForm(33, 40, matches)

        assertThat(result.homeForm).containsExactly("W", "W", "L", "D")
        assertThat(result.awayForm).containsExactly("L", "L", "W", "D")
        assertThat(result.isHomeHot).isFalse()
        assertThat(result.isHomeCold).isFalse()
        assertThat(result.isAwayHot).isFalse()
        assertThat(result.isAwayCold).isFalse()
    }

    @Test
    fun willMarkHomeHotAndAwayColdWhenTrailingThreeResultsQualify() {
        val manUtdWins = List(3) { ClientMatchResponseData.matchResponse }
        val matches = mapOf(33 to manUtdWins, 40 to manUtdWins)

        val result = underTest.buildLiveHomeAwayForm(33, 40, matches)

        assertThat(result.homeForm).containsExactly("W", "W", "W")
        assertThat(result.isHomeHot).isTrue()
        assertThat(result.isAwayCold).isTrue()
        assertThat(result.isHomeCold).isFalse()
        assertThat(result.isAwayHot).isFalse()
    }

    @Test
    fun willMarkHomeColdAndAwayHotWhenTrailingThreeResultsQualify() {
        val liverpoolWins = List(3) { ClientMatchResponseData.matchResponseList[2] }
        val matches = mapOf(33 to liverpoolWins, 40 to liverpoolWins)

        val result = underTest.buildLiveHomeAwayForm(33, 40, matches)

        assertThat(result.homeForm).containsExactly("L", "L", "L")
        assertThat(result.isHomeCold).isTrue()
        assertThat(result.isAwayHot).isTrue()
        assertThat(result.isHomeHot).isFalse()
        assertThat(result.isAwayCold).isFalse()
    }

    @Test
    fun willNotMarkHotOrColdWhenFormHasFewerThanThreeResults() {
        val twoMatches = ClientMatchResponseData.matchResponseList.take(2)
        val matches = mapOf(33 to twoMatches, 40 to twoMatches)

        val result = underTest.buildLiveHomeAwayForm(33, 40, matches)

        assertThat(result.isHomeHot).isFalse()
        assertThat(result.isHomeCold).isFalse()
        assertThat(result.isAwayHot).isFalse()
        assertThat(result.isAwayCold).isFalse()
    }

    @Test
    fun willReturnEmptyFormWhenTeamNotInMap() {
        val result = underTest.buildLiveHomeAwayForm(33, 40, emptyMap())

        assertThat(result.homeForm).isEmpty()
        assertThat(result.awayForm).isEmpty()
        assertThat(result.isHomeHot).isFalse()
        assertThat(result.isHomeCold).isFalse()
        assertThat(result.isAwayHot).isFalse()
        assertThat(result.isAwayCold).isFalse()
    }

    @Test
    fun willReturnLastFiveResults() {
        val result = underTest.lastFiveResults(
            teamId = 33,
            ClientMatchResponseData.matchResponseList
        )

        assertThat(result).containsExactly("W", "W", "L", "D")
    }

    @Test
    fun willExtractFixtureOdds() {
        val odds = ClientOddsData.mockResponse
        val result = underTest.extractBets(odds)

        assertThat(result).isNotNull
        assertThat(result[0].betName).isEqualTo(OddsResponseData.bets[0].betName)
        assertThat(result[1].betName).isEqualTo(OddsResponseData.bets[1].betName)
    }

    @Test
    fun willCheckDaysDifferenceBetweenTwoDates() {

        assertThat(underTest.daysBetween(null, "2025-08-04T16:30:00+00:00"))
            .isNull()
        assertThat(underTest.daysBetween("2025-08-04T16:30:00+00:00", null))
            .isNull()
        assertThat(underTest.daysBetween("", "2025-08-04T16:30:00+00:00"))
            .isNull()
        assertThat(underTest.daysBetween("2025-08-04T16:30:00+00:00", ""))
            .isNull()
        assertThat(underTest.daysBetween("2025-08-02T16:30:00+00:00", "2025-08-04T16:30:00+00:00"))
            .isEqualTo(2)

    }

    @Test
    fun willGetTheRightStatus() {
        assertThat(underTest.teamRestStatus(7)).isEqualTo(TeamRestStatus.GOOD_REST.status)
        assertThat(underTest.teamRestStatus(3)).isEqualTo(TeamRestStatus.MODERATE_CONGESTION.status)
        assertThat(underTest.teamRestStatus(1)).isEqualTo(TeamRestStatus.SEVERE_CONGESTION.status)
        assertThat(underTest.teamRestStatus(-1)).isEqualTo(TeamRestStatus.UNKNOWN_STATE.status)
    }


    @Test
    fun shouldCaptureOddsFeeling() {
        val result = underTest.oddsFeeling(
            ClientOddsData.mockResponse
        )

        assertThat(result).isEqualTo(
            mapOf
                (
                Odd.HOME to OddFeeling.STRONG,
                Odd.DRAW to OddFeeling.WEAK,
                Odd.AWAY to OddFeeling.WEAK
            )
        )

    }

    @Test
    fun filterAndValidate_keepsOnlyMatchingOutcomesWhenFilterIsProvided() {
        val values = listOf(
            OddValue("Over 1.5", "1.25"),
            OddValue("Over 2.5", "1.70"),
            OddValue("Under 2.5", "2.10"),
            OddValue("Over 3.5", "2.50")
        )

        val result = underTest.filterAndValidate(values, listOf("Over 2.5", "Under 2.5"))

        assertThat(result.map { it.value }).containsExactly("Over 2.5", "Under 2.5")
    }

    @Test
    fun filterAndValidate_returnsAllOutcomesWhenNoFilterIsProvided() {
        val values = listOf(
            OddValue("Home", "1.80"),
            OddValue("Draw", "3.50"),
            OddValue("Away", "4.50")
        )

        val result = underTest.filterAndValidate(values, outcomeFilter = null)

        assertThat(result).hasSize(3)
    }

    @Test
    fun filterAndValidate_dropsOutcomesWithUnparseableOdds() {
        val values = listOf(
            OddValue("Over 2.5", "1.70"),
            OddValue("Under 2.5", "N/A")
        )

        val result = underTest.filterAndValidate(values, outcomeFilter = null)

        assertThat(result.map { it.value }).containsExactly("Over 2.5")
    }

    @Test
    fun filterAndValidate_matchesOutcomeLabelsIgnoringCase() {
        val values = listOf(
            OddValue("Over 2.5", "1.70"),
            OddValue("Under 2.5", "2.10")
        )

        val result = underTest.filterAndValidate(values, listOf("over 2.5", "UNDER 2.5"))

        assertThat(result).hasSize(2)
    }

    @Test
    fun normalizeImplied_probabilitiesSumToOne() {
        val values = listOf(
            OddValue("Home", "1.80"),
            OddValue("Draw", "3.50"),
            OddValue("Away", "4.50")
        )

        val result = underTest.normalizeImplied(values)

        val sum = result.sumOf { it.second }
        assertThat(sum).isCloseTo(1.0, within(0.0001))
    }

    @Test
    fun normalizeImplied_equalOddsProduceEqualProbabilities() {
        val values = listOf(
            OddValue("Yes", "2.00"),
            OddValue("No", "2.00")
        )

        val result = underTest.normalizeImplied(values)

        assertThat(result[0].second).isCloseTo(0.5, within(0.0001))
        assertThat(result[1].second).isCloseTo(0.5, within(0.0001))
    }

    @Test
    fun normalizeImplied_preservesOutcomeLabels() {
        val values = listOf(
            OddValue("Over 2.5", "1.70"),
            OddValue("Under 2.5", "2.10")
        )

        val result = underTest.normalizeImplied(values)

        assertThat(result[0].first).isEqualTo("Over 2.5")
        assertThat(result[1].first).isEqualTo("Under 2.5")
    }

    @Test
    fun normalizeImplied_lowerOddProducesHigherProbability() {
        val values = listOf(
            OddValue("Home", "1.50"),
            OddValue("Away", "3.00")
        )

        val result = underTest.normalizeImplied(values)
        val homeProb = result.first { it.first == "Home" }.second
        val awayProb = result.first { it.first == "Away" }.second

        assertThat(homeProb).isGreaterThan(awayProb)
    }

    @Test
    fun fairOddsFrom_computesFairOddAsInverseOfAverageProbability() {
        val outcomeLabels = listOf("Home", "Away")
        val bookmakerProbabilities = listOf(
            listOf("Home" to 0.6, "Away" to 0.4),
            listOf("Home" to 0.4, "Away" to 0.6)
        )

        val result = underTest.fairOddsFrom(outcomeLabels, bookmakerProbabilities)

        val homeOdd = result.first { it.label == "Home" }.odd
        val awayOdd = result.first { it.label == "Away" }.odd
        assertThat(homeOdd).isCloseTo(2.0, within(0.0001))
        assertThat(awayOdd).isCloseTo(2.0, within(0.0001))
    }

    @Test
    fun fairOddsFrom_singleBookmakerFairOddEqualsItsOwnNormalizedOdd() {
        val outcomeLabels = listOf("Yes", "No")
        val bookmakerProbabilities = listOf(
            listOf("Yes" to 0.6, "No" to 0.4)
        )

        val result = underTest.fairOddsFrom(outcomeLabels, bookmakerProbabilities)

        assertThat(result.first { it.label == "Yes" }.odd).isCloseTo(1.0 / 0.6, within(0.0001))
        assertThat(result.first { it.label == "No" }.odd).isCloseTo(1.0 / 0.4, within(0.0001))
    }

    @Test
    fun fairOddsFrom_ignoresBookmakersMissingALabel() {
        val outcomeLabels = listOf("Home", "Draw", "Away")
        val bookmakerProbabilities = listOf(
            listOf("Home" to 0.5, "Draw" to 0.25, "Away" to 0.25),
            listOf("Home" to 0.6, "Away" to 0.4) // missing "Draw"
        )

        val result = underTest.fairOddsFrom(outcomeLabels, bookmakerProbabilities)

        
        assertThat(result.first { it.label == "Draw" }.odd).isCloseTo(1.0 / 0.25, within(0.0001))
        
        assertThat(result.first { it.label == "Home" }.odd).isCloseTo(1.0 / 0.55, within(0.0001))

        assertThat(result.first { it.label == "Away" }.odd).isCloseTo(1.0 / 0.325, within(0.0001))
    }

    @Test
    fun fairOddsFrom_preservesOutcomeLabels() {
        val outcomeLabels = listOf("Over 2.5", "Under 2.5")
        val bookmakerProbabilities = listOf(
            listOf("Over 2.5" to 0.55, "Under 2.5" to 0.45)
        )

        val result = underTest.fairOddsFrom(outcomeLabels, bookmakerProbabilities)

        assertThat(result.map { it.label }).containsExactly("Over 2.5", "Under 2.5")
    }

    @Test
    fun toBookmakerLine_flagsOutcomeAsValueWhenOddExceedsFairOdd() {
        val fairOddByLabel = mapOf("Home" to FairOdd("Home", 2.00), "Away" to FairOdd("Away", 3.00))
        val values = listOf(OddValue("Home", "2.10"), OddValue("Away", "2.80"))

        val result = underTest.toBookmakerLine("TestBook", values, fairOddByLabel)

        assertThat(result.outcomes.first { it.label == "Home" }.isValue).isTrue()
        assertThat(result.outcomes.first { it.label == "Away" }.isValue).isFalse()
    }

    @Test
    fun toBookmakerLine_doesNotFlagValueWhenOddEqualsOrIsBelowFairOdd() {
        val fairOddByLabel = mapOf("Yes" to FairOdd("Yes", 1.80), "No" to FairOdd("No", 2.10))
        val values = listOf(OddValue("Yes", "1.80"), OddValue("No", "2.05"))

        val result = underTest.toBookmakerLine("TestBook", values, fairOddByLabel)

        assertThat(result.outcomes.first { it.label == "Yes" }.isValue).isFalse()
        assertThat(result.outcomes.first { it.label == "No" }.isValue).isFalse()
    }

    @Test
    fun toBookmakerLine_preservesBookmakerNameAndOutcomeLabels() {
        val fairOddByLabel = mapOf("Over 2.5" to FairOdd("Over 2.5", 1.65), "Under 2.5" to FairOdd("Under 2.5", 2.40))
        val values = listOf(OddValue("Over 2.5", "1.70"), OddValue("Under 2.5", "2.30"))

        val result = underTest.toBookmakerLine("Pinnacle", values, fairOddByLabel)

        assertThat(result.name).isEqualTo("Pinnacle")
        assertThat(result.outcomes.map { it.label }).containsExactly("Over 2.5", "Under 2.5")
    }

    @Test
    fun toBookmakerLine_dropsOutcomeWhenLabelHasNoFairOddEntry() {
        val fairOddByLabel = mapOf("Home" to FairOdd("Home", 2.00))
        val values = listOf(OddValue("Home", "2.10"), OddValue("Draw", "3.50"))

        val result = underTest.toBookmakerLine("TestBook", values, fairOddByLabel)

        assertThat(result.outcomes).hasSize(1)
        assertThat(result.outcomes.first().label).isEqualTo("Home")
    }

    @Test
    fun willReturn9MarketsWithCorrectNamesFromMultiBookmakerData() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        assertThat(result.markets).hasSize(9)
        assertThat(result.markets.map { it.betName }).containsExactly(
            "Match Winner",
            "Goals Over/Under",
            "Both Teams Score",
            "Double Chance",
            "First Half Winner",
            "Both Teams Score - First Half",
            "Both Teams To Score - Second Half",
            "Goals Over/Under First Half",
            "Corners Over Under"
        )
    }

    @Test
    fun willIncludeAllBookmakersInEachMarket() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        assertThat(result.markets[0].bookmakers).hasSize(3)
        assertThat(result.markets[1].bookmakers).hasSize(3)
        assertThat(result.markets[2].bookmakers).hasSize(3)
        assertThat(result.markets[3].bookmakers).hasSize(3)
        assertThat(result.markets[4].bookmakers).hasSize(3)
    }

    @Test
    fun willReturnDoubleChanceMarketWithAllThreeOutcomes() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        val market = result.markets.first { it.betName == "Double Chance" }
        val outcomeLabels = market.bookmakers.first().outcomes.map { it.label }

        assertThat(outcomeLabels).containsExactlyInAnyOrder("Home/Draw", "Home/Away", "Draw/Away")
    }

    @Test
    fun willReturnFirstHalfWinnerMarketWithAllThreeOutcomes() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        val market = result.markets.first { it.betName == "First Half Winner" }
        val outcomeLabels = market.bookmakers.first().outcomes.map { it.label }

        assertThat(outcomeLabels).containsExactlyInAnyOrder("Home", "Draw", "Away")
    }

    @Test
    fun willFlagValueBetWhenBookmakerOddExceedsFairOdd() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        val betaMatchWinner = result.markets[0].bookmakers.first { it.name == "BetaBook" }
        val awayOutcome = betaMatchWinner.outcomes.first { it.label == "Away" }

        assertThat(awayOutcome.isValue).isTrue()
    }

    @Test
    fun willNotFlagNonValueBet() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        val alphaMatchWinner = result.markets[0].bookmakers.first { it.name == "AlphaBook" }
        assertThat(alphaMatchWinner.outcomes.first { it.label == "Home" }.isValue).isFalse()
        assertThat(alphaMatchWinner.outcomes.first { it.label == "Away" }.isValue).isFalse()
    }

    @Test
    fun willReturnOnlyOver2point5AndUnder2point5ForGoalsMarket() {
        val result = underTest.extractValueBets(ClientOddsData.mockMultiBookmakerResponse)

        val goalsMarket = result.markets.first { it.betName == "Goals Over/Under" }
        val alphaOutcomes = goalsMarket.bookmakers.first { it.name == "AlphaBook" }.outcomes

        assertThat(alphaOutcomes).hasSize(2)
        assertThat(alphaOutcomes.map { it.label }).containsExactlyInAnyOrder("Over 2.5", "Under 2.5")
    }

    @Test
    fun willExcludeMarketWhenFewerThanTwoBookmakersOfferIt() {
        val data = listOf(
            FixtureOdds(bookmakers = listOf(
                Bookmaker(name = "BoA", bets = listOf(
                    Bet(name = "Match Winner", values = listOf(
                        OddValue("Home", "1.80"), OddValue("Draw", "3.50"), OddValue("Away", "4.50")
                    )),
                    Bet(name = "Goals Over/Under", values = listOf(
                        OddValue("Over 2.5", "1.70"), OddValue("Under 2.5", "2.10")
                    ))
                )),
                Bookmaker(name = "BoB", bets = listOf(
                    Bet(name = "Match Winner", values = listOf(
                        OddValue("Home", "1.85"), OddValue("Draw", "3.40"), OddValue("Away", "4.40")
                    ))
                    // no Goals Over/Under, no Both Teams Score
                ))
            ))
        )

        val result = underTest.extractValueBets(data)

        assertThat(result.markets).hasSize(1)
        assertThat(result.markets[0].betName).isEqualTo("Match Winner")
    }
}