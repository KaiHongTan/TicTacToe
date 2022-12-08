import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{Cluster, Join}

object GameActor {
  def apply(): Behavior[GameCommand] =
    Behaviors.setup { context =>
      val cluster = Cluster(context.system)
      cluster.manager ! Join(cluster.selfMember.address)

      context.log.info("Game actor started")

      gameBehavior(Map.empty)
    }

  private def gameBehavior(games: Map[String, Game]): Behavior[GameCommand] =
    Behaviors.receiveMessage {
      case CreateGame(gameId, player1, player2) =>
        val game = Game(player1, player2)
        context.log.info(s"Created game: $game")
        gameBehavior(games + (gameId -> game))

      case JoinGame(gameId, player) =>
        games.get(gameId) match {
          case Some(game) if game.player1.isEmpty =>
            val updatedGame = game.copy(player1 = Some(player))
            context.log.info(s"Player joined game: $updatedGame")
            gameBehavior(games + (gameId -> updatedGame))

          case Some(game) if game.player2.isEmpty =>
            val updatedGame = game.copy(player2 = Some(player))
            context.log.info(s"Player joined game: $updatedGame")
            gameBehavior(games + (gameId -> updatedGame))

          case Some(_) =>
            context.log.warning(s"Game $gameId is full")
            Behaviors.same

          case None =>
            context.log.warning(s"Game $gameId does not exist")
            Behaviors.same
        }
    }
}
