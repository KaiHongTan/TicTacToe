trait GameCommand
case class CreateGame(gameId: String, player1: String, player2: String) extends GameCommand
case class JoinGame(gameId: String, player: String) extends GameCommand
