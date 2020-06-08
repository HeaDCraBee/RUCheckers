
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.Main;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.opentest4j.AssertionFailedError;
import view.Checker;

import static model.Model.board;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Tests extends Application {

    Main n = new Main();

    @BeforeAll
    public void start(Stage primaryStage){
        Scene scene = new Scene(n.createScene());
        primaryStage.setTitle("RUCheckers");
        primaryStage.setScene(scene);
        primaryStage.show();
        checkBoard();
        primaryStage.close();
    }

    @Test
    public void checkBoard(){
        int x = 0;
        int y = 0;
        try {
            y = 1;
            assertTrue(board[0][1].hasChecker());
            x = 4;
            y = 4;
            assertTrue(board[4][4].hasChecker());
        }
        catch (AssertionFailedError e){
            System.err.println("There are no checkers on the field [" + x + "][" + y + "]");
        }
    }




}
