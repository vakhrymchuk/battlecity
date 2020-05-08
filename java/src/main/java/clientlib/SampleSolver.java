package clientlib;

import clientlib.model.Message;
import clientlib.model.Point;
import clientlib.model.Tank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class SampleSolver extends Solver {

    private Random rnd = new Random();

    public List<Point> getBarriers(Message message) {
        List<Point> barriers = new ArrayList<>();
        barriers.addAll(message.getConstructions());
        barriers.addAll(message.getAiTanks());
        barriers.addAll(message.getEnemies());
        barriers.addAll(message.getBorders());
        return barriers;
    }

    /**
     * manhattan distance used
     */
    public Tank getNearest(List<Tank> tanks) {
        if (tanks == null || tanks.isEmpty()) {
            return null;
        }

        Iterator<Tank> iterator = tanks.iterator();
        Tank closest = iterator.next();

        Tank playerTank = message.getPlayerTank();
        int minDist = closest.manhattanDistance(playerTank);
        while (iterator.hasNext()) {
            Tank current = iterator.next();
            int currentDistance = current.manhattanDistance(playerTank);
            if (currentDistance < minDist) {
                minDist = currentDistance;
                closest = current;
            }
        }
        return closest;
    }


    @Override
    public String move() {
        ActionPrinter.FireOrder fireOrder = null;
        switch (rnd.nextInt(3)) {
            case 1:
                fireOrder = ActionPrinter.FireOrder.FIRE_BEFORE_TURN;
                break;
            case 2:
                fireOrder = ActionPrinter.FireOrder.FIRE_AFTER_TURN;
                break;
            default:
                break;
        }
        List<Tank> allTanks = new ArrayList<>(message.getEnemies());
        allTanks.addAll(message.getAiTanks());
        allTanks = allTanks.stream().filter(Tank::isAlive).collect(Collectors.toList());

        Tank nearestEnemy = getNearest(allTanks);
        Point diff = message.getPlayerTank().diff(nearestEnemy);

        List<Direction> availableDirections = new ArrayList<>();
        if (diff.getX() * Direction.LEFT.getDx() > 0) {
            availableDirections.add(Direction.LEFT);
        } else if (diff.getX() * Direction.RIGHT.getDx() > 0) {
            availableDirections.add(Direction.RIGHT);
        }
        if (diff.getY() * Direction.UP.getDy() > 0) {
            availableDirections.add(Direction.UP);
        } else if (diff.getY() * Direction.DOWN.getDy() > 0) {
            availableDirections.add(Direction.DOWN);
        }
        return ActionPrinter.printAction(fireOrder, availableDirections.get(rnd.nextInt(availableDirections.size())));
    }
}
