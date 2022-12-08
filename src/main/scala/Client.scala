import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.stage.Window

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Client extends JFXApp3 {
  val system = akka.actor.ActorSystem("TicTacToe")
  implicit val timeout: Timeout = 3.seconds

  val lobbyActor: ActorRef[LobbyCommand] =
    system.spawn(LobbyActor(), "lobby-actor")

  val gameIdField = new TextField()
  gameIdField.promptText = "Enter game ID"

  val playerField = new TextField()
  playerField.promptText = "Enter player name"

  val joinButton = new Button("Join game")
  joinButton.onAction = _ => {
    val gameId = gameIdField.text.value
    val player = playerField.text.value
    lobbyActor ! JoinGame(gameId, player)
  }

  val startButton = new Button("Start game")
  startButton.onAction = _ => {
    val gameId = gameIdField.text.value
    val player = playerField.text.value
    lobbyActor ! CreateGame(gameId, player, "")
  }

  val gridPane = new GridPane()
  gridPane.hgap = 10
  gridPane.vgap = 10
  gridPane.padding = Insets(10)

  gridPane.add(new Label("Game ID:"), 0, 0)
  gridPane.add(gameIdField, 1, 0)
  gridPane.add(new Label("Player:"), 0, 1)
  gridPane.add(playerField, 1, 1)

  val buttons = new HBox(10)
  buttons.children = List(joinButton, startButton)

  val root = new VBox()
  root.children = List(gridPane, buttons)

  stage = new PrimaryStage() {
    title = "TicTacToe Lobby"
    scene = new Scene(root)
  }

  override def stopApp(): Unit = {
    system.terminate()
    super.stopApp()
  }
}
