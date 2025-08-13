package battleship;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Piece> pieces;
    private GridCell[][] grid;
    private String name;

    public Player(String name) {
        this.name = name;
        this.pieces = new ArrayList<>();
        this.grid = new GridCell[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                grid[r][c] = new GridCell(r, c);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public GridCell[][] getGrid() {
        return grid;
    }

    private boolean isValidPlacement(Piece piece, int row, int col, boolean isHorizontal) {
        // Check boundaries
        int endRow = isHorizontal ? row : row + piece.getSize() - 1;
        int endCol = isHorizontal ? col + piece.getSize() - 1 : col;
        if (endRow >= 9 || endCol >= 9)
            return false;

        // Check for overlap and adjacent ships
        for (int r = Math.max(0, row - 1); r <= Math.min(8, endRow + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(8, endCol + 1); c++) {
                if (grid[r][c].hasPiece())
                    return false;
            }
        }
        return true;
    }

    public boolean placePiece(Piece piece, int row, int col, boolean isHorizontal) {
        if (!isValidPlacement(piece, row, col, isHorizontal)) {
            return false;
        }

        piece.setPosition(row, col, isHorizontal);
        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            grid[r][c].setPiece(piece);
        }
        pieces.add(piece);
        return true;
    }

    public boolean allPiecesDestroyed() {
        for (Piece p : pieces) {
            if (!p.isDestroyed())
                return false;
        }
        return true;
    }
}
