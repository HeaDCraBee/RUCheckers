package logic;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static logic.GameController.turn;


public class ModalWindow {

    //���� ������������� ����� ��� ������
    public static void modalWindow(String text, boolean draw) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(200, 90);

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);

        Label message = new Label(text);
        message.setFont(Font.font(20));

        Button buttonY = new Button("YES");
        buttonY.setLayoutX(40);
        buttonY.setLayoutY(30);
        buttonY.setFont(Font.font(20));
        buttonY.setOnMouseClicked(event -> {
            if (draw) modalWindowResult(true);
            else
                modalWindowResult(false);
        });

        Button buttonN = new Button("NO");
        buttonN.setOnMouseClicked(event -> window.close());
        buttonN.setLayoutX(110);
        buttonN.setLayoutY(30);
        buttonN.setFont(Font.font(20));

        root.getChildren().addAll(message, buttonN, buttonY);


        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setTitle("RUCheckers");
        window.show();
    }

    //���������� ������ ��� ����������
    public static void modalWindowResult(boolean toDraw) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);

        AnchorPane root = new AnchorPane();
        root.setPrefSize(200, 140);

        Label label = new Label("Winner");
        label.setFont(Font.font(20));
        label.setLayoutX(70);

        Label winner = new Label(turn ? "Black" : "White");
        winner.setFont(Font.font(40));
        winner.setLayoutX(60);
        winner.setLayoutY(30);

        Label draw = new Label("DRAW");
        draw.setFont(Font.font(40));
        draw.setLayoutX(42);
        draw.setLayoutY(30);


        Button exit = new Button("OK");
        exit.setFont(Font.font(25));
        exit.setLayoutY(90);
        exit.setLayoutX(70);
        exit.setOnMouseClicked(event -> System.exit(0));

        root.getChildren().addAll(label, exit);
        root.getChildren().add(toDraw? draw : winner);

        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setTitle("RUCheckers");
        window.show();
    }

}
