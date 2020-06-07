package model;

import view.Checker;

public class MoveResult {

    public MoveType type;

    public MoveType getType() {
        return type;
    }

    public Checker checker;

    public MoveResult(MoveType type) {
        this(type, null);
    }

    MoveResult(MoveType type, Checker checker) {
        this.type = type;
        this.checker = checker;
    }
}