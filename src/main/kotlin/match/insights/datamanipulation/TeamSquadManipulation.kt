package match.insights.datamanipulation

import match.insights.clientData.PlayerResponse
import match.insights.response.PlayerSummary
import org.springframework.stereotype.Component

@Component
class TeamSquadManipulation {

    fun teamSquadSummary(data: Map<Int, List<PlayerResponse>>): List<PlayerSummary> {
        return data.values.flatten().map {
            PlayerSummary.fromResponse(it)
        }.sortedWith(
            compareByDescending<PlayerSummary> { it.goals }
                .thenByDescending { it.redCards }
                .thenByDescending { it.yellowCards }
                .thenByDescending { it.penaltiesScored }
                .thenByDescending { it.penaltiesSaved }
        )
    }
}