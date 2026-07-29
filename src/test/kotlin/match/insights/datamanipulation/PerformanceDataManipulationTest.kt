package match.insights.datamanipulation

import match.insights.data.client.ClientMatchResponseData
import match.insights.model.Performance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PerformanceDataManipulationTest {
    val underTest = PerformanceDataManipulation()

    @Test
    fun shouldCalculateRecentScorePerformance() {

        val result = underTest.calculateScorePerformance(
            33, ClientMatchResponseData.matchResponseList
        )

        assertThat(result).isEqualTo(Performance.GOOD.value)
    }

    @Test
    fun shouldBeNoDataWithoutMatches() {
        val result = underTest.calculateScorePerformance(
            33, emptyList()
        )
        assertThat(result).isEqualTo(Performance.NO_DATA.value)
    }
}