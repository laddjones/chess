package boardview;

import java.util.List;
import java.util.Map;
import gamecontrol.GameController;
import gamecontrol.GameState;
import gamecontrol.NetworkedChessController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import model.Move;
import model.Piece;
import model.PieceType;
import model.Position;
import model.Side;
import java.util.Set;
import java.util.HashSet;
import javafx.scene.paint.Color;
import model.IllegalMoveException;
import model.chess.ChessUtils;
import java.util.Optional;
import javafx.scene.control.ChoiceDialog;
import java.util.ArrayList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import gamecontrol.ChessController;




/**
 * A class for a view for a chess board. This class must have a reference
 * to a GameController for chess playing chess
 * @author Gustavo
 * @date Oct 20, 2015
 */
public class BoardView {

    /* You may add more instance data if you need it */
    protected GameController controller;
    private GridPane gridPane;
    private Tile[][] tiles;
    private Text sideStatus;
    private Text state;
    private boolean isRotated;
    private int clicker;
    private Tile previousTile;
    private Move moveYouJustMade;

    /**
     * Construct a BoardView with an instance of a GameController
     * and a couple of Text object for displaying info to the user
     * @param controller The controller for the chess game
     * @param state A Text object used to display state to the user
     * @param sideStatus A Text object used to display whose turn it is
     */
    public BoardView(GameController controller, Text state, Text sideStatus) {
        this.controller = controller;
        this.state = state;
        this.sideStatus = sideStatus;
        tiles = new Tile[8][8];
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color : goldenrod;");
        reset(controller);
        this.clicker = 1;
    }

    /**
     * Listener for clicks on a tile
     *
     * @param tile The tile attached to this listener
     * @return The event handler for all tiles.
     */
    private EventHandler<? super MouseEvent> tileListener(Tile tile) {
        return event -> {
            if (controller instanceof NetworkedChessController
                    && controller.getCurrentSide()
                    != ((NetworkedChessController) controller).getLocalSide()) {
                //not your turn!
                return;
            }
            //everytime you click it increments by one
            // Don't change the code above this :)

            if (!(clicker % 2 == 0)) {
                firstClick(tile);
            } else if (clicker % 2 == 0) {
                secondClick(tile);
                clicker++;
            }
        };
    }

    /**
     * Perform the first click functions, like displaying
     * which are the valid moves for the piece you clicked.
     * @param tile The TileView that was clicked
     */
    private void firstClick(Tile tile) {
        // gets set of moves
        // for each move in that set highlight the destination tile
        if (sideReturn(tile.getSymbol()).equals(controller.getCurrentSide().
            toString())) {
            Set<Move> hlMove = new HashSet<>();
            previousTile = tile;
            hlMove = controller.getMovesForPieceAt(tile.getPosition());
            if (!(tile.getSymbol().equals(""))) {
                clicker++;
            }
            for (Move element: hlMove) {
                if (getTileAt(element.getDestination()).getSymbol().equals(""))
                {
                    getTileAt(element.getDestination()).highlight(Color.GREEN);
                } else {
                    getTileAt(element.getDestination()).highlight(Color.RED);
                }
            }
        }
    }

    /**
     * Perform the second click functions, like
     * sending moves to the controller but also
     * checking that the user clicked on a valid position.
     * If they click on the same piece they clicked on for the first click
     * then you should reset to click state back to the first click and clear
     * the highlighting effected on the board.
     *
     * @param tile the TileView at which the second click occurred
     */
    private void secondClick(Tile tile) {
        Set<Move> hlMove = new HashSet<>();
        hlMove = controller.getMovesForPieceAt(previousTile.getPosition());
        for (Move element: hlMove) {
            getTileAt(element.getDestination()).clear();
        }
        if (controller.getSymbolForPieceAt(previousTile.getPosition()) != "") {
            try {
                controller.makeMove(new Move(previousTile.getPosition(),
                    tile.getPosition()));
                controller.endTurn();
                controller.beginTurn();
            } catch (IllegalMoveException e) {
            }
        }
    }

    //returns the sides that a piece is on
    //@param symbol the symbol whose side you want to check
    private String sideReturn(String symbol) {
        if (symbol.equals("")) {
            return "String none";
        }
        if (symbol.equals("♟") || symbol.equals("♜") || symbol.equals("♞")
            || symbol.equals("♝") || symbol.equals("♛") || symbol.equals("♚")) {
            return Side.BLACK.toString();
        } else {
            return Side.WHITE.toString();
        }
    }



    /**
     * This method should be called any time a move is made on the back end.
     * It should update the tiles' highlighting and symbols to reflect the
     * change in the board state.
     *
     * @param moveMade the move to show on the view
     * @param capturedPositions a list of positions where pieces were captured
     *
     */
    public void updateView(Move moveMade, List<Position> capturedPositions) {
        // if you made a move previously
        // get teh start and destination of that move
        // clear them
        //
        for (Position capturedPosition: capturedPositions) {
            getTileAt(capturedPosition).setSymbol("");
        }

        if (moveYouJustMade != null) {
            getTileAt(moveYouJustMade.getStart()).clear();
            getTileAt(moveYouJustMade.getDestination()).clear();
        }

        getTileAt(moveMade.getDestination()).setSymbol(controller.
            getSymbolForPieceAt(moveMade.getDestination()));
        getTileAt(moveMade.getStart()).setSymbol("");
        getTileAt(moveMade.getStart()).highlight(Color.PURPLE);
        getTileAt(moveMade.getDestination()).highlight(Color.PURPLE);
        moveYouJustMade = moveMade;

        //remove the symbol from the tile form where you astarted and
        //add the symbol to new square
    }

