package org.footballproject.datamanipulation

import org.footballproject.clientData.Bookmaker
import org.footballproject.clientData.FixtureOdds
import org.footballproject.clientData.OddValue
import org.footballproject.clientData.MatchResponse
import org.footballproject.errors.ApiFailedException
import org.footballproject.errors.ErrorMessage
import org.footballproject.model.Odd
import org.footballproject.model.OddFeeling
import org.footballproject.model.TeamRestStatus
import org.footballproject.response.Bet
import org.footballproject.response.BookmakerLine
import org.footballproject.response.FairOdd
import org.footballproject.response.LiveHomeAwayForm
import org.footballproject.response.SingleOdd
import org.footballproject.response.ValueBetMarket
import org.footballproject.response.ValueBetOutcome
import org.footballproject.response.ValueBetsResponse


import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Component
class DataManipulation {

    fun lastFiveResults(teamId: Int, matches: List<MatchResponse>): List<String> {
        return matches.map { match ->
            val homeGoals = match.goals?.home ?: 0
            val awayGoals = match.goals?.away ?: 0

            when {
                match.teams.home?.id == teamId && homeGoals > awayGoals -> "W"
                match.teams.home?.id == teamId && homeGoals < awayGoals -> "L"
                match.teams.away?.id == teamId && homeGoals > awayGoals -> "L"
                match.teams.away?.id == teamId && homeGoals < awayGoals -> "W"
                else -> "D"
            }
        }
    }

    fun buildLiveHomeAwayForm(
        homeTeamId: Int,
        awayTeamId: Int,
        matches: Map<Int, List<MatchResponse>>
    ): LiveHomeAwayForm {
        val homeForm = lastFiveResults(homeTeamId, matches[homeTeamId] ?: emptyList())
        val awayForm = lastFiveResults(awayTeamId, matches[awayTeamId] ?: emptyList())

        return LiveHomeAwayForm(
            homeForm = homeForm,
            awayForm = awayForm,
            isHomeHot = homeForm.size >= 3 && homeForm.takeLast(3).all { it == "W" },
            isHomeCold = homeForm.size >= 3 && homeForm.takeLast(3).all { it == "L" },
            isAwayHot = awayForm.size >= 3 && awayForm.takeLast(3).all { it == "W" },
            isAwayCold = awayForm.size >= 3 && awayForm.takeLast(3).all { it == "L" },
            isDisplayable = (homeForm.size >= 3 && awayForm.size >= 3)
        )
    }

    fun extractBets(data: List<FixtureOdds>): List<Bet> {
        val allBets = mutableListOf<Bet>()

        data
            .flatMap { it.bookmakers }
            .firstOrNull()
            ?.bets
            ?.forEach { bet ->
                val odds = bet.values.mapNotNull { value ->
                    val oddDouble = value.odd.toDoubleOrNull()
                    if (oddDouble != null) SingleOdd(label = value.value, odd = oddDouble) else null
                }

                if (odds.isNotEmpty()) {
                    allBets.add(
                        Bet(
                            betName = bet.name,
                            values = odds
                        )
                    )
                }
            }

        return allBets
    }

    fun daysBetween(date1: String?, date2: String?): Long? {
        if (date1.isNullOrBlank() || date1 == "Unknown Date") return null
        if (date2.isNullOrBlank() || date2 == "Unknown Date") return null

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val d1 = runCatching { ZonedDateTime.parse(date1, formatter) }.getOrNull()
        val d2 = runCatching { ZonedDateTime.parse(date2, formatter) }.getOrNull()

        if (d1 != null && d2 != null) {
            return ChronoUnit.DAYS.between(d1, d2)
        }

        return null
    }

    fun teamRestStatus(daysSinceLastMatch: Long): String {
        if (daysSinceLastMatch >= 5) return TeamRestStatus.GOOD_REST.status
        if (daysSinceLastMatch in 3..4) return TeamRestStatus.MODERATE_CONGESTION.status
        if (daysSinceLastMatch in 0..2) return TeamRestStatus.SEVERE_CONGESTION.status
        return TeamRestStatus.UNKNOWN_STATE.status
    }


