package match.insights.response

import match.insights.clientData.LineupPlayerWrapper
import match.insights.clientData.LineupTeam

class TeamsLineups(
    val teamA: TeamLineup?, val teamB: TeamLineup?
) {
    companion object {
        fun fromClientData(teams: List<LineupTeam>) = TeamsLineups(
            teamA = if (teams.isEmpty()) null else TeamLineup.fromClientLineup(teams[0]),
            teamB = if (teams.isEmpty() && teams.size < 2) null else TeamLineup.fromClientLineup(teams[1])
        )
    }
}


class TeamLineup(
    val teamId: Int,
    val teamLogo: String,
    val teamName: String,
    val teamFormation: String,
    val lineup: List<LineupPlayer>,
    val substitutes: List<LineupPlayer>
) {
    companion object {
        fun fromClientLineup(teamInfo: LineupTeam) = TeamLineup(
            teamId = teamInfo.team.id,
            teamLogo = teamInfo.team.logo,
            teamName = teamInfo.team.name,
            teamFormation = teamInfo.formation ?: "4-4-2",
            lineup = LineupPlayer.fromClientPlayersWrapper(teamInfo.startXI),
            substitutes = LineupPlayer.fromClientPlayersWrapper(teamInfo.substitutes),
        )
    }
}

class LineupPlayer(
    val name: String, val number: Int, val pos: String, val grid: String
) {
    companion object {
        fun fromClientPlayersWrapper(players: List<LineupPlayerWrapper>) = players.map {
            LineupPlayer(
                name = it.player.name,
                number = it.player.number ?: -1,
                pos = it.player.pos,
                grid = it.player.grid ?: "0:0"
            )
        }
    }
}