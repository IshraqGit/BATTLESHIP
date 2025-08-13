package battleship;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.event.ActionEvent;

public class GameController {
    private enum Phase {
        DEPLOY_P1, DEPLOY_P2, GAMEPLAY
    }

    private Phase currentPhase = Phase.DEPLOY_P1;
    @FXML
    private GridPane playerGrid;
    @FXML
    private GridPane opponentGrid;
    @FXML
    private Label statusLabel;
    @FXML
    private Button rotateButton;
    @FXML
    private Button clearPreviewButton;
    @FXML
    private TextField player1NameField;
    @FXML
    private TextField player2NameField;
    @FXML
    private Button startButton;
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;

    private Game game;
    private boolean placingPhase = false;
    private int currentPieceIndex = 0;
    private Piece[] piecesToPlace;
    private boolean isHorizontal = true;
    private int currentPlacingPlayer = 1;
    private Player[] players;

    @FXML
    private void clearPreview() {
        for (Node node : playerGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (!btn.getStyle().contains("gray")) {
                    btn.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                }
            }
        }
    }

    @FXML
    private void updateShipPreview() {
        // No hover preview needed
    }

    // Neon preview for ship placement
    private void showNeonPreview(int row, int col) {
        showNeonPreview(playerGrid, row, col);
    }

    private void showNeonPreview(GridPane grid, int row, int col) {
        if (currentPhase == Phase.GAMEPLAY || currentPieceIndex >= piecesToPlace.length)
            return;
        Piece piece = piecesToPlace[currentPieceIndex];
        boolean canPlace = true;
        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            if (r >= 9 || c >= 9) {
                canPlace = false;
                break;
            }
            Button btn = getButtonAt(grid, r, c);
            if (btn != null && btn.getStyle().contains("gray")) {
                canPlace = false;
                break;
            }
        }
        String previewColor = canPlace ? "#39ff14" : "#ff00cc"; // Neon green or neon pink
        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            if (r < 9 && c < 9) {
                Button btn = getButtonAt(grid, r, c);
                if (btn != null && !btn.getStyle().contains("gray")) {
                    btn.setStyle("-fx-background-color: " + previewColor
                            + "; -fx-border-color: #fff700; -fx-effect: dropshadow(gaussian, #39ff14, 10, 0.7, 0, 0);");
                }
            }
        }
    }

    @FXML
    private void showPreview(int row, int col) {
        if (!placingPhase || currentPieceIndex >= piecesToPlace.length)
            return;

        Piece piece = piecesToPlace[currentPieceIndex];
        boolean canPlace = true;

        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;

            if (r >= 9 || c >= 9) {
                canPlace = false;
                break;
            }

            Button btn = getButtonAt(playerGrid, r, c);
            if (btn != null && btn.getStyle().contains("gray")) {
                canPlace = false;
                break;
            }
        }

        String previewColor = canPlace ? "lightgreen" : "pink";
        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;

            if (r < 9 && c < 9) {
                Button btn = getButtonAt(playerGrid, r, c);
                if (btn != null && !btn.getStyle().contains("gray")) {
                    btn.setStyle("-fx-background-color: " + previewColor +
                            "; -fx-border-color: black; -fx-background-radius: 0; -fx-border-radius: 0;");
                }
            }
        }
    }

    @FXML
    public void initialize() {
        playerGrid.setDisable(true);
        opponentGrid.setDisable(true);
        rotateButton.setDisable(true);
        clearPreviewButton.setDisable(true);
        setupGrid(playerGrid, true);
        setupGrid(opponentGrid, false);
    }

    @FXML
    private void startGame() {
        String p1Name = player1NameField.getText().trim();
        String p2Name = player2NameField.getText().trim();

        if (p1Name.isEmpty() || p2Name.isEmpty()) {
            statusLabel.setText("Please enter names for both players!");
            return;
        }

        game = new Game(p1Name, p2Name);
        players = new Player[] { game.getCurrentPlayer(), game.getOpponent() };
        piecesToPlace = new Piece[] {
                new Battleship(),
                new Destroyer(), new Destroyer(),
                new Submarine(), new Submarine(), new Submarine()
        };

        player1Label.setText(p1Name + "'s Grid");
        player2Label.setText(p2Name + "'s Grid");

        placingPhase = true;
        playerGrid.setDisable(false);
        rotateButton.setDisable(false);
        clearPreviewButton.setDisable(false);
        player1NameField.setDisable(true);
        player2NameField.setDisable(true);
        startButton.setDisable(true);

        statusLabel.setText(p1Name + ": Place your Battleship (size 3)");

        rotateButton.setOnAction(e -> {
            isHorizontal = !isHorizontal;
            statusLabel.setText("Placing " + (isHorizontal ? "Horizontal" : "Vertical"));
            updateShipPreview();
            // Do NOT hide the grid or change its visibility, just update preview
        });
    }

    private void setupGrid(GridPane grid, boolean isPlayer) {
        grid.getChildren().clear();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                javafx.scene.control.Button btn = new javafx.scene.control.Button();
                btn.setPrefSize(30, 30);
                btn.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                btn.setUserData(new int[] { row, col });
                if (isPlayer) {
                    btn.setOnAction(e -> handlePlayerGridClick(btn));
                } else {
                    btn.setOnAction(e -> handleOpponentGridClick(btn));
                }
                grid.add(btn, col, row);
            }
        }
    }

    private void handlePlayerGridClick(javafx.scene.control.Button btn) {
        if (currentPhase != Phase.DEPLOY_P1)
            return;
        int[] pos = (int[]) btn.getUserData();
        Piece piece = piecesToPlace[currentPieceIndex];
        Player placingPlayer = players[0];
        boolean placed = placingPlayer.placePiece(piece, pos[0], pos[1], isHorizontal);
        if (placed) {
            // Set ship color: red for Player 1
            for (int i = 0; i < piece.getSize(); i++) {
                int r = isHorizontal ? pos[0] : pos[0] + i;
                int c = isHorizontal ? pos[1] + i : pos[1];
                Button b = getButtonAt(playerGrid, r, c);
                b.setStyle("-fx-background-color: #ff4444; -fx-border-color: black;");
            }
            currentPieceIndex++;
            if (currentPieceIndex < piecesToPlace.length) {
                String name = pieceName(piecesToPlace[currentPieceIndex]);
                statusLabel.setText(player1NameField.getText() + ": Place your " + name + " (size "
                        + piecesToPlace[currentPieceIndex].getSize() + ")");
            } else {
                // Hide Player 1 ships for Player 2
                for (Node node : playerGrid.getChildren()) {
                    if (node instanceof Button) {
                        ((Button) node).setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                    }
                }
                currentPhase = Phase.DEPLOY_P2;
                currentPieceIndex = 0;
                piecesToPlace = new Piece[] {
                        new Battleship(),
                        new Destroyer(), new Destroyer(),
                        new Submarine(), new Submarine(), new Submarine()
                };
                playerGrid.setDisable(true);
                opponentGrid.setDisable(false);
                // Set event handlers for opponentGrid for ship placement
                for (Node node : opponentGrid.getChildren()) {
                    if (node instanceof Button) {
                        ((Button) node).setOnAction(e -> handlePlayer2GridClick((Button) node));
                    }
                }
                statusLabel.setText(player2NameField.getText() + ": Place your Battleship (size 3)");
                updateShipPreview();
            }
        } else {
            statusLabel.setText("Invalid placement. Try again.");
        }
    }

    private void handleOpponentGridClick(Button btn) {
        if (placingPhase || game.isGameOver() || btn.getStyle().contains("hit") || btn.getStyle().contains("miss"))
            return;

        int[] pos = (int[]) btn.getUserData();
        boolean hit = game.fire(pos[0], pos[1]);

        Player defender = game.getOpponent();
        GridCell cell = defender.getGrid()[pos[0]][pos[1]];
        if (hit) {
            btn.setText("");
            if (cell.getPiece() instanceof Battleship) {
                btn.setStyle(
                        "-fx-background-color: #ff4444; -fx-border-color: black; -fx-background-radius: 0; -fx-border-radius: 0; hit");
            } else {
                btn.setStyle(
                        "-fx-background-color: #ffffff; -fx-border-color: black; -fx-background-radius: 0; -fx-border-radius: 0; hit");
            }
            playHitAnimation(btn);
            statusLabel.setText("Hit! " +
                    (game.getCurrentPlayer() == players[0] ? player2NameField.getText() : player1NameField.getText()) +
                    "'s turn.");
        } else {
            btn.setText("");
            btn.setStyle(
                    "-fx-background-color: #ffffff; -fx-border-color: black; -fx-background-radius: 0; -fx-border-radius: 0; miss");
            statusLabel.setText("Miss! " +
                    (game.getCurrentPlayer() == players[0] ? player2NameField.getText() : player1NameField.getText()) +
                    "'s turn.");
        }

        if (game.isGameOver()) {
            gameOver();
        } else {
            game.nextTurn();
            switchPlayerView();
        }
    }

    private void playHitAnimation(Button btn) {
        btn.setScaleX(1.2);
        btn.setScaleY(1.2);
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(100),
                        new javafx.animation.KeyValue(btn.scaleXProperty(), 1),
                        new javafx.animation.KeyValue(btn.scaleYProperty(), 1)));
        timeline.play();
    }

    private void switchPlayerView() {
        // Hide both grids briefly
        playerGrid.setVisible(false);
        opponentGrid.setVisible(false);

        // Show appropriate grid after a short delay
        javafx.application.Platform.runLater(() -> {
            playerGrid.setVisible(true);
            opponentGrid.setVisible(true);

            if (game.getCurrentPlayer() == players[0]) {
                playerGrid.toFront();
                player1Label.toFront();
                statusLabel.setText(player1NameField.getText() + "'s turn to fire!");
            } else {
                opponentGrid.toFront();
                player2Label.toFront();
                statusLabel.setText(player2NameField.getText() + "'s turn to fire!");
            }
        });
    }

    private void gameOver() {
        // Clear the screen
        VBox parent = (VBox) statusLabel.getParent();
        parent.getChildren().clear();

        String winner = game.getWinner();
        String winnerText;
        if (winner.equals(player1NameField.getText())) {
            winnerText = "Player 1 (" + winner + ") wins!";
        } else {
            winnerText = "Player 2 (" + winner + ") wins!";
        }

        Label winnerLabel = new Label(winnerText);
        winnerLabel.setStyle(
                "-fx-font-size: 48px; -fx-font-family: 'Brush Script MT', cursive; -fx-text-fill: white; -fx-background-color: linear-gradient(to bottom right, #39ff14, #228B22); -fx-padding: 50px; -fx-alignment: center; -fx-border-color: #006400; -fx-border-width: 5px; -fx-border-radius: 30px; -fx-background-radius: 30px; -fx-effect: dropshadow(gaussian, #006400, 20, 0.7, 0, 0);");
        winnerLabel.setMaxWidth(Double.MAX_VALUE);
        winnerLabel.setAlignment(javafx.geometry.Pos.CENTER);

        parent.getChildren().add(winnerLabel);

        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: #fff700; -fx-text-fill: #222; -fx-padding: 10px 30px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        playAgainBtn.setOnAction(e -> resetGame());
        parent.getChildren().add(playAgainBtn);
    }

    private void resetGame() {
        player1NameField.setDisable(false);
        player2NameField.setDisable(false);
        startButton.setDisable(false);
        playerGrid.setDisable(true);
        opponentGrid.setDisable(true);
        rotateButton.setDisable(true);
        clearPreviewButton.setDisable(true);

        setupGrid(playerGrid, true);
        setupGrid(opponentGrid, false);

        currentPieceIndex = 0;
        currentPlacingPlayer = 1;
        isHorizontal = true;
        placingPhase = false;

        statusLabel.setText("Enter player names and click Start Game");

        VBox parent = (VBox) statusLabel.getParent();
        parent.getChildren().remove(parent.getChildren().size() - 1);
    }

    private javafx.scene.control.Button getButtonAt(GridPane grid, int row, int col) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (javafx.scene.control.Button) node;
            }
        }
        return null;
    }

    private String pieceName(Piece piece) {
        if (piece instanceof Battleship)
            return "Battleship";
        if (piece instanceof Destroyer)
            return "Destroyer";
        if (piece instanceof Submarine)
            return "Submarine";
        return "Ship";
    }

    // Add this new method for Player 2 placement
    private void handlePlayer2GridClick(Button btn) {
        if (currentPhase != Phase.DEPLOY_P2)
            return;
        int[] pos = (int[]) btn.getUserData();
        Piece piece = piecesToPlace[currentPieceIndex];
        Player placingPlayer = players[1];
        boolean placed = placingPlayer.placePiece(piece, pos[0], pos[1], isHorizontal);
        if (placed) {
            // Set ship color: red for Player 2 (same logic as Player 1)
            for (int i = 0; i < piece.getSize(); i++) {
                int r = isHorizontal ? pos[0] : pos[0] + i;
                int c = isHorizontal ? pos[1] + i : pos[1];
                Button b = getButtonAt(opponentGrid, r, c);
                if (b != null) {
                    b.setStyle("-fx-background-color: #ff4444; -fx-border-color: black;");
                }
            }
            currentPieceIndex++;
            if (currentPieceIndex < piecesToPlace.length) {
                String name = pieceName(piecesToPlace[currentPieceIndex]);
                statusLabel.setText(player2NameField.getText() + ": Place your " + name + " (size "
                        + piecesToPlace[currentPieceIndex].getSize() + ")");
                updateShipPreview();
            } else {
                // Hide Player 2 ships for Player 1
                for (Node node : opponentGrid.getChildren()) {
                    if (node instanceof Button) {
                        ((Button) node).setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                    }
                }
                currentPhase = Phase.GAMEPLAY;
                opponentGrid.setDisable(true);
                playerGrid.setDisable(true);
                statusLabel.setText("All pieces placed! " + player1NameField.getText() + "'s turn to attack!");
                enableAttackPhase();
            }
        } else {
            statusLabel.setText("Invalid placement. Try again.");
        }
    }

    // Neon preview for Player 2 grid
    private void showNeonPreviewOpponent(int row, int col) {
        if (!placingPhase || currentPieceIndex >= piecesToPlace.length)
            return;
        Piece piece = piecesToPlace[currentPieceIndex];
        boolean canPlace = true;
        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            if (r >= 9 || c >= 9) {
                canPlace = false;
                break;
            }
            Button btn = getButtonAt(opponentGrid, r, c);
            if (btn != null && btn.getStyle().contains("gray")) {
                canPlace = false;
                break;
            }
        }
        String previewColor = canPlace ? "#39ff14" : "#ff00cc";
        for (int i = 0; i < piece.getSize(); i++) {
            int r = isHorizontal ? row : row + i;
            int c = isHorizontal ? col + i : col;
            if (r < 9 && c < 9) {
                Button btn = getButtonAt(opponentGrid, r, c);
                if (btn != null && !btn.getStyle().contains("gray")) {
                    btn.setStyle("-fx-background-color: " + previewColor
                            + "; -fx-border-color: #fff700; -fx-effect: dropshadow(gaussian, #39ff14, 10, 0.7, 0, 0);");
                }
            }
        }
    }

    private void clearPreviewOpponent() {
        for (Node node : opponentGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (!btn.getStyle().contains("gray")) {
                    btn.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
                }
            }
        }
    }

    // Add this method for attack phase
    private void enableAttackPhase() {
        playerGrid.setDisable(true);
        opponentGrid.setDisable(false);
        for (Node node : opponentGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setOnAction(e -> handleAttack(btn));
            }
        }
    }

    private void handleAttack(Button btn) {
        if (game.isGameOver())
            return;
        int[] pos = (int[]) btn.getUserData();
        Player defender;
        GridCell cell;
        if (game.getCurrentPlayer() == players[0]) {
            defender = players[1];
            cell = defender.getGrid()[pos[0]][pos[1]];
        } else {
            defender = players[0];
            cell = defender.getGrid()[pos[0]][pos[1]];
        }
        if (cell.isHit())
            return;
        cell.hit();
        Button targetBtn;
        if (game.getCurrentPlayer() == players[0]) {
            targetBtn = getButtonAt(opponentGrid, pos[0], pos[1]);
        } else {
            targetBtn = getButtonAt(playerGrid, pos[0], pos[1]);
        }
        if (cell.hasPiece()) {
            targetBtn.setText("");
            targetBtn.setStyle("-fx-background-color: #ff4444; -fx-border-color: black; -fx-background-radius: 50;");
            statusLabel.setText("Hit! "
                    + (game.getCurrentPlayer() == players[0] ? player2NameField.getText() : player1NameField.getText())
                    + "'s turn.");
        } else {
            targetBtn.setText("");
            targetBtn.setStyle("-fx-background-color: #ffffff; -fx-border-color: black; -fx-background-radius: 50;");
            statusLabel.setText("Miss! "
                    + (game.getCurrentPlayer() == players[0] ? player2NameField.getText() : player1NameField.getText())
                    + "'s turn.");
        }
        if (game.isGameOver()) {
            statusLabel.setText("Game Over! " + game.getWinner() + " wins!");
            playerGrid.setDisable(true);
            opponentGrid.setDisable(true);
        } else {
            game.nextTurn();
            swapAttackGrids();
        }
    }

    private void swapAttackGrids() {
        if (game.getCurrentPlayer() == players[0]) {
            playerGrid.setDisable(true);
            opponentGrid.setDisable(false);
            statusLabel.setText(player1NameField.getText() + "'s turn to attack!");
            // Set event handlers for opponentGrid
            for (Node node : opponentGrid.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    btn.setOnAction(e -> handleAttack(btn));
                }
            }
        } else {
            playerGrid.setDisable(false);
            opponentGrid.setDisable(true);
            statusLabel.setText(player2NameField.getText() + "'s turn to attack!");
            // Set event handlers for playerGrid
            for (Node node : playerGrid.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    btn.setOnAction(e -> handleAttack(btn));
                }
            }
        }
    }
}
