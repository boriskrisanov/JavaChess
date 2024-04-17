package io.github.boriskrisanov.javachess.ui;

import io.github.boriskrisanov.javachess.engine.board.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;


public class BoardController {
    @FXML
    GridPane board;

    @FXML
    VBox vbox;

    private final Color whiteSquareColor = Color.valueOf("#f0d9b5");
    private final Color blackSquareColor = Color.valueOf("#b58863");
    private final Color selectedSquareColor = Color.valueOf("#19fc05");
    private double mouseDeltaX = 0;
    private double mouseDeltaY = 0;

    @FXML
    public void initialize() {
        Board board1 = new Board();
        var stackPanes = new ArrayList<Pane>(64);
        for (int i = 0; i < 64; i++) {
            stackPanes.add(new StackPane());
        }
        vbox.setBackground(new Background(new BackgroundFill(new Color(44.0 / 255, 44.0 / 255, 44.0 / 255, 1), null, null)));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int index = j * 8 + i;
                var stackPane = new Pane(new Rectangle(70, 70, (i + j) % 2 != 0 ? blackSquareColor : whiteSquareColor));
                board.add(stackPane, i, j);
                stackPane.setMaxHeight(70);
                stackPane.setMaxWidth(70);
                stackPanes.set(index, stackPane);
            }
        }

        for (int i = 0; i < 64; i++) {
            if (board1.getPieceOn(i) == null) {
                continue;
            }
            String pieceChar = String.valueOf(board1.getPieceOn(i).getChar());
            if (Character.isUpperCase(pieceChar.charAt(0))) {
                pieceChar = '_' + pieceChar.toLowerCase();
            }

            var imageView = new ImageView(new Image(getClass().getResource("/pieces/" + pieceChar + ".png").toString()));

            imageView.setOnMousePressed(e -> {
                mouseDeltaX = imageView.getTranslateX() - e.getSceneX();
                mouseDeltaY = imageView.getTranslateY() - e.getSceneY();
            });
            imageView.setOnMouseDragged(e -> {
                imageView.setTranslateX(e.getSceneX() + mouseDeltaX);
                imageView.setTranslateY(e.getSceneY() + mouseDeltaY);
            });
            imageView.setOnMouseReleased(e -> {
                for (Pane pane : stackPanes) {
                    if (pane.getBoundsInParent().contains(e.getScreenX() - 70, e.getScreenY() - 70)) {
                        ((Rectangle) pane.getChildren().get(0)).setFill(selectedSquareColor);
                    }
                }
            });


            imageView.setFitWidth(70);
            imageView.setFitHeight(70);
            stackPanes.get(i).getChildren().add(imageView);
        }
    }
}
