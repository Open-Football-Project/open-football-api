package match.insights.sorting

import match.insights.data.client.ClientMatchResponseData
import match.insights.props.LeaguesSortProps
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MatchesSortTest {

    val underTest = MatchesSort(
        leaguesSortProps = LeaguesSortProps(
            listOf("England", "Argentina"),
            listOf("conmebol libertadores"),
            listOf("Premier league", "FA cup"),
            listOf("liga profesional argentina"),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
    )

    @Test
    fun shouldSortByCountry() {
        val result = underTest.sortByPriorityCountries(
            ClientMatchResponseData.matchResponseList,
            { it.league.country ?: "Unknown" })

        Assertions.assertThat(result[0].league.country).isEqualTo("England")
        Assertions.assertThat(result[3].league.country).isEqualTo("Unknown")
    }

    @Test
    fun shouldSortLeagues() {
        val result = underTest.sortByPriorityLeagues(
            ClientMatchResponseData.matchResponseList,
            { it.league.name })

        Assertions.assertThat(result[0].league.name).isEqualTo("Premier League")
        Assertions.assertThat(result[3].league.name).isEqualTo("FA Cup")
    }

}