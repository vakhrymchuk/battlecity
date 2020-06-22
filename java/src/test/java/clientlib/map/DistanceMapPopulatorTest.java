package clientlib.map;

import clientlib.action.Action;
import clientlib.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static clientlib.action.ActionPrinter.FireOrder.FIRE_AFTER_TURN;
import static clientlib.action.ActionPrinter.FireOrder.FIRE_BEFORE_TURN;
import static clientlib.map.Type.AI;
import static clientlib.map.Type.ME;
import static clientlib.model.Direction.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class DistanceMapPopulatorTest {

    int size = 10;

    BotMap map;

    Tank playerTank;
    List<Point> borders;
    List<Construction> constructions;
    List<Tank> aiTanks;
    List<Bullet> bullets;
    private List<Tank> enemies;


    @BeforeEach
    void setUp() {
        map = new BotMap(size, size);
        playerTank = new Tank(5, 5, UP, 4, 0, true);
        borders = createBorders(size);
        constructions = createConstructions(size);
        enemies = new ArrayList<>();
        aiTanks = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    private Message createMessage() {
        return new Message(playerTank, aiTanks, enemies, constructions, bullets, emptyList(), borders);
    }

    @Test
    void populate() {

//        DistanceMapPopulator.populate();

        enemies.add(new Tank(3, 1, UP, 4, 0, true));
        enemies.add(new Tank(8, 1, UP, 4, 0, true));
        aiTanks.add(new Tank(10, 1, UP, 4, 0, true));

        map.fillMap(createMessage());
        map.show();

        DistanceMapPopulator.show(BotMap.distances);

        assertEquals(6, BotMap.distances[5][5]);
        assertEquals(1000, BotMap.distances[0][0]);

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

}