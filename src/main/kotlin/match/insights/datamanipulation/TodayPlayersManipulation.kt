package match.insights.datamanipulation

import match.insights.clientData.Bookmaker
import match.insights.clientData.PlayerInfoResponse
import match.insights.clientData.SquadPlayer
import match.insights.model.PlayerPosition
import match.insights.model.PlayerScore
import match.insights.model.ScoreReason
import match.insights.model.ScoringSignal
import match.insights.props.TodayPlayersProps
import org.springframework.stereotype.Component
import java.text.Normalizer

@Component
class TodayPlayersManipulation(private val props: TodayPlayersProps) {

    fun findPlayerWithOddsName(oddsPlayerName: String, squad: List<SquadPlayer>): SquadPlayer? {
        val oddsTokens = normalizeToTokens(oddsPlayerName.replace(thresholdSuffix, ""))
        if (oddsTokens.isEmpty()) return null
        return squad.firstOrNull { matchesName(oddsTokens, normalizeToTokens(it.name)) }
    }

    fun scorePlayerWithOdds(
        player: SquadPlayer,
        position: PlayerPosition,
        bookmakers: List<Bookmaker>
    ): PlayerScore? {
        val (score, markets) = oddsImpliedScore(player, position, bookmakers) ?: return null
        return PlayerScore(player, score, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(markets))
    }

    fun scorePlayerWithStats(player: SquadPlayer, playerInfo: PlayerInfoResponse): PlayerScore {
        val (score, reason) = seasonStatScore(playerInfo)
        return PlayerScore(player, score, ScoringSignal.SEASON_STAT, reason)
    }

    private fun oddsImpliedScore(
        player: SquadPlayer,
        position: PlayerPosition,
        bookmakers: List<Bookmaker>
    ): Pair<Double, List<String>>? {
        val matchedQuotes = bookmakers
            .flatMap { it.bets }
            .filter { isEligibleMarket(it.name, position) }
            .flatMap { bet -> bet.values.filter { findPlayerWithOddsName(it.value, listOf(player)) != null }.map { bet.name to it } }

        if (matchedQuotes.isEmpty()) return null

        val impliedProbabilities = matchedQuotes.mapNotNull { (_, value) -> value.odd.toDoubleOrNull() }.map { 1 / it }
        val markets = matchedQuotes.map { it.first }.distinct()

        return impliedProbabilities.average() to markets
    }

    private fun seasonStatScore(info: PlayerInfoResponse): Pair<Double, ScoreReason.SeasonForm> {
        val appearances = info.statistics.sumOf { it.games.appearences ?: 0 }
        val goals = info.statistics.sumOf { it.goals.total ?: 0 }
        val assists = info.statistics.sumOf { it.goals.assists ?: 0 }
        val weightedRating = info.statistics.sumOf { stat ->
            val rating = stat.games.rating?.toDoubleOrNull() ?: return@sumOf 0.0
            rating * (stat.games.appearences ?: 0)
        }

        val averageRating = if (appearances > 0) weightedRating / appearances else 0.0
        val score = averageRating + 0.1 * (goals + assists)

        return score to ScoreReason.SeasonForm(appearances, goals, assists, averageRating)
    }

    private fun isEligibleMarket(marketName: String, position: PlayerPosition): Boolean =
        when (position) {
            PlayerPosition.ATTACKER -> props.attackerMarkets
            PlayerPosition.MIDFIELDER -> props.midfieldMarkets
            PlayerPosition.GOALKEEPER -> props.goalkeeperMarkets
            PlayerPosition.DEFENDER -> props.defenderMarkets
        }.contains(TodayPlayersProps.normalize(marketName))


    private fun matchesName(oddsTokens: List<String>, squadTokens: List<String>): Boolean {
        if (squadTokens.isEmpty() || oddsTokens.last() != squadTokens.last()) return false
        val squadFirst = squadTokens.first()
        val oddsFirst = oddsTokens.first()
        return if (squadFirst.length == 1) oddsFirst.startsWith(squadFirst) else squadFirst == oddsFirst
    }

    private fun normalizeToTokens(name: String): List<String> =
        Normalizer.normalize(name, Normalizer.Form.NFD)
            .replace(diacritics, "")
            .lowercase()
            .replace(".", "")
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

    companion object {
        private val thresholdSuffix = Regex("\\s*-\\s*\\d+\\+\\s*$")
        private val diacritics = Regex("\\p{M}")
    }
}
