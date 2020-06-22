package clientlib.map;

import clientlib.action.Action;
import clientlib.action.ActionPrinter.FireOrder;
import clientlib.model.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static clientlib.action.ActionPrinter.FireOrder.FIRE_AFTER_TURN;
import static clientlib.action.ActionPrinter.FireOrder.FIRE_BEFORE_TURN;
import static clientlib.map.Type.*;
import static clientlib.model.Direction.STOP;
import static clientlib.model.Direction.getMoveDirections;
import static clientlib.model.TankStrategy.STAY;


public class BotMap implements Serializable {

    public static final int DEFAULT_SIZE = 34;
    public static final int KILL_ENEMY = 10_000;
    public static final int KILL_AI = 2_000;
    public static final int KILL_ME = -1_000_000;
    public static final int PENALTY_FIRE = -500;
    public static final int PENALTY_STOP = -100;

    public static int[][] distances;

    private static final Random rnd = new Random();

    private static final Set<Type> tankTypes = EnumSet.of(ENEMY, AI, ME);
    private static final Set<Type> tankTypesEnemy = EnumSet.of(ENEMY);
    private static final Set<Type> tankTypesEnemyAi = EnumSet.of(ENEMY, AI);

    @Getter
    private final int sizeY, sizeX;

    private final Point[][] boxes;

    public final BulletsHolder bulletsHolder;

    @Getter
    @Setter
    private int score = 0;


