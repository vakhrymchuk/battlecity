package clientlib.model;

import java.util.List;
import java.util.Random;

public enum Direction {
    UP(0, 1), RIGHT(1, 0), DOWN(0, -1), LEFT(-1, 0), STOP(0, 0);

    private final int dx;
    private final int dy;

    private static final Random rnd = new Random();

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Direction reverse() {
        switch (this) {
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case STOP:
                return STOP;
            default:
                return null;
        }
    }

    public static Direction[] getAllDirections() {
        return values();
    }
    public static Direction[] getRandomDirections() {
        Direction[] values = values();
        shuffleArray(values);
        return values;
    }

    public static List<Direction> getMoveDirections() {
        return List.of(UP, RIGHT, DOWN, LEFT);
    }

    public static Direction getRandomMoveDirection() {
        return values()[rnd.nextInt(4)];
    }

    static void shuffleArray(Direction[] ar) {
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Direction a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
