package match.insights.datamanipulation.livecharts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommonIndicatorUtilsTest {

    @Test
    fun shouldNormalizeTheRawScoresIntoPercentagesMatchingTheExistingTsFormula() {
        val (homePercent, awayPercent) = normalize(20.0, 8.0)

        assertThat(homePercent).isEqualTo(71)
        assertThat(awayPercent).isEqualTo(29)
    }

    @Test
    fun shouldAlwaysNormalizeToASumOfOneHundred() {
        val (homePercent, awayPercent) = normalize(20.0, 8.0)

        assertThat(homePercent + awayPercent).isEqualTo(100)
    }

    @Test
    fun shouldSplitFiftyFiftyWhenBothScoresAreZero() {
        val (homePercent, awayPercent) = normalize(0.0, 0.0)

        assertThat(homePercent).isEqualTo(50)
        assertThat(awayPercent).isEqualTo(50)
    }

    @Test
    fun shouldReturnZeroWhenThereIsNotEnoughHistoryToFillTheWindow() {
        assertThat(baselineIndex(totalPoints = 2, windowSize = 5)).isEqualTo(0)
    }

    @Test
    fun shouldReturnExactlyWindowSizePollsBackOnceHistoryExceedsTheWindow() {
        assertThat(baselineIndex(totalPoints = 6, windowSize = 5)).isEqualTo(1)
    }
}
