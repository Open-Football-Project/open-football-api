package match.insights.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import match.insights.apidata.MatchesData
import match.insights.apidata.OddsData
import match.insights.apidata.PlayersData
import match.insights.apidata.TeamData
import match.insights.clientData.Bet
import match.insights.clientData.Bookmaker
import match.insights.clientData.Fixture
import match.insights.clientData.FixtureOdds
import match.insights.clientData.League
import match.insights.clientData.MatchResponse
import match.insights.clientData.MatchStatus
import match.insights.clientData.OddValue
import match.insights.clientData.PlayerInfo
import match.insights.clientData.PlayerInfoResponse
import match.insights.clientData.SquadPlayer
import match.insights.clientData.Team
import match.insights.clientData.Teams
import match.insights.datamanipulation.TodayPlayersManipulation
import match.insights.model.PlayerPosition
import match.insights.model.PlayerScore
import match.insights.model.ScoreReason
import match.insights.model.ScoringSignal
import match.insights.model.TodayPlayersWatchlist
import match.insights.props.TodayPlayersProps
import match.insights.repository.TodayPlayersRepository
import match.insights.response.FixtureTodayPlayers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import match.insights.model.MatchStatus as MatchStatusEnum

class TodayPlayersServiceTest {

    private val matchesData: MatchesData = mockk()
    private val teamData: TeamData = mockk()
    private val oddsData: OddsData = mockk()
    private val playersData: PlayersData = mockk()
    private val manipulation: TodayPlayersManipulation = mockk()
    private val todayPlayersRepository: TodayPlayersRepository = mockk()

    private val trackedLeagueId = 1
    private val props = TodayPlayersProps(
        trackedLeagueIds = listOf(trackedLeagueId),
        attackerMarkets = setOf(TodayPlayersProps.normalize("Anytime Goal Scorer")),
        nonPlayerOddsValues = setOf("Yes", "No", "No Goalscorer")
    )
    private val fixedClock = Clock.fixed(Instant.parse("2026-07-09T00:00:00Z"), ZoneOffset.UTC)

    private val underTest = TodayPlayersService(
        matchesData, teamData, oddsData, playersData, manipulation, todayPlayersRepository, props, fixedClock
    )

    private val fixture = MatchResponse(
        fixture = Fixture(id = 1576804, status = MatchStatus(short = MatchStatusEnum.NOT_STARTED.code)),
        league = League(id = trackedLeagueId, season = 2026),
        teams = Teams(home = Team(id = 26, name = "Argentina"), away = Team(id = 32, name = "Egypt"))
    )

    private val messi = SquadPlayer(id = 154, name = "L. Messi", age = 38, number = 10, position = "Attacker", photo = null)
    private val lautaro = SquadPlayer(id = 217, name = "Lautaro Martínez", age = 28, number = 22, position = "Attacker", photo = null)
    private val unrecognizedPositionPlayer =
        SquadPlayer(id = 999, name = "Unknown Role", age = 20, number = 99, position = "Wing-back", photo = null)
    private val noIdPlayer = SquadPlayer(id = null, name = "No Id Guy", age = 21, number = 77, position = "Midfielder", photo = null)
    private val salah = SquadPlayer(id = 301, name = "M. Salah", age = 33, number = 11, position = "Attacker", photo = null)

    private val bookmakers = listOf(
        Bookmaker(
            name = "Bet365",
            bets = listOf(Bet(name = "Anytime Goal Scorer", values = listOf(OddValue(value = "Lionel Messi", odd = "1.67"))))
        )
    )

    private val messiScore = PlayerScore(messi, 0.6, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(listOf("Anytime Goal Scorer")))
    private val lautaroScore = PlayerScore(lautaro, 0.3, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(listOf("Anytime Goal Scorer")))
    private val salahScore = PlayerScore(salah, 0.5, ScoringSignal.SEASON_STAT, ScoreReason.SeasonForm(10, 5, 2, 7.0))

    private val samplePlayerInfo = PlayerInfoResponse(
        player = PlayerInfo(
            id = 301, name = "M. Salah", firstname = null, lastname = null, age = null,
            birth = null, nationality = null, height = null, weight = null, injured = false, photo = null
        ),
        statistics = emptyList()
    )

    private fun watchlistMap(vararg scores: Pair<PlayerPosition, List<PlayerScore>>): Map<PlayerPosition, List<PlayerScore>> {
        val provided = scores.toMap()
        return PlayerPosition.entries.associateWith { provided[it] ?: emptyList() }
    }

