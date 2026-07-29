package match.insights.data.client

import match.insights.clientData.LineupCoach
import match.insights.clientData.LineupPlayer
import match.insights.clientData.LineupPlayerWrapper
import match.insights.clientData.LineupTeam
import match.insights.clientData.LineupTeamInfo

class ClientLineupResponseData {
    companion object {

        val mockLineups = listOf(
            LineupTeam(
                team = LineupTeamInfo(
                    id = 33,
                    name = "Manchester United",
                    logo = "https://media.api-sports.io/football/teams/33.png"
                ),
                coach = LineupCoach(
                    id = 501,
                    name = "Erik ten Hag",
                    photo = "https://media.api-sports.io/football/coachs/501.png"
                ),
                formation = "4-2-3-1",
                startXI = listOf(
                    LineupPlayerWrapper(LineupPlayer(1, "André Onana", 24, "G", "1:1")),
                    LineupPlayerWrapper(LineupPlayer(2, "Diogo Dalot", 20, "D", "2:4")),
                    LineupPlayerWrapper(LineupPlayer(3, "Harry Maguire", 5, "D", "2:2")),
                    LineupPlayerWrapper(LineupPlayer(4, "Lisandro Martínez", 6, "D", "2:3")),
                    LineupPlayerWrapper(LineupPlayer(5, "Luke Shaw", 23, "D", "2:1")),
                    LineupPlayerWrapper(LineupPlayer(6, "Casemiro", 18, "M", "3:2")),
                    LineupPlayerWrapper(LineupPlayer(7, "Bruno Fernandes", 8, "M", "4:2")),

                    ),
                substitutes = listOf(
                    LineupPlayerWrapper(LineupPlayer(10, "Christian Eriksen", 14, "M", null)),
                    LineupPlayerWrapper(LineupPlayer(11, "Scott McTominay", 39, "M", null))
                )
            ),
            LineupTeam(
                team = LineupTeamInfo(
                    id = 40,
                    name = "Liverpool",
                    logo = "https://media.api-sports.io/football/teams/40.png"
                ),
                coach = LineupCoach(
                    id = 601,
                    name = "Jürgen Klopp",
                    photo = "https://media.api-sports.io/football/coachs/601.png"
                ),
                formation = "4-3-3",
                startXI = listOf(
                    LineupPlayerWrapper(LineupPlayer(12, "Alisson Becker", 1, "G", "1:1")),
                    LineupPlayerWrapper(LineupPlayer(15, "Ibrahima Konaté", 5, "D", "2:3")),
                    LineupPlayerWrapper(LineupPlayer(16, "Andrew Robertson", 26, "D", "2:1")),
                    LineupPlayerWrapper(LineupPlayer(17, "Alexis Mac Allister", 10, "M", "3:2")),

                    ),
                substitutes = listOf(
                    LineupPlayerWrapper(LineupPlayer(20, "Joe Gomez", 2, "D", null)),
                    LineupPlayerWrapper(LineupPlayer(21, "Diogo Jota", 20, "F", null))
                )
            )
        )
    }
}