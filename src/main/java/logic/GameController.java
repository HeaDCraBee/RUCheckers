package logic;

import javafx.scene.text.Text;
import javafx.util.Pair;
import model.MoveResult;
import model.MoveType;
import model.CheckerType;
import view.Checker;
import view.Tile;
import javafx.scene.Group;

import java.util.ArrayList;

import static view.ModalWindow.modalWindowResult;
import static model.Model.*;

public class GameController {

    public boolean turn = true;
    private int xKilled;
    private int yKilled;
    private int turnsWithStainsB = 0;
    private int turnsWithStainsW = 0;
    private int turnsWith3checkersB = 0;
    private int turnsWith3checkersW = 0;
    private int killedBlack = 0;
    private int killedBlackStain = 0;
    private int killedWhite = 0;
    private int killedWhiteStain = 0;
    private Tile[][] board;
    private Group checkerGroup;
    private Text player;
    private Text whiteKilled;
    public Text blackKilled;

    public GameController(Tile[][] board, Group checkerGroup, Text player, Text whiteKilled, Text blackKilled) {
        this.board = board;
        this.checkerGroup = checkerGroup;
        this.player = player;
        this.whiteKilled = whiteKilled;
        this.blackKilled = blackKilled;
    }

    public boolean getTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    //адаптация координат
    private int onBoard(double size) {
        return (int) (size + TILE_SIZE / 2) / TILE_SIZE;
    }

    // Постановка и перестановка шашек
    public Checker makeChecker(CheckerType type, int x, int y) {
        Checker checker = new Checker(type, x, y);
        ArrayList<Checker> killedInRound = new ArrayList<>();


        checker.setOnMouseReleased(e -> {
            int newX = onBoard(checker.getLayoutX());
            int newY = onBoard(checker.getLayoutY());

            MoveResult result;
            if (checker.getLayoutX() > 800 || checker.getLayoutY() > 700) {
                checker.abortMove();
            }
            if (newX < 0 || newY < 0 || newX > 7 || newY > 7) {
                result = new MoveResult(MoveType.NONE);
            } else
                result = tryMove(checker, newX, newY);

            int x0 = onBoard(checker.getOldX());
            int y0 = onBoard(checker.getOldY());
            switch (result.getType()) {
                case NONE:
                    checker.abortMove();
                    break;
                case NORMAL:
                    checker.move(newX, newY);
                    board[x0][y0].setChecker(null);
                    board[newX][newY].setChecker(checker);
                    /*
                     * Проверка на становление дамкой
                     */
                    if (checker.getType() == CheckerType.WHITE && newY == 0) {
                        checker.stroke();// Красная обводка вокруг шашки, смотреть класс view/Checker
                        checker.setType(CheckerType.WHITESTAIN);
                    } else if (checker.getType() == CheckerType.BLACK && newY == 7) {
                        checker.stroke();// Красная обводка вокруг шашки, смотреть класс view/Checker
                        checker.setType(CheckerType.BLACKSTAIN);
                    }
                    checkWin();
                    turn = !turn;// Смена хода
                    player.setText(turn ? "WHITE" : "BLACK");
                    break;
                case KILL:
                    checker.move(newX, newY);
                    board[x0][y0].setChecker(null);
                    board[newX][newY].setChecker(checker);
                    /*
                     * Проверка на становление дамкой
                     */
                    if (checker.getType() == CheckerType.WHITE && newY == 0) {
                        checker.stroke();// Красная обводка вокруг шашки, смотреть класс view/Checker
                        checker.setType(CheckerType.WHITESTAIN);
                    } else if (checker.getType() == CheckerType.BLACK && newY == 7) {
                        checker.stroke();// Красная обводка вокруг шашки, смотреть класс view/Checker
                        checker.setType(CheckerType.BLACKSTAIN);
                    }
                    Checker otherChecker = board[xKilled][yKilled].getChecker();
                    killedInRound.add(otherChecker); // Список со взятыми шашками, нужен чтобы убрать с поля взятые шашки только после серии взятий
                    board[onBoard(otherChecker.getOldX())][onBoard(otherChecker.getOldY())].setChecker(null);
                    switch (otherChecker.getType()) {
                        case WHITE:
                            killedWhite++;
                            break;
                        case WHITESTAIN:
                            killedWhiteStain++;
                            break;
                        case BLACK:
                            killedBlack++;
                            break;
                        case BLACKSTAIN:
                            killedBlackStain++;
                    }
                    /*
                     * Проверка на отсутствие следующего взятия
                     */
                    if (checkForKill(checker, newX, newY) == 0) {
                        /*
                         * Удаление вязтых шашек с доски
                         */
                        for (Checker checker1 : killedInRound) {
                            checkerGroup.getChildren().remove(checker1);
                        }
                        whiteKilled.setText(killedWhite + "/" + killedWhiteStain);
                        blackKilled.setText(killedBlack + "/" + killedBlackStain);
                        checkWin();
                        turn = !turn;// Смена хода
                        player.setText(turn ? "WHITE" : "BLACK");
                    }
                    break;
            }
        });
        return checker;
    }

