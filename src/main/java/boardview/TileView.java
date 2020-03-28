package boardview;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import model.Position;
import javafx.scene.text.Font;

/**
 * View class for a tile on a chess board
 * A tile should be able to display a chess piece
 * as well as highlight itself during the game.
 *
 * @author <Yourname here>
 */
public class TileView implements Tile {
    private Position tilePosition;
    private StackPane stackPaneNode;
    private Label label;
    private Rectangle bottomTileColor;
    private Rectangle highlightTile;

    /**
     * Creates a TileView with a specified position
     * @param p
     */
    public TileView(Position p) {
        this.tilePosition = p;
        this.label = new Label("");
        this.stackPaneNode = new StackPane();
        if ((p.getRow() + p.getCol()) % 2 == 0) {
            this.bottomTileColor = new Rectangle(75, 75, Color.GREY);
        } else {
            this.bottomTileColor = new Rectangle(75, 75, Color.WHITE);
        }
        label.setFont(new Font("Arial", 41));
        this.highlightTile = new Rectangle(75, 75, Color.YELLOW);
        highlightTile.setOpacity(0);
        stackPaneNode.getChildren().addAll(bottomTileColor, label,
            highlightTile);

    }

    //gets the postition of a tile
    //@return tilePosition the position of the tile
    @Override
    public Position getPosition() {
        return tilePosition;
    }

    //gets the root node
    //@return stackPaneNode the stack pane that is returned
    @Override
    public Node getRootNode() {
        return stackPaneNode;
    }
    //sets the symbol on the tile
    //@param symbol the symbol that you want to set the tile to have
    @Override
    public void setSymbol(String symbol) {
        label.setText(symbol);
    }

    //gets the symbol of the tile
    //@return label.getText() the String label of the tile
    @Override
    public String getSymbol() {
        return label.getText();
    }

    //highlights a tile by changing opacity
    //@param color takes in the color that you want to highlight the piece
    @Override
    public void highlight(Color color) {
        highlightTile.setFill(color);
        highlightTile.setOpacity(0.5);
    }

    //clears the til e
    @Override
    public void clear() {
        highlightTile.setOpacity(0);

    }
}
