package org.footballproject.datamanipulation

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.footballproject.clientData.LeagueAndCountry
import org.footballproject.clientData.LeagueStandings
import org.footballproject.clientData.LeagueWithStandings
import org.footballproject.clientData.PlayerResponse
import org.footballproject.clientData.PlayerStatistic
import org.footballproject.response.CountryLeagues
import org.footballproject.response.LeagueBasicInfo
import org.footballproject.response.LeagueGroup
import org.footballproject.response.LeagueInfo
import org.footballproject.response.LeagueRankings
import org.footballproject.response.LeagueTeamInfo
import org.footballproject.response.LeaguesGroups
import org.footballproject.response.PlayerRanking
import org.footballproject.response.PositionAndPoints
import org.footballproject.response.Ranking
import org.footballproject.response.TeamPositionsAndPoints
import org.footballproject.sorting.LeaguesSort
import org.springframework.stereotype.Component

@Component
class LeagueDataManipulation(private val leaguesSort: LeaguesSort) {

    private val internationalNames = setOf(
        "world",
        "europe",
        "south america",
        "asia",
        "africa",
        "north & central america",
        "oceania"
    )

    fun positionAndPoints(
        homeTeamId: Int, awayTeamId: Int, leagueWithStandings: LeagueWithStandings?
    ): TeamPositionsAndPoints {
        return leagueWithStandings?.let { league ->
            val groupsData: List<LeagueGroup> = league.standings.toLeagueGroups()
            TeamPositionsAndPoints(
                homeTeam = rankAndPointsByTeamId(homeTeamId, groupsData),
                awayTeam = rankAndPointsByTeamId(awayTeamId, groupsData)
            )
        } ?: TeamPositionsAndPoints(homeTeam = emptyList(), awayTeam = emptyList())
    }


    fun extractLeaguesInfo(leagueWithStandings: LeagueWithStandings?): LeagueInfo {
        return leagueWithStandings?.let { league ->
            LeagueInfo(
                id = league.id,
                name = league.name,
                country = league.country,
                logo = league.logo,
                flag = league.flag,
                season = league.season,
                group = league.standings.toLeagueGroups(),
                videos = emptySet()
            )
        } ?: LeagueInfo(
            id = -1,
            name = "Unknown League",
            country = "Unknown Country",
            logo = "Unknown Logo",
            flag = "Unknown Flag",
            season = -1,
            group = emptyList(),
            videos = emptySet()
        )
    }

    private fun List<List<LeagueStandings>>.toLeagueGroups(): List<LeagueGroup> =
        this.flatten().groupBy { it.group ?: "Default" }
            .map { (key, value) -> LeagueGroup(key, value.toLeagueTeamInfoList()) }


    private fun List<LeagueStandings>.toLeagueTeamInfoList(): List<LeagueTeamInfo> {
        return this.map {
            LeagueTeamInfo(
                teamId = it.team.id,
                rank = it.rank,
                teamName = it.team.name,
                logo = it.team.logo ?: "",
                points = it.points,
                played = it.all?.played ?: 0,
                won = it.all?.win ?: 0,
                draw = it.all?.draw ?: 0,
                lost = it.all?.lose ?: 0,
                goalsFor = it.all?.goals?.`for` ?: 0,
                goalsAgainst = it.all?.goals?.against ?: 0,
                form = it.form ?: "",
                update = it.update
            )
        }
    }


