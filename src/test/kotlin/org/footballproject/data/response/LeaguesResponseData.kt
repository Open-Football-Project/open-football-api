package org.footballproject.data.response

import org.footballproject.response.CountryLeagues
import org.footballproject.response.LeagueBasicInfo

class LeaguesResponseData {
    companion object {
        val internationalLeagues: List<LeagueBasicInfo> =
            listOf(
                LeagueBasicInfo(1, "anyLeague at the top", "unknown", "no logo"),
                LeagueBasicInfo(2, "anyOtherLeague at the top", "unknown", "no logo"),
                LeagueBasicInfo(3, "conmebol libertadores", "unknown", "no logo"),
            )

        val argLeague = CountryLeagues(
            "Argentina",
            "light blue and white flag",
            leagues = listOf(
                LeagueBasicInfo(1, "anyLeague at the top", "unknown", "no logo"),
                LeagueBasicInfo(2, "liga profesional argentina", "unknown", "no logo")
            )
        )

        val kazLeague = CountryLeagues(
            "Kazakhstan",
            "...",
            leagues = listOf(
                LeagueBasicInfo(1, "anyLeague at the top", "unknown", "no logo"),

                )
        )

    }
}