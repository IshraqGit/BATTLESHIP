package battleship;

public abstract class Piece {
    protected int size;
    protected boolean isHorizontal;
    protected int startRow;
    protected int startCol;
    protected boolean[] hits;

    public Piece(int size) {
        this.size = size;
        this.isHorizontal = true;
        this.hits = new boolean[size];
    }

    public void setPosition(int row, int col, boolean isHorizontal) {
        this.startRow = row;
        this.startCol = col;
        this.isHorizontal = isHorizontal;
    }

    public boolean occupies(int row, int col) {
        for (int i = 0; i < size; i++) {
            int r = isHorizontal ? startRow : startRow + i;
            int c = isHorizontal ? startCol + i : startCol;
            if (r == row && c == col)
                return true;
        }
        return false;
    }

    public boolean hit(int row, int col) {
        for (int i = 0; i < size; i++) {
            int r = isHorizontal ? startRow : startRow + i;
            int c = isHorizontal ? startCol + i : startCol;
            if (r == row && c == col) {
                hits[i] = true;
                return true;
            }
        }
        return false;
    }

    public boolean isDestroyed() {
        for (boolean h : hits) {
            if (!h)
                return false;
        }
        return true;
    }

    public int getSize() {
        return size;
    }
}
