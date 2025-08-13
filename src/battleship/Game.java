package battleship;

public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player opponent;
    private boolean isSetupPhase;

    public Game(String name1, String name2) {
        player1 = new Player(name1);
        player2 = new Player(name2);
        currentPlayer = player1;
        opponent = player2;
        isSetupPhase = true;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponent() {
        return opponent;
    }

    public boolean isSetupPhase() {
        return isSetupPhase;
    }

    public void nextTurn() {
        Player temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;
    }

    public boolean fire(int row, int col) {
        GridCell cell = opponent.getGrid()[row][col];
        if (cell.isHit()) {
            return false; // Already hit this cell
        }
        cell.hit();
        return cell.hasPiece(); // Return true only if we hit a ship
    }

    public boolean isGameOver() {
        return player1.allPiecesDestroyed() || player2.allPiecesDestroyed();
    }

    public String getWinner() {
        if (player1.allPiecesDestroyed())
            return player2.getName();
        if (player2.allPiecesDestroyed())
            return player1.getName();
        return null;
    }
}
