package match.insights.props

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TodayPlayersPropsTest {

    private val props = TodayPlayersProps(nonPlayerOddsValues = setOf("Yes", "No", "No Goalscorer"))

    @Test
    fun normalizesTitleCaseMarketNamesToSnakeCase() {
        assertThat(TodayPlayersProps.normalize("Away Player Shots On Target Total"))
            .isEqualTo("away_player_shots_on_target_total")
        assertThat(TodayPlayersProps.normalize("Goalkeeper Saves"))
            .isEqualTo("goalkeeper_saves")
        assertThat(TodayPlayersProps.normalize("Player to be booked"))
            .isEqualTo("player_to_be_booked")
    }

    @Test
    fun collapsesRepeatedWhitespaceAndTrimsBeforeNormalizing() {
        assertThat(TodayPlayersProps.normalize("  Player   Assists  "))
            .isEqualTo("player_assists")
    }

    @Test
    fun isIdempotentOnAlreadyNormalizedValues() {
        assertThat(TodayPlayersProps.normalize("away_player_shots_on_target_total"))
            .isEqualTo("away_player_shots_on_target_total")
    }


    @Test
    fun recognizesConfiguredSentinelValuesRegardlessOfCaseOrSpacing() {
        assertThat(props.isNonPlayerOddsValue("Yes")).isTrue()
        assertThat(props.isNonPlayerOddsValue("no")).isTrue()
        assertThat(props.isNonPlayerOddsValue(" NO GOALSCORER ")).isTrue()
    }

    @Test
    fun recognizesOverUnderLinesForAnyThreshold() {
        assertThat(props.isNonPlayerOddsValue("Over 9.5")).isTrue()
        assertThat(props.isNonPlayerOddsValue("Under 3.5")).isTrue()
        assertThat(props.isNonPlayerOddsValue("over 12.5")).isTrue()
        assertThat(props.isNonPlayerOddsValue("Under 4")).isTrue()
    }

    @Test
    fun doesNotFlagRealPlayerNamesAsNonPlayerValues() {
        assertThat(props.isNonPlayerOddsValue("Reinier Carvalho")).isFalse()
        assertThat(props.isNonPlayerOddsValue("da Silva Willian Jose - 1+")).isFalse()
    }

    @Test
    fun stillRecognizesOverUnderLinesWhenNoSentinelSetIsConfigured() {
        val defaultProps = TodayPlayersProps()

        assertThat(defaultProps.isNonPlayerOddsValue("Over 9.5")).isTrue()
        assertThat(defaultProps.isNonPlayerOddsValue("Yes")).isFalse()
    }
}
