package clientlib.map;

import clientlib.model.Direction;
import clientlib.model.Point;
import clientlib.model.Tank;
import clientlib.model.TankStrategy;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.*;

@ToString
@AllArgsConstructor
class QueueItem {
    int distance;
    Point point;
}

public class DistanceMapPopulator {

    public static final int UNREACHABLE_POINT = 1_000;
    private final BotMap botMap;
    int[][] distances;

    Queue<QueueItem> queue = new PriorityQueue<>(Comparator.comparingInt(value -> value.distance));

    private DistanceMapPopulator(BotMap botMap) {
        this.botMap = botMap;
        distances = createDefaultScores(botMap.getSizeY(), botMap.getSizeX());
    }

    public static int[][] createDistArray(BotMap botMap, Tank playerTank) {

        int[][] scores = createDistArray(botMap, Type.ENEMY);

        if (scores[playerTank.getY()][playerTank.getX()] == UNREACHABLE_POINT) {
            scores = createDistArray(botMap, Type.AI);
        }

//        show(scores);

        return scores;
    }

    private static int[][] createDistArray(BotMap botMap, Type type) {
        List<Tank> enemies = botMap.getTanksOfType(EnumSet.of(type));
        return new DistanceMapPopulator(botMap).calcForEnemies(enemies);

    }

    private int[][] calcForEnemies(List<Tank> enemies) {
        for (Tank enemy : enemies) {
//            putItem(enemy, 2 * enemy.getKilledCounter());
            if (enemy.getKilledCounter() < 10) {
                int startDist = enemy.getStrategy() == TankStrategy.STAY ? -10 : 0;
                putItem(enemy, startDist);
            }

        }

        while (!queue.isEmpty()) {
            QueueItem queueItem = queue.remove();
            int nextDistance = queueItem.distance + 1;

            for (Direction direction : Direction.getMoveDirections()) {
                Point dir = queueItem.point.dir(direction);
                Point box = botMap.getBox(dir);

                int penalty = getPenalty(nextDistance, box);

                if (penalty < distances[dir.getY()][dir.getX()]) {
                    putItem(dir, penalty);
                }
            }
        }

        return distances;
    }

    private int getPenalty(int nextDistance, Point box) {
        if (box == null) {
            return nextDistance;
        } else if (box.getType() == Type.CONSTRUCTION) {
//            Construction construction = (Construction) box;
//            return nextDistance + 6 * construction.getPower();
            return UNREACHABLE_POINT;
        } else if (box.getType() != Type.BORDER) {
            return nextDistance;
        }
        return UNREACHABLE_POINT;
    }

    private int[][] calcForEnemy(Tank enemy) {
        putItem(enemy, 2 * enemy.getKilledCounter());
        while (!queue.isEmpty()) {
            QueueItem queueItem = queue.remove();
            int nextDistance = queueItem.distance + 1;

            for (Direction direction : Direction.getMoveDirections()) {
                Point dir = queueItem.point.dir(direction);
                if (!botMap.isBoxBorderOrConstruction(dir) && distances[dir.getY()][dir.getX()] == UNREACHABLE_POINT) {
                    putItem(dir, nextDistance);
                }
            }
        }
        return distances;
    }

    private static int[][] createDefaultScores(int sizeY, int sizeX) {
        int[][] scores = new int[sizeY][sizeX];
        for (int[] score : scores) {
            Arrays.fill(score, UNREACHABLE_POINT);
        }
        return scores;
    }

    private void putItem(Point point, int distance) {
        distances[point.getY()][point.getX()] = distance;
        queue.add(new QueueItem(distance, point));
    }

    public static void show(int[][] arr) {

        for (int y = arr.length - 1; y >= 0; y--) {
            int[] line = arr[y];
            for (int i : line) {
                System.out.print(String.format("%6d", i));
            }
            System.out.println();
        }
    }


}
