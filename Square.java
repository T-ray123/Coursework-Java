package swing.scc110cw1;

import javax.swing.*;

/**
 * {@link swing.scc110cw1.Square Square} represents a single field on a {@link swing.scc110cw1.Board Board}.
 */
public class Square extends JButton 
{
    private int row;
    private int col;
    private SquareValue value;

    public Square(int row, int col, SquareValue value) 
    {
        this.row = row;
        this.col = col;
        this.setValue(value);
    }

    // This method works with the assumption that an empty field is always white, since the figures can only move diagonally
    public void moveTo(Square otherSquare) 
    {
        otherSquare.setValue(this.value);
        this.setValue(SquareValue.FIELD_WHITE);
    }

    public void setValue(SquareValue value) 
    {
        this.value = value;
        this.setIcon(value.getImage());
    }

    public SquareValue getValue()
     {
        return this.value;
    }
  //Methods for accessing rows and columns
    public int getRow() 
    {
        return this.row;
    }

    public int getCol() 
    {
        return this.col;
    }
}
