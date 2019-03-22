package swing.scc110cw1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link swing.scc110cw1.Board Board} contains 64 {@link swing.scc110cw1.Square Square}s aligned in an 8x8 grid.
 *
 * Be aware this class is not only the model representing the Board, but also responsible for handling the window displaying it.
 */
public class Board {

    private final JFrame window;
    private final Square[][] squares = new Square[8][8];

    private PlayerColor currentPlayer = PlayerColor.WHITE;
    private Square selectedSquare = null;

    public Board() {
        // Create the window
        this.window = new JFrame("Checkers");
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create board
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8,8));
        boardPanel.setSize(600,600);

        // Fill board with squares, alternating black and white (with figures)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Create new square
                Square newSquare;
                if ((i + j) % 2 == 0) {
                     newSquare = new Square(i, j, SquareValue.FIELD_BLACK);
                } else {
                    if (i <= 2) newSquare = new Square(i, j, SquareValue.D_FIGURE_RED);
                    else if (i >= 5) newSquare = new Square(i, j, SquareValue.D_FIGURE_WHITE);
                    else newSquare = new Square(i, j, SquareValue.FIELD_WHITE);
                }

                // Add button reaction
                newSquare.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        if (Board.this.selectedSquare == null) 
                        {
                            // Currently no square selected
                            if (currentPlayer == newSquare.getValue().getPlayer()) 
                            {
                                // Current player shares player color with selected square
                                Board.this.selectedSquare = newSquare;
                                for (Square square : Board.this.getValidTargets(newSquare))
                             {
                                    square.setValue(SquareValue.SELECTED);
                                }
                            }
                        } else {
                            // Another square is already selected
                            if (newSquare.getValue() == SquareValue.SELECTED)
                             {
                                // ...and this square is a valid target from that other square
                                Board.this.selectedSquare.moveTo(newSquare);
                                if (Math.abs(newSquare.getRow() - Board.this.selectedSquare.getRow()) == 2) 
                                {
                                    // The difference is 2, meaning this is a capturing move
                                    // The captured figure needs to be removed
                                    int capRow = (selectedSquare.getRow() + newSquare.getRow()) / 2;
                                    int capCol = (selectedSquare.getCol() + newSquare.getCol()) / 2;

                                    Board.this.squares[capRow][capCol].setValue(SquareValue.FIELD_WHITE);
                                }

                                // Checking if the turn will end.
                                Set<Square> captureFollowups = Board.this.getValidCapturingTargets(newSquare);
                                if (!captureFollowups.isEmpty()) {
                                    // In draughts, the piece has to keep attacking if possible
                                    Board.this.clearSelection();
                                    for (Square validFollowup : captureFollowups)
                                     {
                                        validFollowup.setValue(SquareValue.SELECTED); //using the png image named "SELECTED"
                                        Board.this.selectedSquare = newSquare;
                                    }
                                    return;
                                }

                                // End turn
                                if (Board.this.currentPlayer == PlayerColor.WHITE)
                                    Board.this.currentPlayer = PlayerColor.RED;
                                else
                                    Board.this.currentPlayer = PlayerColor.WHITE;

                                // If a piece reaches the end of the board, turn them into kings
                                if (newSquare.getValue() == SquareValue.D_FIGURE_WHITE && newSquare.getRow() == 0)
                                 {
                                    newSquare.setValue(SquareValue.K_FIGURE_WHITE);
                                } 
                                else if (newSquare.getValue() == SquareValue.D_FIGURE_RED && newSquare.getRow() == 7) 
                                {
                                    newSquare.setValue(SquareValue.K_FIGURE_RED);
                                }
                            }
                            Board.this.clearSelection();
                        }
                    }
                });

                // Store button and pass it on to the panel
                this.squares[i][j] = newSquare;
                boardPanel.add(newSquare);
            }
        }

        // Add board
        this.window.getContentPane().add(boardPanel);

        // Display window
        this.window.setSize(600,600); //setting the Size of the window  
        this.window.setResizable(false); //setting resizable !false
        this.window.setVisible(true);   //set visibility
    }

    private void clearSelection() 
    {
        for (int i = 0; i < 8; i++) //loop for rows
         {
            for (int j = 0; j < 8; j++)  //loop for columns
            {
                // Once any action was performed while moves were suggested, the suggestions should disappear(the SELECTED png)
                if (this.squares[i][j].getValue() == SquareValue.SELECTED) 
                {
                    this.squares[i][j].setValue(SquareValue.FIELD_WHITE);
                }
            }
        }
        this.selectedSquare = null;
    }

    public Set<Square> getValidTargets(Square origin)
     {
        Set<Square> validTargets = new HashSet<>();
      //GetValidOrdinaryTargets is responsible for returning all possible values which are an ordinary value
        validTargets.addAll(getValidOrdinaryTargets(origin));
        validTargets.addAll(getValidCapturingTargets(origin));

        return validTargets;
    }
        /*The 1 and 2 are because thats how ordinary and capturing moves work: if you have an ordinary move,
         you move one field, capturing moves move two fields.
         It gets called 4 times because they need to get checked in all 4 diagonal directions */

    private Set<Square> getValidOrdinaryTargets(Square origin)
     {
        Square s1 = getSquareInDir(origin, -1, -1);
        Square s2 = getSquareInDir(origin, -1, 1);
        Square s3 = getSquareInDir(origin, 1, -1);
        Square s4 = getSquareInDir(origin, 1, 1);

        return nullSafeSetFrom(s1, s2, s3, s4);
    }

    private Set<Square> getValidCapturingTargets(Square origin) 
    {
        Square s1 = getSquareInDir(origin, -2, -2);
        Square s2 = getSquareInDir(origin, -2, 2);
        Square s3 = getSquareInDir(origin, 2, -2);
        Square s4 = getSquareInDir(origin, 2, 2);

        return nullSafeSetFrom(s1, s2, s3, s4);
    }

    private Set<Square> nullSafeSetFrom(Square... squares) 
    {
        Set<Square> result = new HashSet<>();

        for (Square square : squares)
            if (square != null)
                result.add(square);

        return result;
    }

    /**
     * Attempts to get a square (following the draughts ruleset) in a certain direction
     *
     * @param origin The origin square
     * @param rowDir The direction (y-axis)
     * @param colDir The direction (x-axis)
     * @return a square that lies in the provided direction if it follows the draughts ruleset
     * Returns null if no square matching these criteria can be found
     */
    public Square getSquareInDir(Square origin, int rowDir, int colDir) 
    {
        int relativeRow = origin.getRow() + rowDir;
        int relativeCol = origin.getCol() + colDir;

        // Fields can't be an origin for players
        if (origin.getValue() == SquareValue.FIELD_BLACK || origin.getValue() == SquareValue.FIELD_WHITE) return null;

        if (relativeRow >= 0 && relativeRow < 8 && relativeCol >= 0 && relativeCol < 8) 
        {
            // The relativeRow and relativeCol indices are within the bounds of the squares array (8x8)

            if ((origin.getValue() == SquareValue.D_FIGURE_WHITE && rowDir > 0) ||
                    (origin.getValue() == SquareValue.D_FIGURE_RED && rowDir < 0)) 
                    {
                // Wrong direction!
                return null;
            }

            if (Math.abs(colDir) == 1 && Math.abs(rowDir) == 1) {
                // It's an ordinary move (col and row difference of 1)
                if (this.squares[relativeRow][relativeCol].getValue() != SquareValue.FIELD_WHITE) 
                {
                    // ...but the target isn't empty

                    return null;
                } 
                else {
                    // ...and the target is empty
                    return this.squares[relativeRow][relativeCol];
                }

            } else if (Math.abs(colDir) == 2 && Math.abs(rowDir) == 2)
             {
                // It's a capturing move (col and row difference of 2)
                if (this.squares[relativeRow][relativeCol].getValue() != SquareValue.FIELD_WHITE) 
                {
                    // ...but the target isn't empty

                    return null;
                } 
                else {
                    // ...and the target is empty
                    if (origin.getValue() == SquareValue.D_FIGURE_WHITE || origin.getValue() == SquareValue.K_FIGURE_WHITE) 
                    {
                        // The moving figure is white => if the captured figure is red, the move is valid
                        if (this.squares[origin.getRow() + rowDir / 2][origin.getCol() + colDir / 2].getValue() == SquareValue.D_FIGURE_RED ||
                                this.squares[origin.getRow() + rowDir / 2][origin.getCol() + colDir / 2].getValue() == SquareValue.K_FIGURE_RED)
                                 {
                            return this.squares[relativeRow][relativeCol];
                        } else return null;
                    } else if (origin.getValue() == SquareValue.D_FIGURE_RED || origin.getValue() == SquareValue.K_FIGURE_RED) 
                    {
                        // The moving figure is red => if the captured figure is white, the move is valid
                        if (this.squares[origin.getRow() + rowDir / 2][origin.getCol() + colDir / 2].getValue() == SquareValue.D_FIGURE_WHITE ||
                                this.squares[origin.getRow() + rowDir / 2][origin.getCol() + colDir / 2].getValue() == SquareValue.K_FIGURE_WHITE) 
                                {
                            return this.squares[relativeRow][relativeCol];
                        } else return null;
                    } 
                    else {
                        // The moving figure is actually an empty field
                        return null;
                    }
                }
            } else {
                // The figure can't move more than 2 cols at a time (or 0 cols)
                return null;
            }
        } else {
            // The figure can't move out of bounds (8x8)
            return null;
        }
    }

    // So that the window can be controlled / the application can be finished externally
    public JFrame getWindow() 
    {
        return this.window;
    }
}
