package model;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import view.Tile;

public class Model {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    public static Tile[][] board = new Tile[WIDTH][HEIGHT];

    public static Group tileGroup = new Group();
    public static Group checkerGroup = new Group();
    public static Label player = new Label("WHITE");
    public static Button draw = new Button("Draw");
    public static Button giveUp = new Button("Give Up");

}