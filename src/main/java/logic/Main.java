package logic;

import javafx.application.Application;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CheckerType;
import model.Model;
import view.*;
import view.Tile;

import static model.Model.*;


public class Main extends Application {

    private AnchorPane root = new AnchorPane();
    private Text player = new Text();
    private Text whiteKilled = new Text();
    private Text blackKilled = new Text();
    private Model model = new Model();
    private Group tileGroup = model.getTileGroup();
    private Group checkerGroup = model.getCheckerGroup();
    private Tile[][] board = model.getBoard();

    private GameController game = new GameController(board, checkerGroup, player, whiteKilled, blackKilled);

    public GameController getGame(){
        return game;
    }

    public Tile[][] getMainBoard(){
        return board;
    }

    //Построение сцены
    public Parent createScene() {
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
                        checker = game.makeChecker(CheckerType.BLACK, x, y);
                    }
                    if (y > 4) {
                        checker = game.makeChecker(CheckerType.WHITE, x, y);
                    }
                }
                if (checker != null) {
                    tile.setChecker(checker);
                    checkerGroup.getChildren().add(checker);
                }
            }
        }
        return root;
    }



    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createScene());

        Label label = new Label("TURN");
        label.setFont(Font.font(40));
        label.setLayoutX(910);
        root.getChildren().add(label);


        player.setText("WHITE");
        player.setLayoutX(875);
        player.setLayoutY(125);
        player.setFont(Font.font(60));
        root.getChildren().add(player);

        VBox white = new VBox();
        white.relocate(820, 150);
        white.alignmentProperty();
        white.setAlignment(Pos.CENTER);

        Text killLogWhite = new Text("White taken:");
        killLogWhite.setFont(Font.font(50));
        white.getChildren().add(killLogWhite);

        whiteKilled.setText("0/0");
        whiteKilled.setFont(Font.font(40));
        white.getChildren().add(whiteKilled);

        root.getChildren().add(white);

        VBox black = new VBox();
        black.relocate(830, 270);
        black.alignmentProperty();
        black.setAlignment(Pos.CENTER);

        Text killLogBlack = new Text("Black taken:");
        killLogBlack.setFont(Font.font(50));
        black.getChildren().add(killLogBlack);

        blackKilled.setText("0/0");
        blackKilled.setFont(Font.font(40));
        black.getChildren().add(blackKilled);

        root.getChildren().add(black);

        Button draw = new Button("Draw");
        draw.setLayoutX(810);
        draw.setLayoutY(400);
        draw.setFont(Font.font(60));
        draw.setMinSize(292, 10);
        draw.setOnMouseClicked(event -> ModalWindow.modalWindow("Do you agree to a draw?", true, game.getTurn()));
        root.getChildren().add(draw);

        Button giveUp = new Button("Give Up");
        giveUp.setLayoutX(810);
        giveUp.setLayoutY(545);
        giveUp.setFont(Font.font(60));
        giveUp.setMinWidth(292);
        giveUp.setOnMouseClicked(event -> ModalWindow.modalWindow("Do you agree to give up?", false, game.getTurn()));
        root.getChildren().add(giveUp);

        Button exit = new Button("EXIT");
        exit.setLayoutX(810);
        exit.setLayoutY(690);
        exit.setFont(Font.font(50));
        exit.setMinWidth(292);
        exit.setOnMouseClicked(event -> exit());
        root.getChildren().add(exit);

        scene.setRoot(root);
        primaryStage.setTitle("RUCheckers");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void exit(){
        System.exit(0);
    }
}