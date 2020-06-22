package clientlib.model;

import clientlib.map.Type;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Bullet extends Point {

    private Direction direction;

    public Bullet(int x, int y, Direction direction) {
        super(x, y);
        this.direction = direction;
    }

    public Bullet(int x, int y, Direction direction, Type type) {
        super(x, y);
        this.direction = direction;
        this.type = type;
    }

    public Bullet(int x, int y, Direction direction, Type type, float fakeProbability) {
        super(x, y);
        this.direction = direction;
        this.type = type;
        if (fakeProbability < 0.2) {
            System.out.println("VERY FAKE BULLET!");
        }
//        this.fakeProbability = fakeProbability;
    }

    public Point applyDirK(int k) {
        return new Point(getX() + k * direction.getDx(), getY() + k * direction.getDy());
    }
    public Bullet applyDirBulletK(int k) {
        return new Bullet(getX() + k * direction.getDx(), getY() + k * direction.getDy(), direction, type);
    }

    public void makeMove() {
        x += direction.getDx();
        y += direction.getDy();
    }
}
