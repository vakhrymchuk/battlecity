package clientlib.model;

import clientlib.Direction;

public class Bullet extends Point {

    private Direction direction;

    public Bullet(int x, int y, Direction direction) {
        super(x, y);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
