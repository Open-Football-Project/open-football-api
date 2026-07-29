package match.insights.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.time.Instant

enum class LiveIndicatorType {
    MOMENTUM,
    CONTROL,
    GOAL_THREAT
}

data class LiveChartPoint(
    val minute: Int,
    val value: Int,
    val capturedAt: Instant,
    @JsonIgnore
    val homeMomentumScore: Double = 0.0,
    @JsonIgnore
    val awayMomentumScore: Double = 0.0,
    @JsonIgnore
    val homePossessionPercent: Double = 0.0,
    @JsonIgnore
    val awayPossessionPercent: Double = 0.0,
    @JsonIgnore
    val homeTotalPasses: Double = 0.0,
    @JsonIgnore
    val awayTotalPasses: Double = 0.0,
    @JsonIgnore
    val homeAccuratePasses: Double = 0.0,
    @JsonIgnore
    val awayAccuratePasses: Double = 0.0,
    @JsonIgnore
    val homeGoalThreatScore: Double = 0.0,
    @JsonIgnore
    val awayGoalThreatScore: Double = 0.0
) : Serializable

data class LiveIndicatorFixture(
    val fixtureId: Int,
    val homeTeamName: String,
    val awayTeamName: String,
    val points: List<LiveChartPoint>
) : Serializable

data class LiveIndicatorFixtures(
    val fixtures: List<LiveIndicatorFixture> = emptyList()
) : Serializable


interface IndicatorBaseline {
    val capturedAt: Instant
    val elapsedMinutes: Int
}

data class MomentumBaseline(
    val homeScore: Double,
    val awayScore: Double,
    override val capturedAt: Instant,
    override val elapsedMinutes: Int,
) : IndicatorBaseline

data class ControlBaseline(
    val homePossessionPercent: Double,
    val awayPossessionPercent: Double,
    val homeTotalPasses: Double,
    val awayTotalPasses: Double,
    val homeAccuratePasses: Double,
    val awayAccuratePasses: Double,
    override val capturedAt: Instant,
    override val elapsedMinutes: Int,
) : IndicatorBaseline

data class GoalThreatBaseline(
    val homeScore: Double,
    val awayScore: Double,
    override val capturedAt: Instant,
    override val elapsedMinutes: Int,
) : IndicatorBaseline
