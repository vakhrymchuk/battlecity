package clientlib.finder;

import clientlib.action.Action;
import clientlib.map.BotMap;
import clientlib.map.DistanceMapPopulator;
import clientlib.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static clientlib.action.ActionPrinter.FireOrder.FIRE_AFTER_TURN;
import static clientlib.action.ActionPrinter.FireOrder.FIRE_BEFORE_TURN;
import static clientlib.map.Type.AI;
import static clientlib.model.Direction.LEFT;

public class DynPathFinder {

    public static final float NEXT_ROUND_INFLATION = 0.9f;
    private static final Random rnd = new Random();


    private DynPathFinder() {
    }

    public static Action findBest(BotMap botMap, Tank playerTank, int steps) {
//        botMap.setScore(0);

        if (BotMap.distances[playerTank.getY()][playerTank.getX()] >= DistanceMapPopulator.UNREACHABLE_POINT) {
            System.out.println("DID not found enemy");
            return new Action(LEFT, FIRE_BEFORE_TURN);
        }

        return find(botMap, playerTank, steps, null);
    }

    private static Action find(final BotMap botMap, final Tank playerTank, int steps, final Action tryAction) {

        if (!playerTank.isAlive()) {
            System.out.println("Why not ALIVE?? " + playerTank);
        }

        if (tryAction != null) {
//            System.out.println("tryAction = " + tryAction);
            botMap.makeRound();
//            botMap.show();
        }

        int currentScore = calcScore(playerTank, botMap);

        if (steps == 0 || !playerTank.isAlive()) {
            tryAction.setScore(currentScore);
//            System.out.println("return action = " + tryAction);
            return tryAction;
        }

        Action best = getActions(playerTank, botMap)
//                .stream()
                .parallelStream()
                .peek(actionToTry -> checkMyAction(botMap, playerTank, steps, actionToTry))
                .max(Comparator.comparingInt(Action::getScore))
                .get();

        best.setScore((int) (currentScore + best.getScore() * NEXT_ROUND_INFLATION));
        return best;
    }

    private static void checkMyAction(BotMap botMap, Tank playerTank, int steps, Action actionToTry) {

        List<Tank> enemies = getNearEnemies(botMap, playerTank, 5);
        if (enemies.size() > 3) {
            enemies = getNearEnemies(botMap, playerTank, 4);
            if (enemies.size() > 3)
                enemies = getNearEnemies(botMap, playerTank, 3);
        }

        int minScore = Integer.MAX_VALUE;
        for (List<Action> actions : getAllVars(enemies, botMap)) {
            BotMap botMapNew = botMap.copy();
            setNextActions(enemies, actions, botMapNew);
            Tank playerTankNew = (Tank) botMapNew.getBox(playerTank);
            playerTankNew.setNextAction(actionToTry);

            int score = find(botMapNew, playerTankNew, steps - 1, actionToTry)
                    .getScore();
//            System.out.println("actionToTry = " + actionToTry + " actions = " + actions + " score = " + score);
            if (score < minScore || (score == minScore && rnd.nextBoolean())) {
                minScore = score;
                if (minScore < -10_000) break;
            }
        }

        actionToTry.setScore(minScore);
    }

    private static List<Tank> getNearEnemies(BotMap botMap, Tank playerTank, int distance) {
        return botMap.getEnemies()
                    .stream()
                    .filter(tank -> tank.getStrategy() != TankStrategy.STAY)
                    .filter(tank -> tank.manhattanDistance(playerTank) <= distance)
                    .collect(Collectors.toList());
    }

    private static void setNextActions(List<Tank> enemies, List<Action> actions, BotMap botMapNew) {
        for (int i = 0; i < enemies.size(); i++) {
            Tank tankNew = (Tank) botMapNew.getBox(enemies.get(i));
            tankNew.setNextAction(actions.get(i));
        }
    }


    public static List<List<Action>> getAllVars(List<Tank> enemies, BotMap botMap) {

        if (enemies.isEmpty()) {
            return List.of(List.of());
        }
        List<List<Action>> result = new ArrayList<>();
        if (enemies.size() == 1) {
            for (Action action : getActions(enemies.get(0), botMap)) {
                result.add(List.of(action));
            }
            return result;
        }

        Map<Tank, List<Action>> map = enemies.stream()
                .collect(Collectors.toMap(Function.identity(), tank -> getActions(tank, botMap)));

        getAllVarsRec(result, new ArrayList<>(enemies), map, new ArrayList<>());

        return result;
    }

