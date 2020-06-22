package clientlib.finder;

import clientlib.action.Action;
import clientlib.map.BotMap;
import clientlib.map.Type;
import clientlib.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static clientlib.model.Direction.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DynPathFinderTest {

    public static final EnumSet<Type> tanksEnemyAi = EnumSet.of(Type.AI, Type.ENEMY);
    int size = 10;

    BotMap botMap;

    Tank playerTank;
    List<Point> borders;
    List<Construction> constructions;
    List<Tank> aiTanks;
    List<Bullet> bullets;
    private List<Tank> enemies;


    @BeforeEach
    void setUp() {
        botMap = new BotMap(size, size);
        playerTank = new Tank(5, 5, UP, 0, 0, true);
        borders = createBorders(size);
        constructions = createConstructions(size);
        enemies = new ArrayList<>();
        aiTanks = new ArrayList<>();
        bullets = createBullets();
    }

    private Message createMessage() {
        return new Message(playerTank, aiTanks, enemies, constructions, bullets, emptyList(), borders);
    }

    @Test
    void findBest_ai_perpendicular() {

        aiTanks.add(new Tank(4, 8, RIGHT, 8, 0, true));

        botMap.fillMap(createMessage());
        botMap.show();

        int count = 0;

        while (botMap.getTanksOfType(tanksEnemyAi).size() > 0) {

            if (++count > 3) fail("Count is too big " + count);

            Action action = DynPathFinder.findBest(botMap, playerTank, 3);
            System.out.println("Step = " + count + " action = " + action);
            playerTank.setNextAction(action);
            botMap.makeRound();
            botMap.show();
            botMap = recreateMap();
        }
    }

    @Test
    void findBest_ai() {

        aiTanks.add(new Tank(3, 8, RIGHT, 3, 0, true));
        aiTanks.add(new Tank(1, 8, RIGHT, 5, 0, true));

        botMap.fillMap(createMessage());
        botMap.show();

        int count = 0;

        while (botMap.getTanksOfType(tanksEnemyAi).size() > 0) {

            if (++count > 20) fail("Count is too big " + count);

            Action action = DynPathFinder.findBest(botMap, playerTank, 3);
            System.out.println("Step = " + count + " action = " + action);
            playerTank.setNextAction(action);
            botMap.makeRound();
            botMap.show();
            botMap = recreateMap();
        }
    }


    @Test
    void findBest_enemy() {

        Tank e = new Tank(6, 1, UP, 0, 0, true);
//        e.setStrategy(TankStrategy.STOP);
        enemies.add(e);
        bullets.add(new Bullet(5, 4, UP));

        playerTank.setDirection(DOWN);

        botMap.fillMap(createMessage());
        botMap.show();

        int count = 0;

        while (botMap.getTanksOfType(tanksEnemyAi).size() > 0) {

            assertTrue(++count <= 20);

            Action action = DynPathFinder.findBest(botMap, playerTank, 2);
            System.out.println("Step = " + count + " action = " + action);
            playerTank.setNextAction(action);
            botMap.makeRound();
            botMap.show();

            assertTrue(playerTank.isAlive());

            botMap = recreateMap();
        }

    }



    @Test
    void findBest_construction_eagle() {

        Tank e = new Tank(8, 3, UP, 0, 0, true);
        enemies.add(e);
        playerTank.setY(1);

        constructions = createConstructionsEagle();

        botMap.fillMap(createMessage());
        botMap.show();

        int count = 0;

        while (botMap.getTanksOfType(tanksEnemyAi).size() > 0) {

            assertTrue(++count <= 20);

            Action action = DynPathFinder.findBest(botMap, playerTank, 1);
            System.out.println("Step = " + count + " action = " + action);
            playerTank.setNextAction(action);
            botMap.makeRound();
            botMap.show();

            assertTrue(playerTank.isAlive());

            botMap = recreateMap();
        }

    }


    private BotMap recreateMap() {
        borders = botMap.getPointsOfType(EnumSet.of(Type.BORDER));
        constructions = botMap.getPointsOfType(EnumSet.of(Type.CONSTRUCTION));
        enemies = botMap.getTanksOfType(EnumSet.of(Type.ENEMY));
        aiTanks = botMap.getTanksOfType(EnumSet.of(Type.AI));
        bullets = botMap.bulletsHolder.getAllBullets();

        BotMap mapNew = new BotMap(size, size);
        mapNew.fillMap(createMessage());
        return mapNew;
    }

    private List<Point> createBorders(int size) {
        int lastIndex = size - 1;
        Set<Point> borders = new HashSet<>();
        for (int i = 0; i < size; i++) {
            borders.add(new Point(0, i));
            borders.add(new Point(i, 0));
            borders.add(new Point(i, lastIndex));
            borders.add(new Point(lastIndex, i));
        }
        return new ArrayList<>(borders);
    }

    private List<Construction> createConstructions(int size) {
        List<Construction> list = new ArrayList<>();
        for (int i = 2; i < size - 2; i++) {
            list.add(new Construction(2, i, 100, 4));
            list.add(new Construction(4, i, 100, 4));
            list.add(new Construction(7, i, 100, 1));
        }
        return list;
    }

    private List<Construction> createConstructionsEagle() {
        List<Construction> list = new ArrayList<>();
        list.add(new Construction(2, 1, 100, 4));
        list.add(new Construction(2, 2, 100, 4));
        list.add(new Construction(2, 3, 100, 4));
        list.add(new Construction(3, 3, 100, 4));
        list.add(new Construction(4, 3, 100, 1));
        list.add(new Construction(5, 3, 100, 4));
        list.add(new Construction(6, 3, 100, 4));
        list.add(new Construction(7, 3, 100, 4));
        list.add(new Construction(7, 2, 100, 4));
        list.add(new Construction(7, 1, 100, 4));
        return list;
    }

    private List<Tank> createAiTanks() {
        List<Tank> tanks = new ArrayList<>();
//        tanks.add(new Tank(1, 1, UP, 4, 0, true));
//        tanks.add(new Tank(4, 1, UP, 4, 0, true));
//        tanks.add(new Tank(5, 1, RIGHT, 4, 0, true));
        return tanks;
    }

    private List<Bullet> createBullets() {
        List<Bullet> bullets = new ArrayList<>();
//        bullets.add(new Bullet(5, 1, UP));
//        bullets.add(new Bullet(6, 3, RIGHT));
        return bullets;
    }

}