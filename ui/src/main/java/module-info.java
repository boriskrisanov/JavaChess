module io.github.boriskrisanov.javachess.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires JavaChess.engine;

    opens io.github.boriskrisanov.javachess.ui to javafx.fxml;
    exports io.github.boriskrisanov.javachess.ui;
}