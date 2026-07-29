package match.insights.sorting

import match.insights.props.LeaguesSortProps
import org.springframework.stereotype.Component

@Component
class MatchesSort(private val leaguesSortProps: LeaguesSortProps) {

    private val priorityLeagues =
        leaguesSortProps.international +
                leaguesSortProps.argentina +
                leaguesSortProps.france +
                leaguesSortProps.uruguay +
                leaguesSortProps.england +
                leaguesSortProps.spain +
                leaguesSortProps.italy +
                leaguesSortProps.usa +
                leaguesSortProps.germany +
                leaguesSortProps.portugal +
                leaguesSortProps.brazil


    private val priorityCountries = leaguesSortProps.countries


    fun <T> sortByPriorityCountries(
        data: List<T>,
        countrySelector: (T) -> String
    ): List<T> {
        return data.sortedWith(compareBy { item ->
            val idx = priorityCountries.indexOfFirst { countrySelector(item).contains(it, ignoreCase = true) }
            if (idx == -1) Int.MAX_VALUE else idx
        })
    }


    fun <T> sortByPriorityLeagues(
        data: List<T>,
        leagueSelector: (T) -> String
    ): List<T> {
        return data.sortedWith(compareBy { item ->
            val leagueName = leagueSelector(item).trim()
            val exactIdx = priorityLeagues.indexOfFirst { leagueName.equals(it, ignoreCase = true) }
            val containsIdx = priorityLeagues.indexOfFirst { leagueName.contains(it, ignoreCase = true) }

            when {
                exactIdx != -1 -> exactIdx
                containsIdx != -1 -> containsIdx + 1000
                else -> Int.MAX_VALUE
            }
        })
    }

}