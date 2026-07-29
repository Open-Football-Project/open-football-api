package match.insights.datamanipulation

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import match.insights.clientData.ApiResponse
import match.insights.clientData.Bookmaker
import match.insights.clientData.FixtureOdds
import match.insights.clientData.PlayerInfoResponse
import match.insights.clientData.SquadPlayer
import match.insights.clientData.SquadResponse
import match.insights.model.PlayerPosition
import match.insights.model.ScoreReason
import match.insights.model.ScoringSignal
import match.insights.props.TodayPlayersProps
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import java.io.File

class TodayPlayersManipulationTest {
    private val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private fun loadJsonResource(filename: String): String {
        val resource = this::class.java.classLoader.getResource(filename)
            ?: throw IllegalArgumentException("Resource $filename not found")
        return File(resource.toURI()).readText()
    }

    private val bookmakers: List<Bookmaker> by lazy {
        val response: ApiResponse<List<FixtureOdds>> = mapper.readValue(loadJsonResource("odds_with_player_props.json"))
        response.response.first().bookmakers
    }

    private val argentinaSquad: List<SquadPlayer> by lazy {
        val response: ApiResponse<List<SquadResponse>> = mapper.readValue(loadJsonResource("squad.json"))
        response.response.first().players
    }

    private val messi: SquadPlayer by lazy { argentinaSquad.first { it.name == "L. Messi" } }

    private val lautaroMartinezInfo: PlayerInfoResponse by lazy {
        val response: ApiResponse<List<PlayerInfoResponse>> = mapper.readValue(loadJsonResource("player_info.json"))
        response.response.first()
    }

    private fun manipulation(
        attackerMarkets: Set<String> = setOf(
            TodayPlayersProps.normalize("Anytime Goal Scorer"),
            TodayPlayersProps.normalize("Player Assists")
        )
    ) = TodayPlayersManipulation(TodayPlayersProps(attackerMarkets = attackerMarkets))

    @Test
    fun `matches an odds name to a squad name that only differs by accent`() {
        val match = manipulation().findPlayerWithOddsName("Lautaro Martinez", argentinaSquad)

        assertThat(match?.name).isEqualTo("Lautaro Martínez")
    }

    @Test
    fun `matches an odds full first name to a squad entry abbreviated to an initial`() {
        val messiMatch = manipulation().findPlayerWithOddsName("Lionel Messi", argentinaSquad)
        val alvarez = manipulation().findPlayerWithOddsName("Julian Alvarez", argentinaSquad)

        assertThat(messiMatch?.name).isEqualTo("L. Messi")
        assertThat(alvarez?.name).isEqualTo("J. Álvarez")
    }

    @Test
    fun `strips a threshold suffix before matching`() {
        val match = manipulation().findPlayerWithOddsName("Emiliano Martinez - 1+", argentinaSquad)

        assertThat(match?.name).isEqualTo("E. Martínez")
    }

    @Test
    fun `returns null when no squad player matches`() {
        val match = manipulation().findPlayerWithOddsName("Haissem Hassan", argentinaSquad)

        assertThat(match).isNull()
    }

    @Test
    fun `does not confuse two squad players who share a surname and initial`() {
        val martinez = manipulation().findPlayerWithOddsName("Emiliano Martinez", argentinaSquad)

        assertThat(martinez?.name).isEqualTo("E. Martínez")
    }

    @Test
    fun `scores a player from odds-implied probability, averaged across every eligible market quote`() {
        val score = manipulation().scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers)

        assertThat(score?.signal).isEqualTo(ScoringSignal.ODDS_IMPLIED)
        assertThat(score?.score).isCloseTo(0.4812, within(0.001))
        assertThat((score?.reason as? ScoreReason.MarketOdds)?.markets)
            .containsExactlyInAnyOrder("Anytime Goal Scorer", "Player Assists")
    }

    @Test
    fun `ignores quotes from markets outside the position's allowlist`() {
        val onlyAssists = manipulation(attackerMarkets = setOf(TodayPlayersProps.normalize("Player Assists")))

        val score = onlyAssists.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers)

        assertThat(score?.score).isCloseTo(1 / 2.75, within(0.0001))
        assertThat((score?.reason as? ScoreReason.MarketOdds)?.markets).containsExactly("Player Assists")
    }

    @Test
    fun `returns null from odds scoring when no odds quotes match this player`() {
        val score = manipulation().scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers = emptyList())

        assertThat(score).isNull()
    }

    @Test
    fun `scores a player from season-stats`() {
        val score = manipulation().scorePlayerWithStats(messi, lautaroMartinezInfo)

        assertThat(score.signal).isEqualTo(ScoringSignal.SEASON_STAT)
        assertThat(score.score).isCloseTo(7.693, within(0.001))
        val reason = score.reason as? ScoreReason.SeasonForm
        assertThat(reason?.appearances).isEqualTo(20)
        assertThat(reason?.goals).isEqualTo(6)
        assertThat(reason?.assists).isEqualTo(2)
        assertThat(reason?.rating).isCloseTo(6.893, within(0.001))
    }
}
