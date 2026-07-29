package match.insights.response

import match.insights.clientData.Team
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class H2HDetailsTest {


    @Test
    fun shouldGetTheUnknownWinner() {
        assertThat(H2HDetails.getWinner(null, null)).isEqualTo("No Data")
    }

    @Test
    fun shouldGetHomeWinnerName() {
        assertThat(H2HDetails.getWinner(Team().copy(name = "homeWinner", winner = true), null)).isEqualTo("homeWinner")
    }

    @Test
    fun shouldGetAwayWinnerName() {
        assertThat(
            H2HDetails.getWinner(
                Team(winner = false),
                Team().copy(name = "awayWinner", winner = true)
            )
        ).isEqualTo("awayWinner")
    }

    @Test
    fun shouldBeADraw() {
        assertThat(
            H2HDetails.getWinner(
                Team(name = "homeTeam", winner = null),
                Team().copy(name = "awayTean", winner = false)
            )
        ).isEqualTo("Draw")
    }
}