    fun oddsFeeling(data: List<FixtureOdds>): Map<Odd, OddFeeling> {
        val bookmaker = data
            .flatMap { it.bookmakers }
            .firstOrNull()
            ?: return emptyMap()

        val matchWinner = bookmaker.bets.firstOrNull { it.name.contains("Match Winner", true) }
            ?: return emptyMap()

        val homeOdds = matchWinner.values.first { it.value.contains(Odd.HOME.value, true) }.odd.toDouble()
        val drawOdds = matchWinner.values.first { it.value.contains(Odd.DRAW.value, true) }.odd.toDouble()
        val awayOdds = matchWinner.values.first { it.value.contains(Odd.AWAY.value, true) }.odd.toDouble()

        val (homeProb, drawProb, awayProb) = calculateOddsProbabilities(homeOdds, drawOdds, awayOdds)

        return mapOf(
            Odd.HOME to if (homeProb >= 0.5) OddFeeling.STRONG else OddFeeling.WEAK,
            Odd.DRAW to if (drawProb >= 0.5) OddFeeling.STRONG else OddFeeling.WEAK,
            Odd.AWAY to if (awayProb >= 0.5) OddFeeling.STRONG else OddFeeling.WEAK
        )
    }


    fun extractValueBets(data: List<FixtureOdds>): ValueBetsResponse {
        val allBookmakers = data.flatMap { it.bookmakers }
        val markets = listOf(
            buildMarketWrapper(allBookmakers, MATCH_WINNER),
            buildMarketWrapper(
                allBookmakers,
                GOALS_OVER_UNDER,
                outcomeFilter = listOf(OVER_TWO_AND_A_HALF, UNDER_TWO_AND_A_HALF)
            ),
            buildMarketWrapper(allBookmakers, BOTH_TEAMS_SCORE),
            buildMarketWrapper(allBookmakers, DOUBLE_CHANCE),
            buildMarketWrapper(allBookmakers, FIRST_HALF_WINNER),
            buildMarketWrapper(allBookmakers, BTTS_FIRST_HALF),
            buildMarketWrapper(allBookmakers, BTTS_SECOND_HALF),
            buildMarketWrapper(
                allBookmakers,
                GOALS_OVER_UNDER_FIRST_HALF,
                outcomeFilter = listOf(OVER_ZERO_POINT_FIVE, UNDER_ZERO_POINT_FIVE)
            ),
            buildMarketWrapper(
                allBookmakers,
                CORNERS_OVER_UNDER,
                outcomeFilter = listOf(OVER_NINE_POINT_FIVE, UNDER_NINE_POINT_FIVE)
            )
        ).filterNotNull()
        return ValueBetsResponse(markets)
    }

    private fun buildMarketWrapper(
        allBookmakers: List<Bookmaker>,
        betName: String,
        outcomeFilter: List<String>? = emptyList()
    ) =
        runCatching { buildMarket(allBookmakers, betName, outcomeFilter) }
            .getOrNull()

    private fun buildMarket(
        bookmakers: List<Bookmaker>,
        betName: String,
        outcomeFilter: List<String>? = emptyList()
    ): ValueBetMarket? {
        val qualifiedBookmakers = bookmakers.mapNotNull { bookmaker ->
            val bet = bookmaker.bets.firstOrNull { it.name.contains(betName, ignoreCase = true) }
                ?: return@mapNotNull null
            val relevantValues = filterAndValidate(bet.values, outcomeFilter)
            if (relevantValues.size < MIN_OUTCOMES) return@mapNotNull null
            bookmaker to relevantValues
        }

        if (qualifiedBookmakers.size < MIN_BOOKMAKERS) return null

        val outcomeLabels = qualifiedBookmakers.first().second.map { it.value }
        val bookmakerProbabilities = qualifiedBookmakers.map { (_, values) -> normalizeImplied(values) }
        val fairOdds = fairOddsFrom(outcomeLabels, bookmakerProbabilities)
        val fairOddByLabel = fairOdds.associateBy { it.label }

        val bookmakerLines = qualifiedBookmakers.map { (bookmaker, values) ->
            toBookmakerLine(bookmaker.name, values, fairOddByLabel)
        }

        return ValueBetMarket(betName = betName, fairOdds = fairOdds, bookmakers = bookmakerLines)
    }