    //проверка хода
    public MoveResult tryMove(Checker checker, int newX, int newY) {
        int x0 = onBoard(checker.getOldX());
        int y0 = onBoard(checker.getOldY());

        //turn = true - ход белых
        if (turn) {
            if (checker.getType() == CheckerType.BLACK || checker.getType() == CheckerType.BLACKSTAIN)
                return new MoveResult(MoveType.NONE);
        } else {
            if (checker.getType() == CheckerType.WHITE || checker.getType() == CheckerType.WHITESTAIN)
                return new MoveResult(MoveType.NONE);
        }

        /*
         * checkForKill == 1 - Шашка должны бить и бьет
         * 2 - Шашка должны ударить, но не бьет
         */
        if (checkForKill(checker, newX, newY) == 1)
            return new MoveResult(MoveType.KILL);
        else if (checkForKill(checker, newX, newY) == 2)
            return new MoveResult(MoveType.NONE);
        /*
         * Шашка не бьет, но может бить другая шашка
         */
        else if (checkAllForKill(checker)) {
            return new MoveResult(MoveType.NONE);
        } else {
            /*
             * Проверка "дальности" хода обычной шашки
             */
            if (checker.getType() == CheckerType.BLACK) {
                if (Math.abs(newX - x0) != 1 || newY < y0)
                    return new MoveResult(MoveType.NONE);
            } else if (checker.getType() == CheckerType.WHITE) {
                if (Math.abs(newX - x0) != 1 || newY > y0)
                    return new MoveResult(MoveType.NONE);
            }
            /*
             * Есть ли на поле, куда шочет ходить шашка, другая шашка
             */
            if (board[newX][newY].hasChecker())
                return new MoveResult(MoveType.NONE);
            else {
                /*
                 * Диагональный ли ход
                 */
                final double hypot = Math.hypot(newX - x0, newY - y0);
                if (Math.abs(newX - x0) / hypot != Math.abs(newY - y0) / hypot)
                    return new MoveResult(MoveType.NONE);
                else {
                    return new MoveResult(MoveType.NORMAL);
                }
            }
        }
    }

