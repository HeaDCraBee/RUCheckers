package view;

import model.Model;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private Checker checker;

    public boolean hasChecker() {
        return checker != null;
    }

    public Checker getChecker() {
        return checker;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public Tile(boolean light, int x, int y) {
        setWidth(Model.TILE_SIZE);
        setHeight(Model.TILE_SIZE);

        relocate(x * Model.TILE_SIZE, y * Model.TILE_SIZE);

        setFill(light ? Color.valueOf("#141313") : Color.valueOf("#dadfe4"));
    }
}