    fun filterAndValidate(values: List<OddValue>, outcomeFilter: List<String>?): List<OddValue> {
        val filtered = if (outcomeFilter?.isNotEmpty() == true) {
            values.filter { oddValue ->
                outcomeFilter.any { filterLabel -> oddValue.value.equals(filterLabel, ignoreCase = true) }
            }
        } else values
        return filtered.filter { it.odd.toDoubleOrNull() != null }
    }

    fun normalizeImplied(values: List<OddValue>): List<Pair<String, Double>> {
        val impliedProbabilities = values.map { 1.0 / it.odd.toDouble() }
        val total = impliedProbabilities.sum()
        return values.zip(impliedProbabilities.map { it / total })
            .map { (oddValue, normalizedProbability) -> oddValue.value to normalizedProbability }
    }

    fun fairOddsFrom(
        outcomeLabels: List<String>,
        bookmakerProbabilities: List<List<Pair<String, Double>>>
    ): List<FairOdd> {
        return outcomeLabels.map { label ->
            val averageProbability = bookmakerProbabilities
                .mapNotNull { probabilities -> probabilities.firstOrNull { it.first == label }?.second }
                .average()
            FairOdd(label = label, odd = 1.0 / averageProbability)
        }
    }

    fun toBookmakerLine(
        name: String,
        values: List<OddValue>,
        fairOddByLabel: Map<String, FairOdd>
    ): BookmakerLine {
        val outcomes = values.mapNotNull { oddValue ->
            val fairOdd = fairOddByLabel[oddValue.value]?.odd ?: return@mapNotNull null
            val bookmakerOdd = oddValue.odd.toDouble()
            ValueBetOutcome(label = oddValue.value, odd = bookmakerOdd, isValue = bookmakerOdd > fairOdd)
        }
        return BookmakerLine(name = name, outcomes = outcomes)
    }

    private fun calculateOddsProbabilities(
        homeOdds: Double,
        drawOdds: Double,
        awayOdds: Double
    ): Triple<Double, Double, Double> {
        if (!(homeOdds > 0) || !(drawOdds > 0) || !(awayOdds > 0))
            throw ApiFailedException(ErrorMessage.CALCULATION_FAILED)

        val impliedHome = 1.0 / homeOdds
        val impliedDraw = 1.0 / drawOdds
        val impliedAway = 1.0 / awayOdds

        val total = impliedHome + impliedDraw + impliedAway

        val normalizedHome = impliedHome / total
        val normalizedDraw = impliedDraw / total
        val normalizedAway = impliedAway / total

        return Triple(normalizedHome, normalizedDraw, normalizedAway)
    }

    companion object {
        private const val MATCH_WINNER = "Match Winner"
        private const val GOALS_OVER_UNDER = "Goals Over/Under"
        private const val BOTH_TEAMS_SCORE = "Both Teams Score"
        private const val DOUBLE_CHANCE = "Double Chance"
        private const val FIRST_HALF_WINNER = "First Half Winner"
        private const val BTTS_FIRST_HALF = "Both Teams Score - First Half"
        private const val BTTS_SECOND_HALF = "Both Teams To Score - Second Half"
        private const val GOALS_OVER_UNDER_FIRST_HALF = "Goals Over/Under First Half"
        private const val CORNERS_OVER_UNDER = "Corners Over Under"
        private const val OVER_TWO_AND_A_HALF = "Over 2.5"
        private const val UNDER_TWO_AND_A_HALF = "Under 2.5"
        private const val OVER_ZERO_POINT_FIVE = "Over 0.5"
        private const val UNDER_ZERO_POINT_FIVE = "Under 0.5"
        private const val OVER_NINE_POINT_FIVE = "Over 9.5"
        private const val UNDER_NINE_POINT_FIVE = "Under 9.5"
        private const val MIN_BOOKMAKERS = 2
        private const val MIN_OUTCOMES = 2
    }

}