    private static void getAllVarsRec(List<List<Action>> result, List<Tank> enemies, Map<Tank, List<Action>> map, List<Action> currActions) {
        if (enemies.isEmpty()) {
            result.add(new ArrayList<>(currActions));
            return;
        }

        Tank enemy = enemies.remove(0);
        List<Action> actions = map.get(enemy);
        for (Action action : actions) {
            currActions.add(action);
            getAllVarsRec(result, enemies, map, currActions);
            currActions.remove(action);
        }
        enemies.add(enemy);

    }

    private static List<Action> getActions(Tank tank, BotMap botMap) {
        return Arrays.stream(Direction.getAllDirections())
                .flatMap(direction -> createActions(tank, direction, botMap).stream())
                .collect(Collectors.toList());
    }

    private static List<Action> createActions(Tank tank, Direction direction, BotMap botMap) {
        if (botMap.isBoxBorderOrConstruction(tank.dir(direction))) {
            return List.of();
        }
        List<Action> actions = new ArrayList<>(3);
        if (tank.getFireCoolDown() <= 1) {
            actions.add(new Action(direction, FIRE_BEFORE_TURN));
            actions.add(new Action(direction, FIRE_AFTER_TURN));
        }
        actions.add(new Action(direction));
        return actions;
    }

    public static int calcScore(Tank playerTank, BotMap botMap) {
//        int minTankScore = botMap.<Tank>getPointsOfType(ENEMY, AI)
//                .stream()
//                .map(tank -> {
//
//                    int tankTypeScore = tank.getType() == ENEMY ? 1 : 5;
//
//                    int dx = Math.abs(playerTank.getX() - tank.getX());
//                    int dy = Math.abs(playerTank.getY() - tank.getY());
//                    int mx = Math.abs(dx - 2);
//                    int my = Math.abs(dy - 2);
//                    if (dx == 0) {
//                        mx = 0;
//                    }else if (dy == 0) {
//                        my = 0;
//                    }
//                    if (dx < 10 && dy < 10) {
//                        int d = tank.getType() == ENEMY ? mx + my : dx * dy + mx + my;
//                        return d * tankTypeScore;
//                    }
//                    return 1000 * tankTypeScore;
//                })
//                .min(Integer::compareTo)
//                .orElse(0);
//        int minTankScore = 0;


        int d = 10 * BotMap.distances[playerTank.getY()][playerTank.getX()];

        int score = botMap.getScore();

        for (Direction direction : Direction.getMoveDirections()) {
            Point dir = playerTank.dir(direction);
            Point dir2 = dir.dir(direction);
            Direction reverse = direction.reverse();

            Bullet bullet = botMap.bulletsHolder.getBullet(dir);
            if (bullet != null && bullet.getDirection() == reverse) {
                score += BotMap.KILL_ME / 3;
            }
            if (botMap.isValidPoint(dir2)) {
                Bullet bullet2 = botMap.bulletsHolder.getBullet(dir2);
                if (bullet2 != null && bullet2.getDirection() == reverse) {
                    score += BotMap.KILL_ME / 4;
                }
            }

            Point box = botMap.getBox(dir);
            if (box instanceof Tank) {
                Tank tank = (Tank) box;
                if (tank.getFireCoolDown() <= 1 && (tank.getDirection() == reverse || tank.getType() == AI)) {
                    score += BotMap.KILL_ME / 5;
                }
            }
            if (botMap.isValidPoint(dir2)) {
                Point box2 = botMap.getBox(dir2);
                if (box2 instanceof Tank) {
                    Tank tank = (Tank) box2;
                    if (tank.getFireCoolDown() <= 2 && tank.getDirection() == reverse && tank.getStrategy() != TankStrategy.STAY) {
                        score += BotMap.KILL_ME / 10;
                    }
                }
            }
        }


        if (!playerTank.isAlive() && score > -80000) {
            System.out.println("Why not scored??!");
            return score - 100_000;
        }

        return score - d;
    }

}