    // Проверка на возможность убийства
    private byte checkForKill(Checker checker, int newX, int newY) {
        int x0 = onBoard(checker.getOldX());
        int y0 = onBoard(checker.getOldY());
        boolean isMustKill = false;
        boolean isTryKill = false;
        /*
         * Проверка диагоналей для обычных шашек
         */
        if (checker.getType() == CheckerType.BLACK || checker.getType() == CheckerType.WHITE) {
            if (x0 - 2 > -1 && y0 - 2 > -1 && !board[x0 - 2][y0 - 2].hasChecker() &&
                    board[x0 - 1][y0 - 1].hasChecker() && checkCheckersType(x0 - 1, y0 - 1)) {//Левая-верхняя
                isMustKill = true;
                if (newX == x0 - 2 && newY == y0 - 2) {
                    isTryKill = true;
                    xKilled = x0 - 1;
                    yKilled = y0 - 1;
                }
            }

            if (x0 - 2 > -1 && y0 + 2 < 8 && !board[x0 - 2][y0 + 2].hasChecker() &&
                    board[x0 - 1][y0 + 1].hasChecker() && checkCheckersType(x0 - 1, y0 + 1)) {//Левая-нижняя
                isMustKill = true;
                if (newX == x0 - 2 && newY == y0 + 2) {
                    isTryKill = true;
                    xKilled = x0 - 1;
                    yKilled = y0 + 1;
                }
            }

            if (x0 + 2 < 8 && y0 - 2 > -1 && !board[x0 + 2][y0 - 2].hasChecker() &&
                    board[x0 + 1][y0 - 1].hasChecker() && checkCheckersType(x0 + 1, y0 - 1)) {//Правая-верхняя
                isMustKill = true;
                if (newX == x0 + 2 && newY == y0 - 2) {
                    isTryKill = true;
                    xKilled = x0 + 1;
                    yKilled = y0 - 1;
                }
            }

            if (x0 + 2 < 8 && y0 + 2 < 8 && !board[x0 + 2][y0 + 2].hasChecker() &&
                    board[x0 + 1][y0 + 1].hasChecker() && checkCheckersType(x0 + 1, y0 + 1)) {//Правая-нижняя
                isMustKill = true;
                if (newX == x0 + 2 && newY == y0 + 2) {
                    isTryKill = true;
                    xKilled = x0 + 1;
                    yKilled = y0 + 1;
                }
            }
        } else {
            /*
             * Проверка диагоналей для дамок
             */
            int i = 1;

            while (i < 8) {
                /*
                 * Слева ... от дамки
                 */
                if (x0 - i > 0 && y0 - i > 0) {
                    //... - Сверху
                    if (board[x0 - i][y0 - i].hasChecker() &&
                            !board[x0 - i - 1][y0 - i - 1].hasChecker() && checkCheckersType(x0 - i, y0 - i)) {
                        isMustKill = true;
                        Pair<Integer, Integer> next = findNextChecker(x0, y0, x0 - i, y0 - i, false, false);
                        if (newX < x0 - i && newY < y0 - i &&
                                ((next.getValue() != -1 && newX > next.getKey() && newY > next.getValue()) ||
                                        (next.getValue() == -1 && newX > -1 && newY > -1))
                        ) {
                            isTryKill = true;
                            xKilled = x0 - i;
                            yKilled = y0 - i;
                        }
                        break;
                    } else if (board[x0 - i][y0 - i].hasChecker() &&
                            board[x0 - i - 1][y0 - i - 1].hasChecker())
                        break;
                }

                if (x0 - i > 0 && y0 + i < 7) {
                    // ... - Снизу
                    if (board[x0 - i][y0 + i].hasChecker() &&
                            !board[x0 - i - 1][y0 + i + 1].hasChecker() && checkCheckersType(x0 - i, y0 + i)) {
                        isMustKill = true;
                        Pair<Integer, Integer> next = findNextChecker(x0, y0, x0 - i, y0 + i, false, true);
                        if (newX < x0 - i && newY > y0 + i &&
                                ((next.getValue() != -1 && newX > next.getKey() && newY < next.getValue()) ||
                                        (next.getValue() == -1 && newX > -1 && newY < 8))) {
                            isTryKill = true;
                            xKilled = x0 - i;
                            yKilled = y0 + i;
                        }
                        break;
                    } else if (board[x0 - i][y0 + i].hasChecker() &&
                            board[x0 - i - 1][y0 + i + 1].hasChecker())
                        break;
                }

                /*
                 * Справа ... от дамки
                 */
                if (x0 + i < 7 && y0 + i < 7) {
                    //... - Снизу
                    if (board[x0 + i][y0 + i].hasChecker() &&
                            !board[x0 + i + 1][y0 + i + 1].hasChecker() && checkCheckersType(x0 + i, y0 + i)) {
                        isMustKill = true;
                        Pair<Integer, Integer> next = findNextChecker(x0, y0, x0 + i, y0 + i, true, true);
                        if (newX > x0 + i && newY > y0 + i &&
                                ((next.getValue() != -1 && newX < next.getKey() && newY < next.getValue())
                                        || next.getValue() == -1 && newX < 8 && newY < 8)) {//Снизу
                            isTryKill = true;
                            xKilled = x0 + i;
                            yKilled = y0 + i;
                        }
                        break;
                    } else if (board[x0 + i][y0 + i].hasChecker() &&
                            board[x0 + i + 1][y0 + i + 1].hasChecker())
                        break;
                }

                if (x0 + i < 7 && y0 - i > 0) {
                    // ... - Сверху
                    if (board[x0 + i][y0 - i].hasChecker() &&
                            !board[x0 + i + 1][y0 - i - 1].hasChecker() && checkCheckersType(x0 + i, y0 - i)) {
                        isMustKill = true;
                        Pair<Integer, Integer> next = findNextChecker(x0, y0, x0 + i, y0 - i, true, false);
                        if (newX > x0 + i && newY < y0 - i &&
                                ((next.getValue() != -1 && newX < next.getKey() && newY > next.getValue()) ||
                                        (next.getValue() == -1 && newX < 8 && newY > -1))) {
                            isTryKill = true;
                            xKilled = x0 + i;
                            yKilled = y0 - i;
                        }
                        break;
                    } else if (board[x0 + i][y0 - i].hasChecker() &&
                            board[x0 + i + 1][y0 - i - 1].hasChecker())
                        break;
                }
                i++;
            }
        }

        //Шашка бьет
        if (isMustKill && isTryKill)
            return 1;
            //Шашка должна бить
        else if (isMustKill)
            return 2;
        //Шашка не бьет
        return 0;
    }

