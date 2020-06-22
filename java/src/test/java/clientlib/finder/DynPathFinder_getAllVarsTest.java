package clientlib.finder;

import clientlib.action.Action;
import clientlib.map.BotMap;
import clientlib.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static clientlib.model.Direction.UP;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynPathFinder_getAllVarsTest {

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
    void test_getAllVars_emptyList() {

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(allVars.size(), 1);
        assertTrue(allVars.get(0).isEmpty());
    }

    @Test
    void test_getAllVars_oneEnemy_notFire() {

        Tank e1 = new Tank(7, 7, Direction.RIGHT, 3, 0, true);
        enemies.add(e1);

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(5, allVars.size());
    }

    @Test
    void test_getAllVars_oneEnemy_canFire() {

        Tank e1 = new Tank(7, 7, Direction.RIGHT, 0, 0, true);
        enemies.add(e1);

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(15, allVars.size());
    }

    @Test
    void test_getAllVars_oneEnemy_notFire_wall() {

        Tank e1 = new Tank(8, 8, Direction.RIGHT, 0, 0, true);
        enemies.add(e1);

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(9, allVars.size());
    }


    @Test
    void test_getAllVars_twoEnemies_notFire() {

        Tank e1 = new Tank(7, 3, Direction.RIGHT, 3, 0, true);
        Tank e2 = new Tank(7, 7, Direction.LEFT, 2, 1, true);
        enemies.add(e1);
        enemies.add(e2);

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(5 * 5, allVars.size());
    }

    @Test
    void test_getAllVars_twoEnemies_oneFire() {

        Tank e1 = new Tank(7, 3, Direction.RIGHT, 0, 0, true);
        Tank e2 = new Tank(7, 7, Direction.LEFT, 2, 1, true);
        enemies.add(e1);
        enemies.add(e2);

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(5 * 15, allVars.size());
    }

    @Test
    void test_getAllVars_threeEnemies_oneFire() {

        Tank e1 = new Tank(7, 3, Direction.RIGHT, 0, 0, true);
        Tank e2 = new Tank(7, 5, Direction.LEFT, 2, 1, true);
        Tank e3 = new Tank(7, 6, Direction.DOWN, 2, 1, true);
        enemies.add(e1);
        enemies.add(e2);
        enemies.add(e3);

        botMap.fillMap(createMessage());
        botMap.show();

        List<List<Action>> allVars = DynPathFinder.getAllVars(enemies, botMap);

        for (List<Action> allVar : allVars) {
            System.out.println("allVar = " + allVar);
        }

        assertEquals(5 * 5 * 15, allVars.size());
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
        }
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