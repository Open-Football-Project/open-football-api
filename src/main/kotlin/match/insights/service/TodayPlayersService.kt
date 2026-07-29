package match.insights.service

import match.insights.apidata.MatchesData
import match.insights.apidata.OddsData
import match.insights.apidata.PlayersData
import match.insights.apidata.TeamData
import match.insights.clientData.Bookmaker
import match.insights.clientData.MatchResponse
import match.insights.clientData.SquadPlayer
import match.insights.datamanipulation.TodayPlayersManipulation
import match.insights.model.MatchStatus
import match.insights.model.PlayerPosition
import match.insights.model.PlayerScore
import match.insights.model.TodayPlayersWatchlist
import match.insights.props.TodayPlayersProps
import match.insights.repository.TodayPlayersRepository
import match.insights.response.FixtureTodayPlayers
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Service
class TodayPlayersService(
    private val matchesData: MatchesData,
    private val teamData: TeamData,
    private val oddsData: OddsData,
    private val playersData: PlayersData,
    private val manipulation: TodayPlayersManipulation,
    private val todayPlayersRepository: TodayPlayersRepository,
    private val props: TodayPlayersProps,
    private val clock: Clock = Clock.systemUTC()
) {

    private val log = LoggerFactory.getLogger(TodayPlayersService::class.java)

    fun captureTodayPlayers() {
        val tomorrow = LocalDate.now(clock).plusDays(1).toString()
        matchesData.matchesOfTheDay(tomorrow)
            .filter { it.league.id in props.trackedLeagueIds && isCapturable(it) }
            .forEach { fixture ->
                saveNewFixture(fixture)
            }
    }


    fun buildWatchlist(fixture: MatchResponse): TodayPlayersWatchlist {
        val homeTeamId = fixture.teams.home?.id ?: -1
        val awayTeamId = fixture.teams.away?.id ?: -1
        val homeSquad = teamData.currentTeamSquad(homeTeamId)
        val awaySquad = teamData.currentTeamSquad(awayTeamId)

        val bookmakers = getFixtureOdds(fixtureId = fixture.fixture.id)

        logUnmatchedOddsNames(bookmakers, homeSquad + awaySquad)

        return TodayPlayersWatchlist(
            fixtureId = fixture.fixture.id,
            leagueId = fixture.league.id,
            leagueName = fixture.league.name,
            homeTeamName = fixture.teams.home?.name ?: "Unknown Team",
            awayTeamName = fixture.teams.away?.name ?: "Unknown Team",
            home = topPlayersPerPosition(homeSquad, bookmakers),
            away = topPlayersPerPosition(awaySquad, bookmakers)
        )
    }

    fun trackedLeagueIds(): List<Int> = props.trackedLeagueIds

    fun availableFixtureIds(): List<Int> = todayPlayersRepository.getAllFixtures().map { it.fixtureId }

    fun allFixtures(): List<FixtureTodayPlayers> =
        todayPlayersRepository.getAllFixtures().map { FixtureTodayPlayers.fromWatchlist(it) }

    private fun isCapturable(fixture: MatchResponse): Boolean =
        MatchStatus.entries.find { it.code == fixture.fixture.status?.short }?.isVoided() != true

    private fun topPlayersPerPosition(
        squad: List<SquadPlayer>,
        bookmakers: List<Bookmaker>
    ): Map<PlayerPosition, List<PlayerScore>> {
        val scoresByPosition = squad.mapNotNull { scoreSquadPlayer(it, bookmakers) }
            .groupBy({ it.first }, { it.second })

        return PlayerPosition.entries.associateWith { position ->
            scoresByPosition[position].orEmpty().sortedByDescending { it.score }.take(MAX_PLAYERS_PER_POSITION)
        }
    }


    private fun scoreSquadPlayer(player: SquadPlayer, bookmakers: List<Bookmaker>): Pair<PlayerPosition, PlayerScore>? {
        val position = player.position?.let { PlayerPosition.fromApiPosition(it) }
        val playerId = player.id

        if (position == null || playerId == null) {
            return null
        }

        val oddsScore = manipulation.scorePlayerWithOdds(player, position, bookmakers)

        return buildPositionScorePair(playerId, player, position, oddsScore)
    }

    private fun logUnmatchedOddsNames(bookmakers: List<Bookmaker>, squad: List<SquadPlayer>) {
        val eligibleMarketNames =
            props.attackerMarkets + props.midfieldMarkets + props.goalkeeperMarkets + props.defenderMarkets

        bookmakers.flatMap { it.bets }
            .filter { TodayPlayersProps.normalize(it.name) in eligibleMarketNames }
            .flatMap { it.values }
            .map { it.value }
            .distinct()
            .filterNot { props.isNonPlayerOddsValue(it) }
            .forEach { oddsPlayerName ->
                if (manipulation.findPlayerWithOddsName(oddsPlayerName, squad) == null) {
                    log.warn("Unmatched odds player name: $oddsPlayerName")
                }
            }
    }


    private fun buildPositionScorePair(
        playerId: Int,
        player: SquadPlayer,
        position: PlayerPosition,
        playerScore: PlayerScore?
    ): Pair<PlayerPosition, PlayerScore>? =
        playerScore?.let { position to it } ?: statsScoreFallback(playerId, position, player)

    private fun statsScoreFallback(playerId: Int, position: PlayerPosition, player: SquadPlayer) = runCatching {
        playersData.playerInfo(playerId)
            ?.let { info -> position to manipulation.scorePlayerWithStats(player, info) }
    }.onFailure {
        log.warn("Failed to fetch fallback player info for player $playerId", it)
    }.getOrElse { null }

    private fun saveNewFixture(fixture: MatchResponse) = runCatching {
        if (!todayPlayersRepository.exists(fixture.fixture.id))
            todayPlayersRepository.save(buildWatchlist(fixture))

    }.onFailure { log.error("Failed to build today-players watchlist for fixture ${fixture.fixture.id}", it) }

    private fun getFixtureOdds(fixtureId: Int): List<Bookmaker> = runCatching {
        oddsData.fetchAllOdds(fixtureId).firstOrNull()?.bookmakers ?: emptyList()
    }.onFailure {
        log.warn("Failed to fetch odds for fixture ${fixtureId}, treating as no odds coverage", it)
    }.getOrElse { emptyList() }

    companion object {
        private const val MAX_PLAYERS_PER_POSITION = 3
    }
}
