package logic;


import model.MoveType;
import model.CheckerType;
import view.Checker;



import static logic.ModalWindow.modalWindowResult;
import static model.Model.*;

class GameController {

    public static boolean turn = true;
    private static int xKilled;
    private static int yKilled;


    //адаптация координат
    private static int onBoard(double size) {
        return (int) (size + TILE_SIZE / 2) / TILE_SIZE;
    }

    // Постановка и перестановка шашек
    public static Checker makeChecker(CheckerType type, int x, int y) {
        Checker checker = new Checker(type, x, y);

        checker.setOnMouseReleased(e -> {
            int newX = onBoard(checker.getLayoutX());
            int newY = onBoard(checker.getLayoutY());
            MoveResult result;

            if (newX < 0 || newY < 0 || newX > 8 || newY > 8) {
                result = new MoveResult(MoveType.NONE);
            } else
                result = tryMove(checker, newX, newY);

            int x0 = onBoard(checker.getOldX());
            int y0 = onBoard(checker.getOldY());

            // При постановке шашки до дамочного поля
            if (newY < 7 && newY > 0) {
                switch (result.getType()) {
                    case NONE:
                        checker.abortMove();
                        break;
                    case NORMAL:
                        checker.move(newX, newY);
                        board[x0][y0].setChecker(null);
                        board[newX][newY].setChecker(checker);
                        turn = !turn;
                        player.setText((turn) ? "WHITE" : "BLACK");
                        break;
                    case KILL:
                        checker.move(newX, newY);
                        board[x0][y0].setChecker(null);
                        board[newX][newY].setChecker(checker);
                        Checker otherChecker = board[xKilled][yKilled].getChecker();
                        board[onBoard(otherChecker.getOldX())][onBoard(otherChecker.getOldY())].setChecker(null);
                        checkerGroup.getChildren().remove(otherChecker);
                        if (checkForKill(checker, newX, newY) == 0) {
                            turn = !turn;
                            player.setText((turn) ? "WHITE" : "BLACK");
                        }
                        break;
                }
            } else
                // При постановке на дамочное поле
                if (newY == 7  || newY == 0 ) {
                    switch (result.getType()) {
                        case NONE:
                            checker.abortMove();
                            break;
                        case NORMAL:
                            checker.move(newX, newY);
                            board[x0][y0].setChecker(null);
                            if (checker.getType() == CheckerType.WHITE && newY == 0) {
                                checker.stroke();
                                checker.setType(CheckerType.WHITESTAIN);
                            }
                            if (checker.getType() == CheckerType.BLACK && newY == 7) {
                                checker.stroke();
                                checker.setType(CheckerType.BLACKSTAIN);
                            }
                            board[newX][newY].setChecker(checker);
                            turn = !turn;
                            player.setText((turn) ? "WHITE" : "BLACK");
                            break;
                        case KILL:
                            checker.move(newX, newY);
                            board[x0][y0].setChecker(null);
                            if (checker.getType() == CheckerType.WHITE) {
                                checker.stroke();
                                checker.setType(CheckerType.WHITESTAIN);
                            }
                            if (checker.getType() == CheckerType.BLACK) {
                                checker.stroke();
                                checker.setType(CheckerType.BLACKSTAIN);
                            }
                            board[newX][newY].setChecker(checker);
                            Checker otherChecker = board[xKilled][yKilled].getChecker();
                            board[onBoard(otherChecker.getOldX())][onBoard(otherChecker.getOldY())].setChecker(null);
                            checkerGroup.getChildren().remove(otherChecker);
                            if (checkForKill(checker, newX, newY) == 0) {
                                turn = !turn;
                                player.setText((turn) ? "WHITE" : "BLACK");
                            }
                            break;
                    }
                }
            // Проверка на выполнение условия победы
            checkWin();
        });
        return checker;
    }

