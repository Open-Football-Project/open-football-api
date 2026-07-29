package org.footballproject.data.client

import org.footballproject.clientData.PlayerTransfer
import org.footballproject.clientData.Transfer
import org.footballproject.clientData.TransferPlayer
import org.footballproject.clientData.TransferTeam
import org.footballproject.clientData.TransferTeams

class ClientTeamTransfers {
    companion object {
        val mockTeamTransfers = listOf(
            PlayerTransfer(
                player = TransferPlayer(
                    id = 10123,
                    name = "Mason Greenwood",
                    photo = "https://media.api-sports.io/football/players/10123.png"
                ),
                update = "2025-08-31T10:22:00+00:00",
                transfers = listOf(
                    Transfer(
                        date = "2025-07-15",
                        type = "Loan",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                id = 50,
                                name = "Manchester City",
                                logo = "https://media.api-sports.io/football/teams/50.png"
                            ),
                            teamFrom = TransferTeam(
                                id = 33,
                                name = "Manchester United",
                                logo = "https://media.api-sports.io/football/teams/33.png"
                            )
                        )
                    )
                )
            ),
            PlayerTransfer(
                player = TransferPlayer(10245, "Phil Foden", "https://media.api-sports.io/football/players/10245.png"),
                update = "2025-08-12T14:48:00+00:00",
                transfers = listOf(
                    Transfer(
                        date = "2025-06-29",
                        type = "Return from Loan",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                50,
                                "Manchester City",
                                "https://media.api-sports.io/football/teams/50.png"
                            ),
                            teamFrom = TransferTeam(
                                70,
                                "Leeds United",
                                "https://media.api-sports.io/football/teams/70.png"
                            )
                        )
                    ),
                    Transfer(
                        date = "2024-08-05",
                        type = "Loan",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                70,
                                "Leeds United",
                                "https://media.api-sports.io/football/teams/70.png"
                            ),
                            teamFrom = TransferTeam(
                                50,
                                "Manchester City",
                                "https://media.api-sports.io/football/teams/50.png"
                            )
                        )
                    )
                )
            ),
            PlayerTransfer(
                player = TransferPlayer(
                    12019,
                    "Julian Alvarez",
                    "https://media.api-sports.io/football/players/12019.png"
                ),
                update = "2025-09-01T09:10:00+00:00",
                transfers = listOf(
                    Transfer(
                        date = "2025-09-01",
                        type = "Free Transfer",
                        teams = TransferTeams(
                            teamTo = TransferTeam(
                                96,
                                "Atletico Madrid",
                                "https://media.api-sports.io/football/teams/96.png"
                            ),
                            teamFrom = TransferTeam(
                                50,
                                "Manchester City",
                                "https://media.api-sports.io/football/teams/50.png"
                            )
                        )
                    )
                )
            )
        )

    }
}