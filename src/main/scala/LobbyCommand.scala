sealed trait LobbyCommand extends GameCommand
case object GetGames extends LobbyCommand
case class AvailableGames(games: Map[String, Game]) extends LobbyCommand
