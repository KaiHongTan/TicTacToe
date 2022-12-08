import akka.actor.TypedActor.context
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{Cluster, Join}

object LobbyActor {
  def apply(): Behavior[LobbyCommand] =
    Behaviors.setup { context =>
      val cluster = Cluster(context.system)
      cluster.manager ! Join(cluster.selfMember.address)

      context.log.info("Lobby actor started")

      lobbyBehavior(Map.empty)
    }

  private def lobbyBehavior(games: Map[String, Game]): Behavior[LobbyCommand] =
    Behaviors.receiveMessage {
      case GetGames =>
        context.log.info(s"Sending available games: $games")
        sender() ! AvailableGames(games)
        Behaviors.same

      case gameCommand: GameCommand =>
        gameActor ! gameCommand
        lobbyBehavior(gameCommand match {
          case CreateGame(gameId, player1, player2) =>
            val game = Game(player1, player2)
            games + (gameId -> game)

          case JoinGame(gameId, player) =>
            val game = games(gameId)
            val updatedGame = game.copy(
              player1 = game.player1.orElse(Some(player)),
              player2 = game.player2.orElse(Some(player))
            )
            games + (gameId -> updatedGame)

          case _ => games
        })
    }
}
