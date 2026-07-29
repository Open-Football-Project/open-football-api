package match.insights.sorting

import match.insights.data.response.LeaguesResponseData
import match.insights.props.LeaguesSortProps
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class LeaguesSortTest {

    val underTest = LeaguesSort(
        leaguesSortProps = LeaguesSortProps(
            listOf("Argentina"),
            listOf("conmebol libertadores"),
            listOf(),
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
    fun shouldSortInternationalLeagues() {
        val result = underTest.sortInternationalLeagues(LeaguesResponseData.Companion.internationalLeagues)

        Assertions.assertThat(result[0].id).isEqualTo(3)
        Assertions.assertThat(result[2].id).isEqualTo(2)
    }

    @Test
    fun shouldPartitionCountryLeagues() {
        val (priority, noPriority) = underTest.priorityLeaguesPartitions(
            listOf(
                LeaguesResponseData.Companion.kazLeague,
                LeaguesResponseData.Companion.argLeague
            )
        )

        Assertions.assertThat(priority[0].country).isEqualTo("Argentina")
        Assertions.assertThat(noPriority[0].country).isEqualTo("Kazakhstan")
    }

    @Test
    fun shouldSortPriorityCountrylLeagues() {
        val result = underTest.sortPriorityCountryLeagues(
            listOf(
                LeaguesResponseData.Companion.kazLeague,
                LeaguesResponseData.Companion.argLeague
            )
        )

        Assertions.assertThat(result[0].country).isEqualTo("Argentina")
        Assertions.assertThat(result[0].leagues[0].id).isEqualTo(2)
        Assertions.assertThat(result[1].country).isEqualTo("Kazakhstan")
    }
}