    public BotMap() {
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public BotMap(int sizeY, int sizeX) {
        this.sizeY = sizeY;
        this.sizeX = sizeX;
        boxes = new Point[sizeY][sizeX];
        bulletsHolder = new BulletsHolder(sizeY, sizeX);
    }


    public void fillMap(Message message) {
//        clear();

        MapPopulator.populate(message, this);
//        List<Tank> collect = getEnemies().stream().filter(tank -> tank.getStrategy() == STAY).collect(Collectors.toList());
//        System.out.println("stay collect = " + collect);

        Tank playerTank = message.getPlayerTank();
        distances = DistanceMapPopulator.createDistArray(this, playerTank);

//        bulletsHolder.clear();
        bulletsHolder.putBullets(message.getBullets());
    }

    public void show() {
        for (int y = boxes.length - 1; y >= 0; y--) {
            Point[] boxLine = boxes[y];
            for (int x = 0; x < boxLine.length; x++) {
                System.out.print(getChar(boxLine[x], bulletsHolder.getBullet(y, x), distances[y][x]));
            }
            int[] line = distances[y];
            for (int i : line) {
                System.out.print(i == 1000 ? "    " : String.format("%4d", i));
            }
            System.out.println();
        }
    }

    private char getChar(Point box, Bullet bullet, int d) {
        if (bullet != null) {
            return '•';
        }
        if (box != null) {
            if (box.getType() == ME) {
                int fireCoolDown = ((Tank) box).getFireCoolDown();
                return (char) (fireCoolDown + '0');
            }
            return box.getType().getC();
        }
//        if (d < 3) return (char) (d + '0');

        return ' ';
    }

    public void addBox(Point point, Type type) {
        if (isValidPoint(point)) {
            point.setType(type);
            setBox(point, point);
        }
    }

    public void setBox(Point point, Point box) {
        boxes[point.getY()][point.getX()] = box;
    }

    public boolean isValidPoint(Point point) {
        return point.getX() >= 0 && point.getX() < sizeX && point.getY() >= 0 && point.getY() < sizeY;
    }

    public Point getBox(Point point) {
        return getBox(point.getY(), point.getX());
    }

    public Point getBox(int y, int x) {
        Point[] line = boxes[y];
        return line[x];
    }


    public Point removeBox(Point point) {
        Point box = getBox(point);
        setBox(point, null);
        return box;
    }


    public boolean isEmptyBox(Point point) {
        return getBox(point) == null;
    }

    public boolean hasNoBullet(Point point) {
        return bulletsHolder.hasNoBullet(point);
    }

    public boolean isBoxBorderOrConstruction(Point point) {
        Point box = getBox(point);
        return box != null && (box.getType() == BORDER || box.getType() == CONSTRUCTION);
    }

    public boolean isBoxConstruction(Point point) {
        Point box = getBox(point);
        return box != null && box.getType() == CONSTRUCTION;
    }


    public void makeRound() {
        bulletsHolder.putBullets(generateNewBullets(FIRE_BEFORE_TURN));
        makeRoundBullets();

        makeAllMove();

        bulletsHolder.putBullets(generateNewBullets(FIRE_AFTER_TURN));
        makeRoundBullets();

        decFireCoolDowns();
    }


    private void decFireCoolDowns() {
        getTanksOfType(tankTypes)
                .forEach(Tank::decFireCoolDown);
    }

    private List<Bullet> generateNewBullets(FireOrder fireOrder) {
        List<Bullet> newBullets = new ArrayList<>();
        getTanksOfType(tankTypes)
                .stream()
                .filter(Tank::isAlive)
                .filter(tank -> tank.getFireCoolDown() <= 1)
                .forEach(tank -> {
//                    System.out.println("tank = " + tank);
                    if (tank.getType() == AI) {
                        newBullets.add(createBullet(tank));
                        if (fireOrder == FIRE_AFTER_TURN) {
                            tank.setFireCoolDown(0);
                        }
                    } else {
                        if (tank.getNextAction() != null && tank.getNextAction().getFireOrder() == fireOrder) {
                            newBullets.add(createBullet(tank));
                            tank.setFireCoolDown(4);
                            if (tank.getType() == ME && distances[tank.getY()][tank.getX()] < 5) {
                                score += PENALTY_FIRE;
                            }
                        }
                    }
                });
        return newBullets;
    }

    private Bullet createBullet(Tank tank) {
        return new Bullet(tank.getX(), tank.getY(), tank.getDirection(), tank.getType());
    }

    private void makeRoundBullets() {
        List<Bullet> allBullets = bulletsHolder.getAllBullets();

        bulletsHolder.collapseBulletsReverse(allBullets);

        allBullets = bulletsHolder.getAllBullets();
        Map<Direction, List<Bullet>> bulletMap = allBullets
                .stream()
                .collect(Collectors.groupingBy(Bullet::getDirection));

        for (Direction direction : Direction.values()) {
            moveBullets(bulletMap.getOrDefault(direction, List.of()));
        }

    }

    private void moveBullets(List<Bullet> bullets) {
        for (Bullet bullet : bullets) {

            Point boxOld = getBox(bullet);
            if (boxOld != null) {
//                setBox(boxOld.getPoint(), boxOld);
                if (tankTypes.contains(boxOld.getType()) && boxOld.getType() != bullet.getType()) {
                    System.out.println("TODO Box Old Bullet FIRE tank!!!" + boxOld);
                    System.out.println("bullet = " + bullet);
                }
            }

            bulletsHolder.moveBullet(bullet);
//            System.out.println("bullet moved = " + bullet);

            Point boxNew = removeBox(bullet);
            if (boxNew == null) {
                continue;
            }

            bulletsHolder.removeBullet(bullet);

            if (boxNew.getType() == BORDER) {
                setBox(boxNew, boxNew);
            } else if (boxNew.getType() == CONSTRUCTION) {
                if (((Construction) boxNew).decPowerAndGet() > 0) {
                    setBox(bullet, boxNew);
                }
            } else if (boxNew.getType() == ME) {
//                System.out.println("Bullet kill me");
                calcScoreKillMe(bullet);
                Tank tank = (Tank) boxNew;
                tank.setAlive(false);
//                boxNew.setType(BOMB);
//                setBox(bullet, boxNew);
            } else if (boxNew.getType() == ENEMY || boxNew.getType() == AI) {
                Tank tank = (Tank) boxNew;
                tank.setAlive(false);
                calcScoreKillTank(tank, bullet);
            }

        }
    }

    private void makeAllMove() {

        Map<Direction, List<Tank>> mapMoveTanks = getTanksToMove();

        boolean moved = true;
        while (moved) {
            moved = false;
            for (Direction direction : Direction.values()) {
                List<Tank> tankList = mapMoveTanks.getOrDefault(direction, List.of());
                while (!tankList.isEmpty()) {
                    List<Tank> tanksToMove = moveTanks(tankList);
                    if (tankList.size() == tanksToMove.size()) {
//                        System.out.println("NO NEW MOVINGS " + direction);
                        break;
                    }
                    tankList = tanksToMove;
                    moved = true;
                }
                mapMoveTanks.put(direction, tankList);
            }
        }

        changeDirAi();
    }

    private Map<Direction, List<Tank>> getTanksToMove() {
        return getTanksOfType(tankTypes)
                .stream()
                .filter(tank -> tank.getStrategy() != STAY)
                .map(tank -> {
                    switch (tank.getType()) {
                        case AI:
                            if (tank.getNextAction() != null && tank.getNextAction().getDirection() != null) {
                                tank.setDirection(tank.getNextAction().getDirection());
                            }
                            break;
                        case ENEMY:
                        case ME:
//                            tank.setDirection(playerTank.diff(tank).toBiggestDir());

                            if (tank.getNextAction() != null && tank.getNextAction().getDirection() == STOP) {
                                score += PENALTY_STOP;

                            }

                            if (tank.getNextAction() != null && tank.getNextAction().getDirection() != STOP) {

                                tank.setDirection(tank.getNextAction().getDirection());


                            } else
                                return null;
                    }
                    return tank;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Tank::getDirection));
    }

    private List<Tank> moveTanks(List<Tank> tankList) {
        List<Tank> tanksToMove = new ArrayList<>(tankList.size());
        for (Tank tank : tankList) {

            if (bulletsHolder.hasBullet(tank)) {
                System.out.println("BULLET ON OLD PLACE");
            }


            Point boxOld = removeBox(tank);
            Point tankNewPoint = tank.dir(tank.getDirection());

            if (bulletsHolder.hasBullet(tankNewPoint)) {
                Bullet bullet = bulletsHolder.getBullet(tankNewPoint);
                if (bullet.getType() != tank.getType()) {
                    bulletsHolder.removeBullet(tankNewPoint);

                    if (tank.getType() == ME) {
                        if (bullet.getType() != ME) {
//                            System.out.println("KILLED myself");
                            calcScoreKillMe(bullet);
                            tank.setAlive(false);
                        }
                    } else {
//                        System.out.println("KILLED enemy: " + tank.type);
                        calcScoreKillTank(tank, bullet);
                        tank.setAlive(false);
                    }
                    continue;
                }
            }

            Point boxNew = getBox(tankNewPoint);
            if (boxNew == null) {
                tank.moveToPoint(tankNewPoint);
                setBox(tankNewPoint, boxOld);
            } else {
                setBox(tank, boxOld);
                if (boxNew.getType() != BORDER && boxNew.getType() != CONSTRUCTION) {
                    tanksToMove.add(tank);
                }
            }
        }
        return tanksToMove;
    }

    private void changeDirAi() {
        Set<Type> set = EnumSet.of(CONSTRUCTION, BORDER);
        getTanksOfType(EnumSet.of(AI))
                .forEach(tank -> {
                    Point pointNext = tank.dir(tank.getDirection());
//                    if (!isValidPoint(pointNext)) return;

                    Point boxNext = getBox(pointNext);
                    if (boxNext != null && set.contains(boxNext.getType())) {

                        List<Direction> newDirections = getMoveDirections()
                                .stream()
                                .filter(direction -> direction != tank.getDirection())
                                .filter(direction -> isEmptyBox(tank.dir(direction)))
                                .collect(Collectors.toList());

                        int size = newDirections.size();
                        if (size > 0) {
                            Direction direction = newDirections.get(rnd.nextInt(size));
                            tank.setNextAction(new Action(direction));
                            tank.incFakeProbability(tank.getFakeProbability() / size);
                        } else
                            System.out.println("new dir for ai not found");
                    }
                });
    }

    private void calcScoreKillMe(Bullet bullet) {
        score += KILL_ME;
    }

    private void calcScoreKillTank(Tank enemy, Bullet bullet) {
        float killKoef = bullet.getType() == ME ? 1.0f : 0.1f;
        int killKoefByEnemyType = enemy.getType() == ENEMY ? KILL_ENEMY : KILL_AI;
        int killKoefKillerEnemy = (enemy.getKilledCounter() + 1);
        score += killKoef * killKoefByEnemyType * killKoefKillerEnemy * enemy.getFakeProbability();
    }


    public List<Tank> getEnemies() {
        return getPointsOfType(tankTypesEnemy);
    }

    public List<Tank> getAllTanks() {
        return getPointsOfType(tankTypesEnemyAi);
    }

    public List<Tank> getTanksOfType(Set<Type> types) {
        return getPointsOfType(types);
    }

    public <T extends Point> List<T> getPointsOfType(Set<Type> types) {
        return Arrays.stream(boxes)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(box -> types.contains(box.getType()))
                .map(box -> (T) box)
                .collect(Collectors.toList());
    }

    public BotMap copy() {
        BotMap clone = SerializationUtils.clone(this);
        clone.score = 0;
        return clone;
    }

    public void showNearestDistance(Tank playerTank) {
        System.out.println("Nearest enemy in = " + BotMap.distances[playerTank.getY()][playerTank.getX()]);
    }

    public BotMap trimMap(Tank playerTank, int size) {

        int half = size / 2;
        int leftInc = Math.max(playerTank.getX() - half, 0);
        int rightExc = Math.min(playerTank.getX() + half, sizeX);
        int bottomInc = Math.max(playerTank.getY() - half, 0);
        int topExc = Math.min(playerTank.getY() + half, sizeY);

        int newSizeX = rightExc - leftInc;
        int newSizeY = topExc - bottomInc;

        BotMap trimmedBotMap = new BotMap(newSizeY, newSizeX);

        trimBoxes(leftInc, bottomInc, trimmedBotMap);

        trimDistances(leftInc, bottomInc, newSizeX, newSizeY);

        trimmedBotMap.bulletsHolder.trim(bulletsHolder, leftInc, bottomInc);

        return trimmedBotMap;
    }

    private void trimBoxes(int leftInc, int bottomInc, BotMap trimmedBotMap) {
        for (int y = 0; y < trimmedBotMap.getSizeY(); y++) {
            System.arraycopy(boxes[bottomInc + y], leftInc, trimmedBotMap.boxes[y], 0, trimmedBotMap.getSizeX());
        }

        for (int y = 0; y < trimmedBotMap.getSizeY(); y++) {
            for (int x = 0; x < trimmedBotMap.getSizeX(); x++) {
                Point box = trimmedBotMap.getBox(y, x);
                if (box != null) {
                    box.setX(box.getX() - leftInc);
                    box.setY(box.getY() - bottomInc);
                }
            }
        }

        int rightLine = trimmedBotMap.getSizeX() - 1;
        for (int y = 0; y < trimmedBotMap.getSizeY(); y++) {
            trimmedBotMap.addBox(new Point(0, y), BORDER);
            trimmedBotMap.addBox(new Point(rightLine, y), BORDER);
        }
        int topLine = trimmedBotMap.getSizeY() - 1;
        for (int x = 0; x < trimmedBotMap.getSizeX(); x++) {
            trimmedBotMap.addBox(new Point(x, 0), BORDER);
            trimmedBotMap.addBox(new Point(x, topLine), BORDER);
        }

    }

    private void trimDistances(int leftInc, int bottomInc, int newSizeX, int newSizeY) {
        int[][] trimmedDistances = new int[newSizeY][newSizeX];
        for (int y = 0; y < newSizeY; y++) {
            System.arraycopy(distances[bottomInc + y], leftInc, trimmedDistances[y], 0, newSizeX);
        }
        distances = trimmedDistances;
    }
}

