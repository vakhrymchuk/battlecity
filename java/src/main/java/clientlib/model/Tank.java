package clientlib.model;

import clientlib.Direction;

public class Tank extends Point {

    private Direction direction;
    private int fireCoolDown;
    private int killedCounter;
    private boolean alive;

    public Tank(int x, int y, Direction direction, int fireCoolDown, int killedCounter, boolean alive) {
        super(x, y);
        this.direction = direction;
        this.fireCoolDown = fireCoolDown;
        this.killedCounter = killedCounter;
        this.alive = alive;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getFireCoolDown() {
        return fireCoolDown;
    }

    public int getKilledCounter() {
        return killedCounter;
    }

    public boolean isAlive() {
        return alive;
    }
}
