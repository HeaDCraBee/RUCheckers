package logic;

import javafx.application.Application;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.CheckerType;
import view.*;
import view.Tile;

import static logic.GameController.makeChecker;
import static model.Model.*;



public class Main extends Application {

    //Построение сцены
    public static Parent createScene() {
        AnchorPane root = new AnchorPane();
        root.setPrefSize((WIDTH) * TILE_SIZE, (HEIGHT) * TILE_SIZE);
        root.getChildren().addAll(tileGroup, checkerGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 1, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);
                Checker checker = null;
                if ((x + y) % 2 == 1) {
                    if (y < 3) {
                        checker = makeChecker(CheckerType.BLACK, x, y);
                    }
                    if (y > 4) {
                        checker = makeChecker(CheckerType.WHITE, x, y);
                    }
                }

                if (checker != null) {
                    tile.setChecker(checker);
                    checkerGroup.getChildren().add(checker);
                }
            }
        }

        Label label = new Label("TURN");
        label.setFont(Font.font(40));
        label.setLayoutX(900);
        root.getChildren().add(label);

        player.setLayoutX(870);
        player.setLayoutY(100);
        player.setFont(Font.font(60));
        player.setAlignment(Pos.CENTER);
        root.getChildren().add(player);

        draw.setLayoutX(810);
        draw.setLayoutY(250);
        draw.setFont(Font.font(60));
        draw.setMinSize(292, 10);
        draw.setOnMouseClicked(event -> ModalWindow.modalWindow("Do you agree to a draw?", true));
        root.getChildren().add(draw);

        giveUp.setLayoutX(810);
        giveUp.setLayoutY(400);
        giveUp.setFont(Font.font(60));
        giveUp.setMinWidth(292);
        giveUp.setOnMouseClicked(event -> ModalWindow.modalWindow("Do you agree to give up?", false));
        root.getChildren().add(giveUp);

        return root;
    }

    @Override
    public void start(Stage primaryStage){
        Scene scene = new Scene(createScene());
        primaryStage.setTitle("RUCheckers");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}