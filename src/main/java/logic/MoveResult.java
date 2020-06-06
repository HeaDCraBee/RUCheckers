package logic;

import model.MoveType;
import view.Checker;

public class MoveResult {

    private MoveType type;

    MoveType getType() {
        return type;
    }

    private Checker checker;

    Checker getChecker() {
        return checker;
    }

    MoveResult(MoveType type) {
        this(type, null);
    }

    MoveResult(MoveType type, Checker checker) {
        this.type = type;
        this.checker = checker;
    }
}