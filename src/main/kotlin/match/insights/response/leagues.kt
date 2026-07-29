package match.insights.response

import match.insights.clientData.LeagueMinInfo

data class LeagueBasicInfo(
    val id: Int = -1,
    val name: String = "Unknown League",
    val type: String = "Unknown Type",
    val logo: String? = "",
) {
    companion object {

        fun fromLeague(league: LeagueMinInfo) = LeagueBasicInfo(
            id = league.id,
            name = league.name,
            type = league.type,
            logo = league.logo
        )
    }
}

data class CountryLeagues(
    val country: String,
    val flag: String?,
    val leagues: List<LeagueBasicInfo>
)

data class LeaguesGroups(
    val internationals: List<LeagueBasicInfo>,
    val countryLeagues: List<CountryLeagues>,
    val others: List<LeagueBasicInfo>
)