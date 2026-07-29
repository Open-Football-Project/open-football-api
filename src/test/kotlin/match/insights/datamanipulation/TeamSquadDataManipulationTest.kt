package match.insights.datamanipulation

import match.insights.data.client.ClientTeamDetails
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamSquadDataManipulationTest {
    val underTest = TeamSquadManipulation()

    @Test
    fun shouldReturnThePlayersSummary() {

        val result = underTest.teamSquadSummary(mapOf(1 to ClientTeamDetails.mockPlayersResponse))

        val firstPlayer = ClientTeamDetails.mockPlayersResponse[1]
        val stats = firstPlayer.statistics[0]

        assertThat(result[0].name).isEqualTo(firstPlayer.player.name)
        assertThat(result[0].weight).isEqualTo(firstPlayer.player.weight)
        assertThat(result[0].height).isEqualTo(firstPlayer.player.height)
        assertThat(result[0].age).isEqualTo(firstPlayer.player.age)
        assertThat(result[0].yellowCards).isEqualTo(stats.cards.yellow)
        assertThat(result[0].redCards).isEqualTo(stats.cards.red)
        assertThat(result[0].goals).isEqualTo(stats.goals.total)
        assertThat(result[0].penaltiesSaved).isEqualTo(stats.penalty.saved)
        assertThat(result[0].penaltiesScored).isEqualTo(stats.penalty.scored)
    }

}