    fun topNRankings(players: List<PlayerResponse>): LeagueRankings =
        runBlocking {
            val topScorers = async {
                Ranking("Top Scorers", topNRanking(players, 5, "Goals") { it.goals.total })
            }
            val topAssists = async {
                Ranking("Top Assists", topNRanking(players, 5, "Assists") { it.goals.assists })
            }
            val topYellowCards = async {
                Ranking("Top Yellow Cards", topNRanking(players, 5, "Yellow Cards") { it.cards.yellow })
            }
            val topRedCards = async {
                Ranking("Top Red Cards", topNRanking(players, 5, "Red Cards") { it.cards.red })
            }
            val topAppearances = async {
                Ranking("Top Appearances", topNRanking(players, 5, "Appearances") { it.games.appearences })
            }
            val topPenaltyGoals = async {
                Ranking("Top Penalty Goals", topNRanking(players, 5, "Penalty Goals") { it.penalty.scored })
            }
            val topPenaltiesSaved = async {
                Ranking("Top Penalties Saved", topNRanking(players, 5, "Penalty Saves") { it.penalty.saved })
            }
            val topGoalsConceded = async {
                Ranking("Top Goals Conceded", topNRanking(players, 5, "Goals Conceded") { it.goals.conceded })
            }
            val topSaves = async {
                Ranking("Top Saves", topNRanking(players, 5, "Saves") { it.goals.saves })
            }
            val topPenaltiesWon = async {
                Ranking("Top Penalties Won", topNRanking(players, 5, "Penalties Won") { it.penalty.won })
            }
            val topPenaltiesMissed = async {
                Ranking("Top Penalties Missed", topNRanking(players, 5, "Penalties Missed") { it.penalty.missed })
            }

            LeagueRankings(
                topScorers.await(),
                topAssists.await(),
                topYellowCards.await(),
                topRedCards.await(),
                topAppearances.await(),
                topPenaltyGoals.await(),
                topPenaltiesSaved.await(),
                topGoalsConceded.await(),
                topSaves.await(),
                topPenaltiesWon.await(),
                topPenaltiesMissed.await()
            )
        }


    fun groupLeagues(leagues: List<LeagueAndCountry>): LeaguesGroups {
        val internationals = mutableListOf<LeagueBasicInfo>()
        val byCountry = mutableMapOf<String, MutableList<LeagueBasicInfo>>()
        val others = mutableListOf<LeagueBasicInfo>()

        leagues.forEach { leagueAndCountry ->
            val name = leagueAndCountry.country.name.lowercase()

            when {
                leagueAndCountry.country.code != null -> {
                    byCountry.computeIfAbsent(leagueAndCountry.country.name) { mutableListOf() }
                        .add(
                            LeagueBasicInfo.fromLeague(leagueAndCountry.league)
                        )
                }

                internationalNames.contains(name) -> {
                    internationals.add(
                        LeagueBasicInfo.fromLeague(leagueAndCountry.league)
                    )
                }

                else -> {
                    others.add(
                        LeagueBasicInfo.fromLeague(leagueAndCountry.league)
                    )
                }
            }
        }

        return LeaguesGroups(
            internationals = leaguesSort.sortInternationalLeagues(internationals),
            countryLeagues = leaguesSort.sortPriorityCountryLeagues(byCountry.map { (country, leagues) ->
                CountryLeagues(
                    country = country,
                    flag = leagues.firstOrNull()?.logo,
                    leagues = leagues
                )
            }),
            others = others
        )
    }

    private fun rankAndPointsByTeamId(
        id: Int,
        groupsData: List<LeagueGroup>
    ): List<PositionAndPoints> =
        groupsData.mapNotNull { (label, teams) ->
            teams.firstOrNull { teamInfo -> teamInfo.teamId == id }
                ?.let { PositionAndPoints(it.rank, it.points, label) }
        }


    private fun topNRanking(
        players: List<PlayerResponse>,
        topN: Int = 5,
        statName: String,
        valueExtractor: (PlayerStatistic) -> Int?
    ): List<PlayerRanking> {
        return players.mapNotNull { pr ->
            val stats = pr.statistics.firstOrNull() ?: return@mapNotNull null
            val value = valueExtractor(stats) ?: 0
            PlayerRanking(
                player = pr.player.name,
                team = stats.team.name,
                statName = statName,
                value = value
            )
        }.sortedByDescending { it.value }
            .take(topN)
    }


}