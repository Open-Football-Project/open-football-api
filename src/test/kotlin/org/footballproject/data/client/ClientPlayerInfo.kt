package org.footballproject.data.client

import org.footballproject.clientData.PlayerBirth
import org.footballproject.clientData.PlayerInfo
import org.footballproject.clientData.PlayerInfoCards
import org.footballproject.clientData.PlayerInfoGames
import org.footballproject.clientData.PlayerInfoGoals
import org.footballproject.clientData.PlayerInfoLeague
import org.footballproject.clientData.PlayerInfoPasses
import org.footballproject.clientData.PlayerInfoPenalty
import org.footballproject.clientData.PlayerInfoResponse
import org.footballproject.clientData.PlayerInfoShots
import org.footballproject.clientData.PlayerInfoStatistics
import org.footballproject.clientData.PlayerInfoTeam

class ClientPlayerInfo {
    companion object {

        val playerInfoResponse = PlayerInfoResponse(
            player = player(),
            statistics = listOf(premierLeagueStats())
        )

        fun player(): PlayerInfo =
            PlayerInfo(
                id = 2273,
                name = "Kepa",
                firstname = "Kepa",
                lastname = "Arrizabalaga Revuelta",
                age = 31,
                birth = PlayerBirth(
                    date = "1994-10-03",
                    place = "Ondárroa",
                    country = "Spain"
                ),
                nationality = "Spain",
                height = "189",
                weight = "84",
                injured = false,
                photo = "https://media.api-sports.io/football/players/2273.png"
            )

        fun premierLeagueStats(): PlayerInfoStatistics =
            PlayerInfoStatistics(
                team = PlayerInfoTeam(
                    id = 42,
                    name = "Arsenal",
                    logo = "https://media.api-sports.io/football/teams/42.png"
                ),
                league = PlayerInfoLeague(
                    id = 39,
                    name = "Premier League",
                    country = "England",
                    logo = "https://media.api-sports.io/football/leagues/39.png",
                    flag = "https://media.api-sports.io/flags/gb-eng.svg",
                    season = 2025
                ),
                games = PlayerInfoGames(
                    appearences = 3,
                    lineups = 3,
                    minutes = 282,
                    number = 13,
                    position = "Goalkeeper",
                    rating = "6.9",
                    captain = false
                ),
                goals = PlayerInfoGoals(
                    total = 0,
                    conceded = 1,
                    assists = 0,
                    saves = 6
                ),
                cards = PlayerInfoCards(
                    yellow = 1,
                    yellowred = 0,
                    red = 0
                ),
                passes = PlayerInfoPasses(
                    total = 113,
                    key = 0,
                    accuracy = "82"
                ),
                shots = PlayerInfoShots(
                    total = 0,
                    on = 0
                ),
                penalty = PlayerInfoPenalty(
                    won = null,
                    commited = null,
                    scored = 0,
                    missed = 0,
                    saved = 0
                )
            )
    }
}