    //Проверка типа Шашки
    private boolean checkCheckersType(int x, int y) {
        if (turn) {
            return (board[x][y].getChecker().getType() == CheckerType.BLACK || board[x][y].getChecker().getType() == CheckerType.BLACKSTAIN);
        } else
            return (board[x][y].getChecker().getType() == CheckerType.WHITE || board[x][y].getChecker().getType() == CheckerType.WHITESTAIN);
    }

    //Проверяет, есть ли на пути после взятой шашки, другая шашка
    private Pair<Integer, Integer> findNextChecker(int x0, int y0, int xForKill, int yForKill, boolean xSign, boolean ySign) {
        int i = 1;
        Pair<Integer, Integer> res = new Pair<>(-1, -1);
        if (xSign) {
            while (x0 + i < xForKill) {
                if (ySign)
                    if (y0 + i < yForKill && board[x0 + i][y0 + i].hasChecker())
                        return new Pair<>(x0 + i, y0 + i);
                    else if (y0 - i > yForKill && board[x0 + i][y0 - i].hasChecker())
                        return new Pair<>(x0 + i, y0 - i);
                i++;
            }
        } else {
            while (x0 - i > xForKill) {
                if (ySign) {
                    if (y0 + i < yForKill && board[x0 - i][y0 + i].hasChecker())
                        return new Pair<>(x0 - i, y0 + i);
                } else {
                    if (y0 - i > -1 && board[x0 - i][y0 - i].hasChecker())
                        return new Pair<>(x0 - i, y0 - i);
                }
                i++;
            }
        }
        return res;
    }

    //Проверяет, может ли другая пешка бить, если ходящая шашка не бьет
    private boolean checkAllForKill(Checker activeChecker) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getChecker() != null && board[i][j].getChecker() != activeChecker) {
                    if ((turn && (board[i][j].getChecker().getType() == CheckerType.WHITE || board[i][j].getChecker().getType() == CheckerType.WHITESTAIN)) ||
                            (!turn && (board[i][j].getChecker().getType() == CheckerType.BLACK || board[i][j].getChecker().getType() == CheckerType.BLACKSTAIN)))
                        if (checkForKill(board[i][j].getChecker(), 0, 0) == 2)
                            return true;

                }
            }
        }
        return false;
    }

    //Проверяет условия победы
    private void checkWin() {
        int i = 0;
        int black = 0;
        int white = 0;
        Checker checker;
        int blackStain = 0;
        int whiteStain = 0;

        while (i < checkerGroup.getChildren().size()) {
            checker = (Checker) checkerGroup.getChildren().get(i);
            if (checker.getType() == CheckerType.BLACKSTAIN)
                blackStain++;
            if (checker.getType() == CheckerType.WHITESTAIN)
                whiteStain++;

            if (checker.getType() == CheckerType.BLACK)
                black++;
            else if (checker.getType() == CheckerType.WHITE)
                white++;
            i++;
        }

        /*
         * Разные условия ничьей
         */
        if (blackStain > 2 && white == 0 && whiteStain == 1 && !turn)
            turnsWithStainsB++;
        else if (whiteStain == 0 || blackStain < 3)
            turnsWithStainsB = 0;

        if (whiteStain > 2 && black == 0 && blackStain == 1 && turn)
            turnsWithStainsW++;
        else if (blackStain == 0 || whiteStain < 3)
            turnsWithStainsW = 0;

        if (((((blackStain == 3 && black == 0) || (blackStain == 2 && black == 1) || (blackStain == 1 && black == 2) || (blackStain == 0 && black == 3)) && whiteStain == 1 && white == 0) && !turn))
            turnsWith3checkersB++;
        else if (black + blackStain != 0 && white + whiteStain != 0)
            turnsWith3checkersB = 0;
        if ((((whiteStain == 3 && white == 0) || (whiteStain == 2 && white == 1) || (whiteStain == 1 && white == 2) || (whiteStain == 0 && white == 3)) && blackStain == 1 && black == 0) && turn)
            turnsWith3checkersW++;
        else if (black + blackStain != 0 && white + whiteStain != 0)
            turnsWith3checkersW = 0;


        if (turnsWithStainsB == 16 || turnsWith3checkersB == 6 || turnsWithStainsW == 16 || turnsWith3checkersW == 6)
            modalWindowResult(true, turn);

        if (black + blackStain == 0 || white + whiteStain == 0) {
            modalWindowResult(false, !turn);
        }
    }
}