package clientlib.map;

import clientlib.model.Bullet;
import clientlib.model.Point;
import clientlib.model.Tank;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BulletsHolder implements Serializable {

    private final int sizeY, sizeX;
    private final Bullet[][] bullets;

    public BulletsHolder(int sizeY, int sizeX) {
        this.sizeY = sizeY;
        this.sizeX = sizeX;
        bullets = new Bullet[sizeY][sizeX];
    }

    public void putBullets(List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
//            if (hasBullet(bullet))
//                System.out.println("TODO BULLET COLLISION " + bullet + " old bullet " + getBullet(bullet));
            setBullet(bullet);
        }
    }

    public void clear() {
        for (Bullet[] line : bullets) {
            Arrays.fill(line, null);
        }
    }

    public Bullet getBullet(Point point) {
        return bullets[point.getY()][point.getX()];
    }

    public Bullet getBullet(int y, int x) {
        return bullets[y][x];
    }

    public void setBullet(Bullet bullet) {
        bullets[bullet.getY()][bullet.getX()] = bullet;
    }

    public boolean hasNoBullet(Point point) {
        return bullets[point.getY()][point.getX()] == null;
    }

    public boolean hasBullet(Point point) {
        return !hasNoBullet(point);
    }

    public void moveBullet(Bullet bullet) {
        removeBullet(bullet);
        bullet.makeMove();
        if (!isValidPoint(bullet)) return;

        if (hasBullet(bullet)) { // bullet collision
            removeBullet(bullet);
        } else {
            setBullet(bullet);
        }
    }

    public void removeBullet(Point bullet) {
        bullets[bullet.getY()][bullet.getX()] = null;
    }

    public List<Bullet> getAllBullets() {
        return Arrays.stream(bullets)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void addBulletIfValid(Bullet bullet) {
        if (isValidPoint(bullet)) {
            setBullet(bullet);
        }
    }

    private boolean isValidPoint(Point p) {
        return p.getX() > 0 && p.getX() < sizeX && p.getY() >= 0 && p.getY() < sizeY;
    }


    private void addEnemiesBullets(List<Tank> enemies) {
//        for (Tank enemy : enemies) {
//            if (enemy.getFireCoolDown() == 1) {
//                addBox(enemy.dir(LEFT), BULLET);
//                addBox(enemy.dir(RIGHT), BULLET);
//                addBox(enemy.dir(UP), BULLET);
//                addBox(enemy.dir(DOWN), BULLET);
//            }
//            if (enemy.getFireCoolDown() == 0) {
//                addBox(enemy.dir(LEFT).dir(LEFT), BULLET);
//                addBox(enemy.dir(LEFT).dir(LEFT).dir(LEFT), BULLET);
//                addBox(enemy.dir(RIGHT).dir(RIGHT), BULLET);
//                addBox(enemy.dir(RIGHT).dir(RIGHT).dir(RIGHT), BULLET);
//                addBox(enemy.dir(UP).dir(UP), BULLET);
//                addBox(enemy.dir(UP).dir(UP).dir(UP), BULLET);
//                addBox(enemy.dir(DOWN).dir(DOWN), BULLET);
//                addBox(enemy.dir(DOWN).dir(DOWN).dir(DOWN), BULLET);
//
//
//                addBox(enemy.dir(DOWN).dir(LEFT), BULLET);
//                addBox(enemy.dir(DOWN).dir(RIGHT), BULLET);
//                addBox(enemy.dir(UP).dir(LEFT), BULLET);
//                addBox(enemy.dir(UP).dir(RIGHT), BULLET);
//            }
//        }
    }

    private void addBulletsWays(List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            for (int i = 1; i <= 4; i++) {
                Bullet bulletNew = bullet.applyDirBulletK(i);
                if (isValidPoint(bulletNew) && hasNoBullet(bulletNew)) {
                    setBullet(bulletNew);
                }
            }
        }
    }

    public void collapseBulletsReverse(List<Bullet> allBullets) {
        for (int i = 0; i < allBullets.size() - 1; i++) {
            Bullet bullet1 = allBullets.get(i);
            for (int j = i + 1; j < allBullets.size(); j++) {
                Bullet bullet2 = allBullets.get(j);
                if (isBulletCollapse(bullet1, bullet2)) {
//                    System.out.println("Bullets collapse reverse: " + bullet1 + " " + bullet2);
                    allBullets.remove(j);
                    allBullets.remove(i);
                    i--;
                    removeBullet(bullet1);
                    removeBullet(bullet2);
                    break;
                }
            }
        }
    }

    private boolean isBulletCollapse(Bullet b1, Bullet b2) {
        Point b11 = b1.applyDirK(1);
        boolean dirsOpposite = b1.getDirection() == b2.getDirection().reverse();
        return dirsOpposite && b11.isSamePoint(b2);
    }

    public void trim(BulletsHolder source, int leftInc, int bottomInc) {
        for (int y = 1; y < sizeY - 1; y++) {
            System.arraycopy(source.bullets[bottomInc + y], leftInc + 1, bullets[y], 1, sizeX - 2);
        }
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Bullet bullet = getBullet(y, x);
                if (bullet != null) {
                    bullet.setX(bullet.getX() - leftInc);
                    bullet.setY(bullet.getY() - bottomInc);
                }
            }
        }
    }
}
