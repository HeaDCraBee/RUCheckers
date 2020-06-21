package model;

import javafx.scene.Group;

import view.Tile;

public class Model {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Group tileGroup = new Group();
    private Group checkerGroup = new Group();
    private Tile[][] board = new Tile[WIDTH][HEIGHT];


    public Group getTileGroup(){
        return tileGroup;
    }

    public Group getCheckerGroup() {
        return checkerGroup;
    }

    public Tile[][] getBoard() {
        return board;
    }

}