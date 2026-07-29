package org.footballproject.datamanipulation

import org.footballproject.clientData.PlayerResponse
import org.footballproject.response.PlayerSummary
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