    //проверка хода
    private static MoveResult tryMove(Checker checker, int newX, int newY) {
        int x0 = onBoard(checker.getOldX());
        int y0 = onBoard(checker.getOldY());
        System.out.println(x0 );
        System.out.println(y0);
        System.out.println(newX);
        System.out.println(newY);

        if (turn) {
            if (checker.getType() == CheckerType.BLACK || checker.getType() == CheckerType.BLACKSTAIN)
                return new MoveResult(MoveType.NONE);
        } else {
            if (checker.getType() == CheckerType.WHITE || checker.getType() == CheckerType.WHITESTAIN)
                return new MoveResult(MoveType.NONE);
        }


        if (checkForKill(checker, newX, newY) == 1)
            return new MoveResult(MoveType.KILL);
        else if (checkForKill(checker, newX, newY) == 2)
            return new MoveResult(MoveType.NONE);
        else if (checkAllForKill(checker))
            return new MoveResult(MoveType.NONE);
        else {

            if (checker.getType() == CheckerType.BLACK) {
                if (Math.abs(newX - x0) != 1 || newY < y0)
                    return new MoveResult(MoveType.NONE);
            } else if (checker.getType() == CheckerType.WHITE) {
                if (Math.abs(newX - x0) != 1 || newY > y0)
                    return new MoveResult(MoveType.NONE);
            } if (board[newX][newY].hasChecker())
                return new MoveResult(MoveType.NONE);

            else {
                final double hypot = Math.hypot(newX - x0, newY - y0);
                if (Math.abs(newX - x0) / hypot != Math.abs(newY - y0) / hypot)
                    return new MoveResult(MoveType.NONE);
                else {
                    return new MoveResult(MoveType.NORMAL);
                }

            }

        }
    }

    // проверка на возможность убийства
    private static byte checkForKill(Checker checker, int newX, int newY) {
        int x0 = onBoard(checker.getOldX());
        int y0 = onBoard(checker.getOldY());
        boolean isMustKill = false;
        boolean isTryKill = false;

        //Проверка диагоналей для обычных шашек
        if (checker.getType() == CheckerType.BLACK || checker.getType() == CheckerType.WHITE) {
            if (checkChecker(x0 - 2, y0 - 2)) {
                //Левая-верхняя
                if (!board[x0 - 2][y0 - 2].hasChecker() &&
                        board[x0 - 1][y0 - 1].hasChecker() && checkCheckersType(x0 - 1, y0 - 1)) {
                    isMustKill = true;
                    if (newX == x0 - 2 && newY == y0 - 2) {
                        isTryKill = true;
                        xKilled = x0 - 1;
                        yKilled = y0 - 1;
                    }
                }
            }

            if (checkChecker(x0 - 2, y0 + 2)) {
                //Левая-нижняя
                if (!board[x0 - 2][y0 + 2].hasChecker() &&
                        board[x0 - 1][y0 + 1].hasChecker() && checkCheckersType(x0 - 1, y0 + 1)) {
                    isMustKill = true;
                    if (newX == x0 - 2 && newY == y0 + 2) {
                        isTryKill = true;
                        xKilled = x0 - 1;
                        yKilled = y0 + 1;
                    }
                }
            }

            if (checkChecker(x0 + 2, y0 - 2)) {
                //Правая-верхняя
                if (!board[x0 + 2][y0 - 2].hasChecker() &&
                        board[x0 + 1][y0 - 1].hasChecker() && checkCheckersType(x0 + 1, y0 - 1)) {
                    isMustKill = true;
                    if (newX == x0 + 2 && newY == y0 - 2) {
                        isTryKill = true;
                        xKilled = x0 + 1;
                        yKilled = y0 - 1;
                    }
                }
            }

            if (checkChecker(x0 + 2, y0 + 2)) {
                //Правая-нижняя
                if (!board[x0 + 2][y0 + 2].hasChecker() &&
                        board[x0 + 1][y0 + 1].hasChecker() && checkCheckersType(x0 + 1, y0 + 1)) {
                    isMustKill = true;
                    if (newX == x0 + 2 && newY == y0 + 2) {
                        isTryKill = true;
                        xKilled = x0 + 1;
                        yKilled = y0 + 1;
                    }
                }
            }

        } else {
            //Левая половина для дамки
            int i = x0;
            while (i > 0) {
                if (checkChecker(x0 - i, y0 - i)) {
                    //Сверху
                    if (!board[x0 - i][y0 - i].hasChecker() &&
                            board[x0 - i + 1][y0 - i + 1].hasChecker() && checkCheckersType(x0 - i + 1, y0 - i + 1) &&
                            checkAnotherChecker(x0, y0, x0 - i + 1, false, false)) {
                        isMustKill = true;
                        if (newX <= x0 - i && newY <= y0 - i) {
                            isTryKill = true;
                            xKilled = x0 - i + 1;
                            yKilled = y0 - i + 1;
                        }
                    }
                }

                if (checkChecker(x0 - i, y0 + i)) {
                    //Снизу
                    if (!board[x0 - i][y0 + i].hasChecker() &&
                            board[x0 - i + 1][y0 + i - 1].hasChecker() && checkCheckersType(x0 - i + 1, y0 + i - 1) &&
                            checkAnotherChecker(x0, y0, x0 - i + 1, false, true)) {
                        isMustKill = true;
                        if (newX <= x0 - i && newY >= y0 + i) {
                            isTryKill = true;
                            xKilled = x0 - i + 1;
                            yKilled = y0 + i - 1;
                        }
                    }
                }
                i--;
            }
            //Для правой
            i = 7 - x0;
            while (i > 0) {
                if (checkChecker(x0 + i, y0 + i)) {
                    //Снизу
                    if (!board[x0 + i][y0 + i].hasChecker() &&
                            board[x0 + i - 1][y0 + i - 1].hasChecker() && checkCheckersType(x0 + i - 1, y0 + i - 1) &&
                            checkAnotherChecker(x0, y0, x0 + i - 1, true, true)) {
                        isMustKill = true;
                        if (newX >= x0 + i && newY >= y0 + i) {
                            isTryKill = true;
                            xKilled = x0 + i - 1;
                            yKilled = y0 + i - 1;
                        }
                    }
                }

                if (checkChecker(x0 + i, y0 - i)) {
                    //Сверху
                    if (!board[x0 + i][y0 - i].hasChecker() &&
                            board[x0 + i - 1][y0 - i + 1].hasChecker() && checkCheckersType(x0 + i - 1, y0 - i + 1) &&
                            checkAnotherChecker(x0, y0, x0 + i - 1, true, false)) {
                        isMustKill = true;
                        if (newX >= x0 + i && newY <= y0 - i) {

                            isTryKill = true;
                            xKilled = x0 + i - 1;
                            yKilled = y0 - i + 1;
                        }
                    }
                }
                i--;
            }
        }

        //Пешка бьет
        if (isMustKill && isTryKill)
            return 1;
        //Пешка должна бить
        else if (isMustKill)
            return 2;
        //Пешка не бьет
        return 0;
    }

