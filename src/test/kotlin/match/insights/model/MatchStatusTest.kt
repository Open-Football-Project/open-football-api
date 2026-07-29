package match.insights.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MatchStatusTest {

    @Test
    fun `isNow is true for statuses where a match is currently in progress, including breaks and interruptions`() {
        assertThat(MatchStatus.LIVE.isNow()).isTrue()
        assertThat(MatchStatus.FIRST_HALF.isNow()).isTrue()
        assertThat(MatchStatus.HALF_TIME.isNow()).isTrue()
        assertThat(MatchStatus.SECOND_HALF.isNow()).isTrue()
        assertThat(MatchStatus.EXTRA_TIME.isNow()).isTrue()
        assertThat(MatchStatus.PENALTIES.isNow()).isTrue()
        assertThat(MatchStatus.BREAK_TIME.isNow()).isTrue()
        assertThat(MatchStatus.INTERRUPTED.isNow()).isTrue()
    }

    @Test
    fun `isNow is false for statuses where a match has not started, has finished, or never happened`() {
        assertThat(MatchStatus.NOT_STARTED.isNow()).isFalse()
        assertThat(MatchStatus.TIME_TBD.isNow()).isFalse()
        assertThat(MatchStatus.SUSPENDED.isNow()).isFalse()
        assertThat(MatchStatus.FULL_TIME.isNow()).isFalse()
        assertThat(MatchStatus.AFTER_EXTRA_TIME.isNow()).isFalse()
        assertThat(MatchStatus.AFTER_PENALTIES.isNow()).isFalse()
        assertThat(MatchStatus.CANCELLED.isNow()).isFalse()
        assertThat(MatchStatus.POSTPONED.isNow()).isFalse()
        assertThat(MatchStatus.ABANDONED.isNow()).isFalse()
        assertThat(MatchStatus.AWARDED.isNow()).isFalse()
        assertThat(MatchStatus.WALKOVER.isNow()).isFalse()
    }

    @Test
    fun `isValidLiveChartStatus is true only for the statuses live-chart capture treats as clean in-progress play`() {
        assertThat(MatchStatus.LIVE.isValidLiveChartStatus()).isTrue()
        assertThat(MatchStatus.FIRST_HALF.isValidLiveChartStatus()).isTrue()
        assertThat(MatchStatus.SECOND_HALF.isValidLiveChartStatus()).isTrue()
        assertThat(MatchStatus.EXTRA_TIME.isValidLiveChartStatus()).isTrue()
    }

    @Test
    fun `isValidLiveChartStatus is false for breaks, interruptions, not-started, finished, and voided statuses`() {
        assertThat(MatchStatus.HALF_TIME.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.PENALTIES.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.BREAK_TIME.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.INTERRUPTED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.SUSPENDED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.NOT_STARTED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.TIME_TBD.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.FULL_TIME.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.AFTER_EXTRA_TIME.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.AFTER_PENALTIES.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.CANCELLED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.POSTPONED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.ABANDONED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.AWARDED.isValidLiveChartStatus()).isFalse()
        assertThat(MatchStatus.WALKOVER.isValidLiveChartStatus()).isFalse()
    }

    @Test
    fun `isVoided is true for statuses where no real match happened or will happen`() {
        assertThat(MatchStatus.CANCELLED.isVoided()).isTrue()
        assertThat(MatchStatus.POSTPONED.isVoided()).isTrue()
        assertThat(MatchStatus.ABANDONED.isVoided()).isTrue()
        assertThat(MatchStatus.WALKOVER.isVoided()).isTrue()
        assertThat(MatchStatus.AWARDED.isVoided()).isTrue()
    }

    @Test
    fun `isVoided is false for statuses where a match is scheduled, in progress, or finished`() {
        assertThat(MatchStatus.NOT_STARTED.isVoided()).isFalse()
        assertThat(MatchStatus.TIME_TBD.isVoided()).isFalse()
        assertThat(MatchStatus.FIRST_HALF.isVoided()).isFalse()
        assertThat(MatchStatus.HALF_TIME.isVoided()).isFalse()
        assertThat(MatchStatus.SECOND_HALF.isVoided()).isFalse()
        assertThat(MatchStatus.EXTRA_TIME.isVoided()).isFalse()
        assertThat(MatchStatus.PENALTIES.isVoided()).isFalse()
        assertThat(MatchStatus.BREAK_TIME.isVoided()).isFalse()
        assertThat(MatchStatus.LIVE.isVoided()).isFalse()
        assertThat(MatchStatus.INTERRUPTED.isVoided()).isFalse()
        assertThat(MatchStatus.SUSPENDED.isVoided()).isFalse()
        assertThat(MatchStatus.FULL_TIME.isVoided()).isFalse()
        assertThat(MatchStatus.AFTER_EXTRA_TIME.isVoided()).isFalse()
        assertThat(MatchStatus.AFTER_PENALTIES.isVoided()).isFalse()
    }
}
