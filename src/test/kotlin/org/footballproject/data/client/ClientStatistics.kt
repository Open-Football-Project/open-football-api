package org.footballproject.data.client

import org.footballproject.clientData.LiveStatistic
import org.footballproject.clientData.LiveStatsTeamInfo
import org.footballproject.clientData.LiveTeamStats

class ClientStatistics {
    companion object {
        val liveStatistics = listOf(
            LiveTeamStats(
                team = LiveStatsTeamInfo(
                    id = 101,
                    name = "Manchester United",
                    logo = "https://media.api-sports.io/football/teams/33.png"
                ),
                statistics = listOf(
                    LiveStatistic("Shots on Goal", 7),
                    LiveStatistic("Shots off Goal", 4),
                    LiveStatistic("Total Shots", 14),
                    LiveStatistic("Blocked Shots", 3),
                    LiveStatistic("Shots insidebox", 9),
                    LiveStatistic("Shots outsidebox", 5),
                    LiveStatistic("Fouls", 12),
                    LiveStatistic("Corner Kicks", 6),
                    LiveStatistic("Offsides", 2),
                    LiveStatistic("Ball Possession", "58%"),
                    LiveStatistic("Yellow Cards", 2),
                    LiveStatistic("Red Cards", 0),
                    LiveStatistic("Goalkeeper Saves", 3),
                    LiveStatistic("Total passes", 480),
                    LiveStatistic("Passes accurate", 420),
                    LiveStatistic("Passes %", "88%"),
                    LiveStatistic("expected_goals", "1.70"),
                    LiveStatistic("goals_prevented", "0.40")
                )
            ),
            LiveTeamStats(
                team = LiveStatsTeamInfo(
                    id = 102,
                    name = "Liverpool",
                    logo = "https://media.api-sports.io/football/teams/40.png"
                ),
                statistics = listOf(
                    LiveStatistic("Shots on Goal", 4),
                    LiveStatistic("Shots off Goal", 6),
                    LiveStatistic("Total Shots", 12),
                    LiveStatistic("Blocked Shots", 2),
                    LiveStatistic("Shots insidebox", 7),
                    LiveStatistic("Shots outsidebox", 5),
                    LiveStatistic("Fouls", 14),
                    LiveStatistic("Corner Kicks", 5),
                    LiveStatistic("Offsides", 1),
                    LiveStatistic("Ball Possession", "42%"),
                    LiveStatistic("Yellow Cards", 1),
                    LiveStatistic("Red Cards", 0),
                    LiveStatistic("Goalkeeper Saves", 5),
                    LiveStatistic("Total passes", 395),
                    LiveStatistic("Passes accurate", 341),
                    LiveStatistic("Passes %", "86%"),
                    LiveStatistic("expected_goals", "1.20"),
                    LiveStatistic("goals_prevented", "-0.10")
                )
            )
        )
    }
}