    //Проверка типа пешки
    private static boolean checkCheckersType(int x, int y) {
        if (turn) {
            return (board[x][y].getChecker().getType() == CheckerType.BLACK || board[x][y].getChecker().getType() == CheckerType.BLACKSTAIN);
        } else
            return (board[x][y].getChecker().getType() == CheckerType.WHITE || board[x][y].getChecker().getType() == CheckerType.WHITESTAIN);
    }


    //Проверяет, есть ли на пути к пешке, другая пешка
    private static boolean checkAnotherChecker(int x0, int y0, int killX, boolean xSign, boolean ySign) {
        int k = 0;
        int j = y0;
        int i = x0;

        if (xSign) {
            while (i < killX) {
                if (board[i][j].hasChecker())
                    k += 1;

                i++;
                if (ySign)
                    j++;
                else
                    j--;
            }
        } else {
            while (i > killX) {
                if (board[i][j].hasChecker())
                    k += 1;

                i--;
                if (ySign)
                    j++;
                else
                    j--;
            }
        }
        return k <= 1;
    }

    //Проверка существования пешки
    private static boolean checkChecker(int x, int y) {
        try {
            return !board[x][y].hasChecker();
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    //Проверяет, может ли другая пешка бить, если ходящая пешка не бьет
    private static boolean checkAllForKill(Checker activeChecker) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (board[i][j].getChecker() != null && board[i][j].getChecker() != activeChecker) {
                    if (board[i][j].getChecker().getType() == activeChecker.getType() && checkForKill(board[i][j].getChecker(), 0, 0) == 2)
                        return true;

                }
            }
        }
        return false;
    }

    //Проверяет условия победы
    private static void checkWin() {
        int i = 0;
        int black = 0;
        int white = 0;
        Checker checker;

        while (i < checkerGroup.getChildren().size()) {
            checker = (Checker) checkerGroup.getChildren().get(i);

            if (checker.getType() == CheckerType.BLACK || checker.getType() == CheckerType.BLACKSTAIN)
                black++;
            else
                white++;
            i++;
        }
        if (black == 0 || white == 0) {
            modalWindowResult(false);
        }
    }
}