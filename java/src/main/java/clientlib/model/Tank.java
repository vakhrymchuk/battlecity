package clientlib.model;

import clientlib.action.Action;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Tank extends Point {

    private Direction direction;
    private int fireCoolDown;
    private int killedCounter;
    private boolean alive;

    private Action nextAction;

//    @Deprecated
    private float fakeProbability = 1.0f;
    private TankStrategy strategy;
    private int samePlaceCounter = 0;

    public Tank(int x, int y, Direction direction, int fireCoolDown, int killedCounter, boolean alive) {
        super(x, y);
        this.direction = direction;
        this.fireCoolDown = fireCoolDown;
        this.killedCounter = killedCounter;
        this.alive = alive;
    }

    public void decFireCoolDown() {
        if (fireCoolDown > 0) {
            fireCoolDown--;
        }
    }

    public void moveToPoint(Point pointNew) {
        x = pointNew.getX();
        y = pointNew.getY();
    }

    public void incFakeProbability(float k) {
        fakeProbability *= k;
    }


}
