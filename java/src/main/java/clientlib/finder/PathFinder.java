package clientlib.finder;

import clientlib.map.BotMap;
import clientlib.map.Type;
import clientlib.model.Direction;
import clientlib.model.Point;
import clientlib.model.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static clientlib.map.Type.AI;
import static clientlib.map.Type.ENEMY;

public class PathFinder {

    private static final Random rnd = new Random();

    private final BotMap botMap;

    List<Point> path = new ArrayList<>();
    List<Point> bestPath = new ArrayList<>();
    boolean tankFound = false;

    public PathFinder(BotMap botMap) {
        this.botMap = botMap;
    }

    public List<Point> findBest(Point tankPoint) {
        path.clear();
        bestPath.clear();
        tankFound = false;

        path.add(tankPoint);
        find(tankPoint);

        return bestPath;
    }

    private void find(Point point) {
        for (Direction direction : Direction.getMoveDirections()) {
            Point p = point.dir(direction);
            if (botMap.isValidPoint(p) && isEnemyOrAi(p) && ((Tank) botMap.getBox(p)).isAlive()) {
                path.add(p);
                if (!tankFound || isShorterPath() || samePathWithBetterPosition()) {
                    bestPath.clear();
                    bestPath.addAll(path);
                }
                path.remove(path.size() - 1);
                tankFound = true;
                return;
            }
        }
        if ((tankFound && path.size() == bestPath.size()) || path.size() > 15) {
            return;
        }

        for (Direction direction : Direction.getMoveDirections()) {
            Point p = point.dir(direction);
            if (botMap.isValidPoint(p) && botMap.isEmptyBox(p) && !path.contains(p)) {
                path.add(p);
                find(p);
                path.remove(path.size() - 1);
            }
        }
    }

    public boolean isEnemyOrAi(Point point) {
        return isBoxHasType(point, ENEMY) || isBoxHasType(point, AI);
    }

    public boolean isBoxHasType(Point point, Type type) {
        Point box = botMap.getBox(point);
        return box != null && box.getType() == type;
    }

    private boolean isShorterPath() {
        return path.size() < bestPath.size();
    }

    private boolean samePathWithBetterPosition() {
        int pathWeight = calcPathWeight(path);
        int bestPathWeight = calcPathWeight(bestPath);
        return path.size() == bestPath.size()
                && (pathWeight < bestPathWeight || (pathWeight == bestPathWeight && rnd.nextBoolean()));
    }

    private int calcPathWeight(List<Point> path) {
        Point first = path.get(1);
        Point finish = path.get(path.size() - 1);
        Point last = finish.diff(first);
        return Math.abs(last.getX() * last.getY());
    }


}
