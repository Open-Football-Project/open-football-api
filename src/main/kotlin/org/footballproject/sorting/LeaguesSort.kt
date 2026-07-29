package org.footballproject.sorting

import org.footballproject.props.LeaguesSortProps
import org.footballproject.response.CountryLeagues
import org.footballproject.response.LeagueBasicInfo
import org.springframework.stereotype.Component

@Component
class LeaguesSort(private val leaguesSortProps: LeaguesSortProps) {
    private val internationalLeaguePriorities = leaguesSortProps.international

    private val countryLeaguePriorities = mapOf(
        "Argentina" to leaguesSortProps.argentina,
        "France" to leaguesSortProps.france,
        "Uruguay" to leaguesSortProps.uruguay,
        "England" to leaguesSortProps.england,
        "Spain" to leaguesSortProps.spain,
        "Italy" to leaguesSortProps.italy,
        "USA" to leaguesSortProps.usa,
        "Germany" to leaguesSortProps.germany,
        "Portugal" to leaguesSortProps.portugal,
        "Brazil" to leaguesSortProps.brazil
    )

    private val priorityCountries = leaguesSortProps.countries


    fun sortInternationalLeagues(internationalLeagues: List<LeagueBasicInfo>): List<LeagueBasicInfo> {
        return internationalLeagues.sortedWith(compareBy { league ->
            val exactIdx =
                internationalLeaguePriorities.indexOfFirst { league.name.trim().equals(it, ignoreCase = true) }
            val containsIdx = internationalLeaguePriorities.indexOfFirst { league.name.contains(it, ignoreCase = true) }

            when {
                exactIdx != -1 -> exactIdx
                containsIdx != -1 -> containsIdx + 1000
                else -> Int.MAX_VALUE
            }
        })
    }


    fun priorityLeaguesPartitions(
        leagues: List<CountryLeagues>
    ): Pair<List<CountryLeagues>, List<CountryLeagues>> =
        leagues.partition { it.country in priorityCountries }

    fun sortPriorityCountryLeagues(leagues: List<CountryLeagues>): List<CountryLeagues> {
        val (priority, nonPriority) = priorityLeaguesPartitions(leagues)

        val priorityCountriesSorted = priorityCountries.mapNotNull { countryName ->
            priority.find { it.country == countryName }?.let { countryLeague ->
                val priorities = countryLeaguePriorities[countryName].orEmpty()
                val leaguesSorted = countryLeague.leagues.sortedWith(compareBy { league ->
                    val leagueName = league.name.trim()
                    val exactIdx = priorities.indexOfFirst { it.equals(leagueName, ignoreCase = true) }
                    val containsIdx = priorities.indexOfFirst { leagueName.contains(it, ignoreCase = true) }

                    when {
                        exactIdx != -1 -> exactIdx
                        containsIdx != -1 -> containsIdx + 1000
                        else -> Int.MAX_VALUE
                    }
                })
                countryLeague.copy(leagues = leaguesSorted)
            }
        }

        return priorityCountriesSorted + nonPriority
    }

}