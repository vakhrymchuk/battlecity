package clientlib.model;


import clientlib.map.Type;
import lombok.*;

import java.io.Serializable;

import static clientlib.model.Direction.*;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Point implements Serializable {

    protected int x;
    protected int y;
    protected Type type;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public Point diff(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public Point dir(Direction direction) {
        return new Point(x + direction.getDx(), y + direction.getDy());
    }

    public int absSum() {
        return Math.abs(x) + Math.abs(y);
    }

    public int manhattanDistance(Point other) {
        return diff(other).absSum();
    }

    public Direction toBiggestDir() {
        if (Math.abs(x) < Math.abs(y)) {
            return y > 0 ? UP : DOWN;
        }
        return x > 0 ? RIGHT : LEFT;
    }

    public boolean isSamePoint(Point other) {
        return x == other.getX() && y == other.getY();
    }

}