    @Test
    fun `builds a watchlist keyed by fixture and team names from the match`() {
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns listOf(salah)
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers) } returns messiScore
        every { manipulation.scorePlayerWithOdds(salah, PlayerPosition.ATTACKER, bookmakers) } returns null
        every { playersData.playerInfo(301) } returns null
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns messi

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.fixtureId).isEqualTo(1576804)
        assertThat(result.homeTeamName).isEqualTo("Argentina")
        assertThat(result.awayTeamName).isEqualTo("Egypt")
    }

    @Test
    fun `keeps at most the 3 highest-scored players per position, ordered by score descending`() {
        val alvarez = SquadPlayer(id = 6009, name = "J. Álvarez", age = 25, number = 9, position = "Attacker", photo = null)
        val depaul = SquadPlayer(id = 2472, name = "R. De Paul", age = 27, number = 7, position = "Attacker", photo = null)
        val alvarezScore = PlayerScore(alvarez, 0.5, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(listOf("Anytime Goal Scorer")))
        val depaulScore = PlayerScore(depaul, 0.2, ScoringSignal.ODDS_IMPLIED, ScoreReason.MarketOdds(listOf("Anytime Goal Scorer")))

        every { teamData.currentTeamSquad(26) } returns listOf(messi, lautaro, alvarez, depaul)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers) } returns messiScore
        every { manipulation.scorePlayerWithOdds(lautaro, PlayerPosition.ATTACKER, bookmakers) } returns lautaroScore
        every { manipulation.scorePlayerWithOdds(alvarez, PlayerPosition.ATTACKER, bookmakers) } returns alvarezScore
        every { manipulation.scorePlayerWithOdds(depaul, PlayerPosition.ATTACKER, bookmakers) } returns depaulScore
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns messi

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home[PlayerPosition.ATTACKER]).containsExactly(messiScore, alvarezScore, lautaroScore)
    }

    @Test
    fun `always returns all 4 positions, with an empty list for positions where nobody scored`() {
        every { teamData.currentTeamSquad(26) } returns emptyList()
        every { teamData.currentTeamSquad(32) } returns listOf(salah)
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(salah, PlayerPosition.ATTACKER, bookmakers) } returns null
        every { playersData.playerInfo(301) } returns samplePlayerInfo
        every { manipulation.scorePlayerWithStats(salah, samplePlayerInfo) } returns salahScore
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns null

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home).isEqualTo(watchlistMap())
        assertThat(result.away).isEqualTo(watchlistMap(PlayerPosition.ATTACKER to listOf(salahScore)))
    }

    @Test
    fun `skips a squad player whose position string is not recognized, without fetching their player info`() {
        every { teamData.currentTeamSquad(26) } returns listOf(unrecognizedPositionPlayer)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns null

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home).isEqualTo(watchlistMap())
        verify(exactly = 0) { playersData.playerInfo(999) }
        verify(exactly = 0) { manipulation.scorePlayerWithOdds(unrecognizedPositionPlayer, any(), any()) }
    }

    @Test
    fun `skips a squad player with no player id, without fetching their player info`() {
        every { teamData.currentTeamSquad(26) } returns listOf(noIdPlayer)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns null

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home).isEqualTo(watchlistMap())
        verify(exactly = 0) { manipulation.scorePlayerWithOdds(noIdPlayer, any(), any()) }
    }

    @Test
    fun `skips a player whose player-info fetch fails, without aborting the rest of the squad`() {
        every { teamData.currentTeamSquad(26) } returns listOf(messi, lautaro)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers) } returns null
        every { playersData.playerInfo(154) } throws RuntimeException("upstream 503")
        every { manipulation.scorePlayerWithOdds(lautaro, PlayerPosition.ATTACKER, bookmakers) } returns lautaroScore
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns messi

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home[PlayerPosition.ATTACKER]).containsExactly(lautaroScore)
        verify(exactly = 0) { manipulation.scorePlayerWithStats(messi, any()) }
    }

    @Test
    fun `treats a fixture with no odds coverage at all as an empty bookmaker list`() {
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns emptyList()
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, emptyList()) } returns null
        every { playersData.playerInfo(154) } returns samplePlayerInfo
        every { manipulation.scorePlayerWithStats(messi, samplePlayerInfo) } returns messiScore

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home).isEqualTo(watchlistMap(PlayerPosition.ATTACKER to listOf(messiScore)))
    }

    @Test
    fun `treats a fixture whose odds fetch fails as having no odds coverage, without aborting the fixture`() {
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } throws RuntimeException("upstream 503")
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, emptyList()) } returns null
        every { playersData.playerInfo(154) } returns samplePlayerInfo
        every { manipulation.scorePlayerWithStats(messi, samplePlayerInfo) } returns messiScore

        val result = underTest.buildWatchlist(fixture)

        assertThat(result.home).isEqualTo(watchlistMap(PlayerPosition.ATTACKER to listOf(messiScore)))
    }

    @Test
    fun `checks unmatched-name resolution only for odds values under an allowlisted market, ignoring unrelated markets leaking from the provider`() {
        val noisyBookmakers = listOf(
            Bookmaker(
                name = "Bet365",
                bets = listOf(
                    Bet(name = "Anytime Goal Scorer", values = listOf(OddValue(value = "Random Guy", odd = "2.0"))),
                    Bet(name = "Player Points", values = listOf(OddValue(value = "Some NBA Player", odd = "1.5")))
                )
            )
        )
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(noisyBookmakers))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, noisyBookmakers) } returns messiScore
        every { manipulation.findPlayerWithOddsName("Random Guy", listOf(messi)) } returns null

        underTest.buildWatchlist(fixture)

        verify { manipulation.findPlayerWithOddsName("Random Guy", listOf(messi)) }
        verify(exactly = 0) { manipulation.findPlayerWithOddsName("Some NBA Player", any()) }
    }

    @Test
    fun `does not attempt to match or warn about known non-player odds values like Yes, No, No Goalscorer, or an Over-Under line`() {
        val bookmakersWithSentinels = listOf(
            Bookmaker(
                name = "Bet365",
                bets = listOf(
                    Bet(
                        name = "Anytime Goal Scorer",
                        values = listOf(
                            OddValue(value = "Random Guy", odd = "2.0"),
                            OddValue(value = "No Goalscorer", odd = "3.0"),
                            OddValue(value = "Yes", odd = "1.2"),
                            OddValue(value = "No", odd = "1.8"),
                            OddValue(value = "Over 9.5", odd = "1.9")
                        )
                    )
                )
            )
        )
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakersWithSentinels))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakersWithSentinels) } returns messiScore
        every { manipulation.findPlayerWithOddsName("Random Guy", listOf(messi)) } returns null

        underTest.buildWatchlist(fixture)

        verify { manipulation.findPlayerWithOddsName("Random Guy", listOf(messi)) }
        verify(exactly = 0) { manipulation.findPlayerWithOddsName("No Goalscorer", any()) }
        verify(exactly = 0) { manipulation.findPlayerWithOddsName("Yes", any()) }
        verify(exactly = 0) { manipulation.findPlayerWithOddsName("No", any()) }
        verify(exactly = 0) { manipulation.findPlayerWithOddsName("Over 9.5", any()) }
    }

    @Test
    fun `captures and saves a watchlist for a tracked not-started fixture not already cached`() {
        val expected = TodayPlayersWatchlist(
            1576804, trackedLeagueId, "Unknown League", "Argentina", "Egypt",
            watchlistMap(PlayerPosition.ATTACKER to listOf(messiScore)), watchlistMap()
        )
        every { matchesData.matchesOfTheDay("2026-07-10") } returns listOf(fixture)
        every { todayPlayersRepository.exists(1576804) } returns false
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers) } returns messiScore
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns messi
        every { todayPlayersRepository.save(expected) } just Runs

        underTest.captureTodayPlayers()

        verify { todayPlayersRepository.save(expected) }
    }

    @Test
    fun `skips fixtures outside the tracked leagues`() {
        val untracked = fixture.copy(league = fixture.league.copy(id = 9999))
        every { matchesData.matchesOfTheDay("2026-07-10") } returns listOf(untracked)

        underTest.captureTodayPlayers()

        verify(exactly = 0) { teamData.currentTeamSquad(any()) }
        verify(exactly = 0) { todayPlayersRepository.save(any()) }
    }

    @Test
    fun `captures a fixture that has already kicked off, since the underlying data doesn't change after kickoff`() {
        val alreadyLive = fixture.copy(fixture = fixture.fixture.copy(status = MatchStatus(short = MatchStatusEnum.FIRST_HALF.code)))
        val expected = TodayPlayersWatchlist(
            1576804, trackedLeagueId, "Unknown League", "Argentina", "Egypt",
            watchlistMap(PlayerPosition.ATTACKER to listOf(messiScore)), watchlistMap()
        )
        every { matchesData.matchesOfTheDay("2026-07-10") } returns listOf(alreadyLive)
        every { todayPlayersRepository.exists(1576804) } returns false
        every { teamData.currentTeamSquad(26) } returns listOf(messi)
        every { teamData.currentTeamSquad(32) } returns emptyList()
        every { oddsData.fetchAllOdds(1576804) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(messi, PlayerPosition.ATTACKER, bookmakers) } returns messiScore
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns messi
        every { todayPlayersRepository.save(expected) } just Runs

        underTest.captureTodayPlayers()

        verify { todayPlayersRepository.save(expected) }
    }

    @Test
    fun `skips fixtures with a voided status (cancelled, postponed, abandoned, walkover, awarded)`() {
        val voidedStatuses = listOf(
            MatchStatusEnum.CANCELLED,
            MatchStatusEnum.POSTPONED,
            MatchStatusEnum.ABANDONED,
            MatchStatusEnum.WALKOVER,
            MatchStatusEnum.AWARDED
        )
        val voidedFixtures = voidedStatuses.map {
            fixture.copy(fixture = fixture.fixture.copy(status = MatchStatus(short = it.code)))
        }
        every { matchesData.matchesOfTheDay("2026-07-10") } returns voidedFixtures

        underTest.captureTodayPlayers()

        verify(exactly = 0) { todayPlayersRepository.exists(any()) }
        verify(exactly = 0) { teamData.currentTeamSquad(any()) }
    }

    @Test
    fun `skips a fixture that already has a cached watchlist, without rebuilding it`() {
        every { matchesData.matchesOfTheDay("2026-07-10") } returns listOf(fixture)
        every { todayPlayersRepository.exists(1576804) } returns true

        underTest.captureTodayPlayers()

        verify(exactly = 0) { teamData.currentTeamSquad(any()) }
        verify(exactly = 0) { todayPlayersRepository.save(any()) }
    }

    @Test
    fun `does not stop processing other fixtures when one fixture fails`() {
        val healthyFixture = fixture.copy(
            fixture = fixture.fixture.copy(id = 1576805),
            teams = Teams(home = Team(id = 999, name = "Home2"), away = Team(id = 998, name = "Away2"))
        )
        val expectedHealthy = TodayPlayersWatchlist(
            1576805, trackedLeagueId, "Unknown League", "Home2", "Away2",
            watchlistMap(PlayerPosition.ATTACKER to listOf(salahScore)), watchlistMap()
        )

        every { matchesData.matchesOfTheDay("2026-07-10") } returns listOf(fixture, healthyFixture)
        every { todayPlayersRepository.exists(1576804) } returns false
        every { todayPlayersRepository.exists(1576805) } returns false
        every { teamData.currentTeamSquad(26) } throws RuntimeException("API down")
        every { teamData.currentTeamSquad(999) } returns listOf(salah)
        every { teamData.currentTeamSquad(998) } returns emptyList()
        every { oddsData.fetchAllOdds(1576805) } returns listOf(FixtureOdds(bookmakers))
        every { manipulation.scorePlayerWithOdds(salah, PlayerPosition.ATTACKER, bookmakers) } returns salahScore
        every { manipulation.findPlayerWithOddsName(any(), any()) } returns null
        every { todayPlayersRepository.save(expectedHealthy) } just Runs

        underTest.captureTodayPlayers()

        verify(exactly = 0) { todayPlayersRepository.save(match { it.fixtureId == 1576804 }) }
        verify { todayPlayersRepository.save(expectedHealthy) }
    }

    @Test
    fun `queries tomorrow's fixtures relative to the injected clock's date`() {
        every { matchesData.matchesOfTheDay("2026-07-10") } returns emptyList()

        underTest.captureTodayPlayers()

        verify { matchesData.matchesOfTheDay("2026-07-10") }
    }

    @Test
    fun `returns the configured tracked league ids`() {
        assertThat(underTest.trackedLeagueIds()).containsExactly(trackedLeagueId)
    }

    @Test
    fun `returns the fixture ids of every currently cached watchlist`() {
        val watchlistA = TodayPlayersWatchlist(
            1576804, trackedLeagueId, "Unknown League", "Argentina", "Egypt", watchlistMap(), watchlistMap()
        )
        val watchlistB = TodayPlayersWatchlist(
            1576805, trackedLeagueId, "Unknown League", "Home2", "Away2", watchlistMap(), watchlistMap()
        )
        every { todayPlayersRepository.getAllFixtures() } returns listOf(watchlistA, watchlistB)

        assertThat(underTest.availableFixtureIds()).containsExactly(1576804, 1576805)
    }

    @Test
    fun `returns the full cached data for every fixture, joined with league info`() {
        val watchlist = TodayPlayersWatchlist(
            1576804, trackedLeagueId, "Unknown League", "Argentina", "Egypt",
            watchlistMap(PlayerPosition.ATTACKER to listOf(messiScore)), watchlistMap()
        )
        every { todayPlayersRepository.getAllFixtures() } returns listOf(watchlist)

        val result = underTest.allFixtures()

        assertThat(result).containsExactly(
            FixtureTodayPlayers(
                fixtureId = 1576804,
                leagueId = trackedLeagueId,
                leagueName = "Unknown League",
                homeTeamName = "Argentina",
                awayTeamName = "Egypt",
                home = watchlistMap(PlayerPosition.ATTACKER to listOf(messiScore)),
                away = watchlistMap()
            )
        )
    }
}