    /**
     * Asks the user which PieceType they want to promote to
     * (suggest using Alert). Then it returns the Piecetype user selected.
     *
     * @return  the PieceType that the user wants to promote their piece to
     */
    private PieceType handlePromotion() {
        List<String> choices = new ArrayList<>();
        choices.add("Queen");
        choices.add("Knight");
        choices.add("Rook");
        choices.add("Bishop");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", choices);
        dialog.setTitle("Promotion");
        dialog.setHeaderText("Promote your piece, Good Ladd");
        dialog.setContentText("Choose your peice:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return ChessUtils.getPieceTypeFromString(result.get().
                toUpperCase());
        }
        return null;
    }

    /**
     * Handles a change in the GameState (ie someone in check or stalemate).
     * If the game is over, it should open an Alert and ask to keep
     * playing or exit.
     *
     * @param s The new Game State
     */
    public void handleGameStateChange(GameState s) {
        if (s.isGameOver()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(controller.getCurrentState().toString());
            alert.setContentText("Choose your option.");

            ButtonType buttonTypeOne = new ButtonType("New Game");
            ButtonType buttonTypeTwo = new ButtonType("Leave Game");

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                reset(new ChessController());
            } else if (result.get() == buttonTypeTwo) {
                System.exit(0);
            }
        }
    }

    /**
     * Updates UI that depends upon which Side's turn it is
     *
     * @param s The new Side whose turn it currently is
     */
    public void handleSideChange(Side s) {
        sideStatus.setText(s.toString() + "'s turn ----- ");
        state.setText(controller.getCurrentState().toString());
    }

    /**
     * Resets this BoardView with a new controller.
     * This moves the chess pieces back to their original configuration
     * and calls startGame() at the end of the method
     * @param newController The new controller for this BoardView
     */
    public void reset(GameController newController) {
        if (controller instanceof NetworkedChessController) {
            ((NetworkedChessController) controller).close();
        }
        controller = newController;
        isRotated = false;
        if (controller instanceof NetworkedChessController) {
            Side mySide
                = ((NetworkedChessController) controller).getLocalSide();
            if (mySide == Side.BLACK) {
                isRotated = true;
            }
        }
        sideStatus.setText(controller.getCurrentSide() + "'s Turn");

        // controller event handlers
        // We must force all of these to run on the UI thread
        controller.addMoveListener(
                (Move move, List<Position> capturePositions) ->
                Platform.runLater(
                    () -> updateView(move, capturePositions)));

        controller.addCurrentSideListener(
                (Side side) -> Platform.runLater(
                    () -> handleSideChange(side)));

        controller.addGameStateChangeListener(
                (GameState state) -> Platform.runLater(
                    () -> handleGameStateChange(state)));

        controller.setPromotionListener(() -> handlePromotion());


        addPieces();
        controller.startGame();
        if (isRotated) {
            setBoardRotation(180);
        } else {
            setBoardRotation(0);
        }
    }

    /**
     * Initializes the gridPane object with the pieces from the GameController.
     * This method should only be called once before starting the game.
     */
    private void addPieces() {
        gridPane.getChildren().clear();
        Map<Piece, Position> pieces = controller.getAllActivePiecesPositions();
        /* Add the tiles */
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = new TileView(new Position(row, col));
                gridPane.add(tile.getRootNode(),
                        1 + tile.getPosition().getCol(),
                        1 + tile.getPosition().getRow());
                GridPane.setHgrow(tile.getRootNode(), Priority.ALWAYS);
                GridPane.setVgrow(tile.getRootNode(), Priority.ALWAYS);
                getTiles()[row][col] = tile;
                tile.getRootNode().setOnMouseClicked(
                        tileListener(tile));
                tile.clear();
                tile.setSymbol("");
            }
        }
        /* Add the pieces */
        for (Piece p : pieces.keySet()) {
            Position placeAt = pieces.get(p);
            getTileAt(placeAt).setSymbol(p.getType().getSymbol(p.getSide()));
        }
        /* Add the coordinates around the perimeter */
        for (int i = 1; i <= 8; i++) {
            Text coord1 = new Text((char) (64 + i) + "");
            GridPane.setHalignment(coord1, HPos.CENTER);
            gridPane.add(coord1, i, 0);

            Text coord2 = new Text((char) (64 + i) + "");
            GridPane.setHalignment(coord2, HPos.CENTER);
            gridPane.add(coord2, i, 9);

            Text coord3 = new Text(9 - i + "");
            GridPane.setHalignment(coord3, HPos.CENTER);
            gridPane.add(coord3, 0, i);

            Text coord4 = new Text(9 - i + "");
            GridPane.setHalignment(coord4, HPos.CENTER);
            gridPane.add(coord4, 9, i);
        }
    }

    private void setBoardRotation(int degrees) {
        gridPane.setRotate(degrees);
        for (Node n : gridPane.getChildren()) {
            n.setRotate(degrees);
        }
    }

    /**
     * Gets the view to add to the scene graph
     * @return A pane that is the node for the chess board
     */
    public Pane getView() {
        return gridPane;
    }

    /**
     * Gets the tiles that belong to this board view
     * @return A 2d array of TileView objects
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    private Tile getTileAt(int row, int col) {
        return getTiles()[row][col];
    }

    private Tile getTileAt(Position p) {
        return getTileAt(p.getRow(), p.getCol());
    }

}
