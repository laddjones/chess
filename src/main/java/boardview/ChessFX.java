package boardview;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import gamecontrol.AIChessController;
import gamecontrol.ChessController;
import gamecontrol.GameController;
import gamecontrol.NetworkedChessController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.beans.binding.Bindings;

/**
 * Main class for the chess application
 * Sets up the top level of the GUI
 * @author Gustavo
 * @version
 */
public class ChessFX extends Application {

    private GameController controller;
    private BoardView board;
    private Text state;
    private Text sideStatus;
    private VBox root;
    private HBox hRoot1;
    private HBox hRoot2;
    private Button resetHumanGM, resetComputerGM, hostingNetWorkGame,
        joinIPGame;


    //starts everything up using a stage
    //@param primaryStage the stage your putting everyting on
    @Override
    public void start(Stage primaryStage) {
        this.controller = new ChessController();
        this.state = new Text(controller.getCurrentState().toString());
        this.sideStatus = new Text(controller.getCurrentSide().toString());
        this.board = new BoardView(controller, state, sideStatus);

        Pane pane = board.getView();
        this.root = new VBox(10);

        this.hRoot1 = new HBox();
        this.hRoot2 = new HBox();

        this.resetHumanGM = new Button("Reset");
        resetHumanGM.setOnMouseClicked(event -> board.reset(
            new ChessController()));

        this.resetComputerGM = new Button("Reset vs Computer");
        resetComputerGM.setOnMouseClicked(event -> board.reset(
            new AIChessController()));

        this.hostingNetWorkGame = new Button("Host a network game");
        hostingNetWorkGame.setOnMouseClicked(makeHostListener());

        Text ipText = new Text();
        try {
            ipText.setText(InetAddress.getLocalHost().toString());
        } catch (UnknownHostException e) {
        }
        TextField theirIPaddress = new TextField();
        this.joinIPGame = new Button("Type IP: Join");
        joinIPGame.disableProperty().bind(Bindings.isEmpty(theirIPaddress
            .textProperty()));
        joinIPGame.setOnMouseClicked(makeJoinListener(theirIPaddress));

        hRoot1.getChildren().addAll(resetHumanGM, resetComputerGM,
            sideStatus, state);
        hRoot2.getChildren().addAll(hostingNetWorkGame, ipText,
            theirIPaddress, joinIPGame);

        root.getChildren().addAll(pane, hRoot1, hRoot2);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Welcome to JavaFX!");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }




    private EventHandler<? super MouseEvent> makeHostListener() {
        return event -> {
            board.reset(new NetworkedChessController());
        };
    }

    private EventHandler<? super MouseEvent> makeJoinListener(TextField input) {
        return event -> {
            try {
                InetAddress addr = InetAddress.getByName(input.getText());
                GameController newController
                    = new NetworkedChessController(addr);
                board.reset(newController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }


    public static void main(String[] args) {
        launch(args);
    }
}
