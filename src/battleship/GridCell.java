package battleship;

public class GridCell {
    private int row;
    private int col;
    private boolean hasPiece;
    private boolean isHit;
    private Piece piece;

    public GridCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.hasPiece = false;
        this.isHit = false;
    }

    public boolean hasPiece() {
        return hasPiece;
    }

    public boolean isHit() {
        return isHit;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        this.hasPiece = true;
    }

    public void hit() {
        this.isHit = true;
        if (piece != null)
            piece.hit(row, col);
    }
}
