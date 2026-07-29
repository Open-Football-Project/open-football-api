package match.insights.response

import java.time.Instant

data class BetMarketInfo(val id: Int, val name: String, val history: Map<String, List<BetOddsPoint>>)
data class BetOddsPoint(val minute: Int, val odd: String, val capturedAt: Instant)
