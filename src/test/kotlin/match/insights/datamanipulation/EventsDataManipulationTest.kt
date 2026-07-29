package match.insights.datamanipulation


import match.insights.clientData.Event
import match.insights.clientData.Team
import match.insights.clientData.Time
import match.insights.data.client.ClientEventsData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventsDataManipulationTest {
    val underTest = EventsDataManipulation()

    @Test
    fun shouldSumLastFiveMatchesEvents() {

        val result = underTest.fiveMachesEventsSum(ClientEventsData.mockEvents)

        assertThat(result.penalties).isEqualTo(1)
        assertThat(result.firstHalfGoals).isEqualTo(1)
        assertThat(result.firstHalfYellowCards).isEqualTo(1)
        assertThat(result.secondHalfRedCards).isEqualTo(1)
    }

    @Test
    fun shouldTreatEventsWithMissingDetailAsNoPenaltyOrCardMatch() {
        val events = listOf(
            Event(
                time = Time(elapsed = 10),
                team = Team(id = 33, name = "Team A"),
                type = "Goal",
                detail = null
            ),
            Event(
                time = Time(elapsed = 20),
                team = Team(id = 44, name = "Team B"),
                type = "Card",
                detail = null
            )
        )

        val result = underTest.fiveMachesEventsSum(events)

        assertThat(result.penalties).isEqualTo(0)
        assertThat(result.firstHalfGoals).isEqualTo(1)
        assertThat(result.firstHalfYellowCards).isEqualTo(0)
        assertThat(result.firstHalfRedCards).isEqualTo(0)
    }

}