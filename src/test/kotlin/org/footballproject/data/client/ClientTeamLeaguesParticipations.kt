package org.footballproject.data.client

import org.footballproject.clientData.TeamLeagueParticipation
import org.footballproject.clientData.TeamLeagueParticipationCountry
import org.footballproject.clientData.TeamLeagueParticipationCoverage
import org.footballproject.clientData.TeamLeagueParticipationFixtureCoverage
import org.footballproject.clientData.TeamLeagueParticipationInfo
import org.footballproject.clientData.TeamLeagueParticipationSeasonInfo

class ClientTeamLeaguesParticipation {
    companion object {
        val participation = listOf(
            TeamLeagueParticipation(
                league = TeamLeagueParticipationInfo(
                    128,
                    "Liga Profesional Argentina",
                    "League",
                    "https://media.api-sports.io/football/leagues/128.png"
                ),
                country = TeamLeagueParticipationCountry("Argentina", "AR", "https://media.api-sports.io/flags/ar.svg"),
                seasons = listOf(
                    TeamLeagueParticipationSeasonInfo(
                        year = 2024,
                        start = "2024-05-12",
                        end = "2024-12-15",
                        current = false,
                        coverage = TeamLeagueParticipationCoverage(
                            fixtures = TeamLeagueParticipationFixtureCoverage(true, true, true, true),
                            standings = true,
                            players = true,
                            top_scorers = true,
                            top_assists = true,
                            top_cards = true,
                            injuries = false,
                            predictions = true,
                            odds = false
                        )
                    )
                )
            ),
            TeamLeagueParticipation(
                league = TeamLeagueParticipationInfo(
                    130,
                    "Copa Argentina",
                    "Cup",
                    "https://media.api-sports.io/football/leagues/130.png"
                ),
                country = TeamLeagueParticipationCountry("Argentina", "AR", "https://media.api-sports.io/flags/ar.svg"),
                seasons = listOf(
                    TeamLeagueParticipationSeasonInfo(
                        year = 2024,
                        start = "2024-01-25",
                        end = "2024-12-04",
                        current = false,
                        coverage = TeamLeagueParticipationCoverage(
                            fixtures = TeamLeagueParticipationFixtureCoverage(true, true, false, false),
                            standings = false,
                            players = true,
                            top_scorers = true,
                            top_assists = true,
                            top_cards = true,
                            injuries = false,
                            predictions = true,
                            odds = false
                        )
                    )
                )
            )
        )
    }
}