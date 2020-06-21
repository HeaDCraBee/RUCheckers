package view;

import model.CheckerType;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import static model.Model.TILE_SIZE;


public class Checker extends StackPane {
    private CheckerType type;
    private Circle circle = new Circle();

    private double mouseX, mouseY;
    private double oldX, oldY;

    public CheckerType getType() {
        return type;
    }

    public void setType(CheckerType type) {
        this.type = type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }


    public Checker(CheckerType type, int x, int y) {
        this.type = type;
        move(x, y);
        circle.setRadius(37);
        circle.setFill(type == CheckerType.BLACK
                ? Color.valueOf("#a19999") : Color.valueOf("#fff9f4"));
        circle.setStroke(Color.BLACK);


        circle.setStrokeWidth(TILE_SIZE * 0.03);
        circle.setTranslateX((TILE_SIZE - TILE_SIZE * 0.36 * 2) / 2);
        circle.setTranslateY((TILE_SIZE - TILE_SIZE * 0.39 * 2) / 2);
        getChildren().addAll(circle);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }


    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void stroke(){
        circle.setStroke(Color.RED);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }

}
