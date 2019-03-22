package swing.scc110cw1;

import javax.swing.*;

/**
 * {@link swing.scc110cw1.SquareValue SquareValue} offers a selection of valid values for a {@link swing.scc110cw1.Square Square}, such as:
 * NONE, WHITE, RED
 *
 * It also stores the image URL of each value.
 */
public enum SquareValue {
    FIELD_WHITE("empty.png", null),
    FIELD_BLACK("empty2.png", null),
    D_FIGURE_WHITE("white.png", PlayerColor.WHITE),
    D_FIGURE_RED("red.png", PlayerColor.RED),
    K_FIGURE_WHITE("white-king.png", PlayerColor.WHITE),
    K_FIGURE_RED("red-king.png", PlayerColor.RED),
    SELECTED("selected.png", null); // Used as an indicator for move suggestions

    private final ImageIcon image;
    private final PlayerColor player;

    SquareValue(String imageURL, PlayerColor player) 
    {
        this.image = new ImageIcon(imageURL);
        this.player = player;
    }

    public ImageIcon getImage() 
    {
        return this.image;
    }

    public PlayerColor getPlayer() 
    {
        return this.player;
    }
}

