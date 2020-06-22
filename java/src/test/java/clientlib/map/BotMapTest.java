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

class BotMapTest {

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
    void fillMap() {

        aiTanks.add(new Tank(1, 1, UP, 4, 0, true));
        aiTanks.add(new Tank(4, 1, UP, 4, 0, true));

        bullets.add(new Bullet(5, 2, UP));


        map.fillMap(createMessage());
        map.show();

        assertFalse(map.isEmptyBox(new Point(0, 0))); // border
        assertFalse(map.isEmptyBox(new Point(2, 2))); // constr
        assertFalse(map.isEmptyBox(new Point(5, 5))); // me
        assertFalse(map.isEmptyBox(new Point(1, 1))); // ai
        assertFalse(map.isEmptyBox(new Point(4, 1))); // ai
        assertFalse(map.hasNoBullet(new Point(5, 2))); // bullet
        assertTrue(map.isEmptyBox(new Point(5, 4)));
    }

    @Test
    void makeRound_stop_me() {

        map.fillMap(createMessage());
        map.show();

        playerTank.setNextAction(new Action(Direction.STOP, null));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 5))); // me
    }

    @Test
    void makeRound_stop_move_ai() {

        aiTanks.add(new Tank(1, 1, UP, 4, 0, true));
        aiTanks.add(new Tank(4, 1, UP, 4, 0, true));
        aiTanks.add(new Tank(1, 3, LEFT, 4, 0, true));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(Direction.STOP, null));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 5))); // me
        assertFalse(map.isEmptyBox(new Point(1, 2))); // ai
        assertFalse(map.isEmptyBox(new Point(1, 3))); // ai stop
        assertTrue(map.isEmptyBox(new Point(1, 1))); // ai old position
        assertFalse(map.isEmptyBox(new Point(4, 1))); // ai stop
    }

    @Test
    void makeRound_move_bullets() {

        bullets.add(new Bullet(5, 2, UP));
        bullets.add(new Bullet(8, 1, RIGHT));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(Direction.STOP, null));
        map.makeRound();
        map.show();

        assertFalse(map.hasNoBullet(new Point(5, 4))); // bullet
        assertTrue(map.isEmptyBox(new Point(5, 3)));
        assertTrue(map.isEmptyBox(new Point(5, 2)));

        assertTrue(map.isEmptyBox(new Point(8, 1)));
        assertFalse(map.isEmptyBox(new Point(9, 1))); // border
    }

    @Test
    void makeRound_left_noWay() {

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(LEFT, null));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 5))); // me

    }

    @Test
    void makeRound_up_fire_before() {

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setFireCoolDown(0);
        playerTank.setNextAction(new Action(UP, FIRE_BEFORE_TURN));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 6))); // me
        assertTrue(map.isEmptyBox(new Point(5, 5)));
        assertFalse(map.hasNoBullet(new Point(5, 7))); // bullet
    }

    @Test
    void makeRound_right_fire_after() {

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setFireCoolDown(0);
        playerTank.setNextAction(new Action(RIGHT, FIRE_AFTER_TURN));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(6, 5))); // me
        assertTrue(map.isEmptyBox(new Point(5, 5)));
        assertTrue(map.isEmptyBox(new Point(7, 5))); // bullet destroyed constr
    }

    @Test
    void makeRound_destroy_construction() {

        bullets.add(new Bullet(6, 3, RIGHT));
        bullets.add(new Bullet(4, 8, DOWN));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(STOP));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 5))); // me
        assertTrue(map.isEmptyBox(new Point(6, 3))); // start bullet
        assertTrue(map.isEmptyBox(new Point(7, 3))); // construction is destroyed

        assertTrue(map.isEmptyBox(new Point(4, 8))); // start bullet
        assertFalse(map.isEmptyBox(new Point(4, 7))); // constr
        assertFalse(map.isEmptyBox(new Point(4, 6))); // constr
        assertEquals(3, ((Construction) map.getBox(new Point(4, 7))).getPower());
    }

    @Test
    void makeRound_stop_dead() {

        bullets.add(new Bullet(5, 3, UP));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertTrue(map.isEmptyBox(new Point(5, 5))); // WOW
        assertEquals(-1000000, map.getScore());
    }

    @Test
    void makeRound_stop_dead2() {

        bullets.add(new Bullet(5, 4, UP));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertTrue(map.isEmptyBox(new Point(5, 5))); // WOW
        assertEquals(-1000000, map.getScore());
    }

    @Test
    void makeRound_down_dead() {

        bullets.add(new Bullet(5, 3, UP));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(DOWN, null));
        map.makeRound();
        map.show();

        assertTrue(map.isEmptyBox(new Point(5, 4))); // WOW
        assertEquals(BotMap.KILL_ME, map.getScore());
    }

    @Test
    void makeRound_bullets_collapse_reverse() {

        int topLine = size - 2;
        bullets.add(new Bullet(5, topLine, RIGHT));
        bullets.add(new Bullet(8, topLine, DOWN));
        bullets.add(new Bullet(6, topLine, LEFT));

        Message message = createMessage();
        map.fillMap(message);
        map.show();

        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertTrue(map.hasNoBullet(new Point(5, topLine)));
        assertTrue(map.hasNoBullet(new Point(6, topLine)));
        assertFalse(map.hasNoBullet(new Point(8, topLine - 2)));
    }

    @Test
    void makeRound_bullets_collapse_perpendicular() {

        int topLine = size - 2;
        bullets.add(new Bullet(5, topLine, RIGHT));
        bullets.add(new Bullet(8, topLine, DOWN));
        bullets.add(new Bullet(6, topLine - 1, UP));

        map.fillMap(createMessage());
        map.show();

        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertTrue(map.isEmptyBox(new Point(5, topLine))); // start1
        assertTrue(map.isEmptyBox(new Point(6, topLine - 1))); // start2
        assertTrue(map.isEmptyBox(new Point(6, topLine))); // collapse
    }

    @Test
    void makeRound_ai_fire() {

        int topLine = size - 2;
        aiTanks.add(new Tank(2, topLine, RIGHT, 0, 0, true));
        map.fillMap(createMessage());
        map.show();

        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertFalse(map.hasNoBullet(new Point(4, topLine))); // bullet
        assertEquals(AI, map.getBox(new Point(3, topLine)).getType()); //ai
    }

    @Test
    void makeRound_enemy_fire() {

        Tank e = new Tank(6, 6, DOWN, 0, 0, true);
        e.setNextAction(new Action(STOP, FIRE_BEFORE_TURN));
        enemies.add(e);
        map.fillMap(createMessage());
        map.show();

        playerTank.setNextAction(new Action(RIGHT, null));
        map.makeRound();
        map.show();

        assertEquals(BotMap.KILL_ME, map.getScore());

    }

    @Test
    void makeRound_ai_move_order() {

        aiTanks.add(new Tank(2, 1, LEFT, 4, 0, true));
        aiTanks.add(new Tank(1, 2, DOWN, 4, 0, true));
        aiTanks.add(new Tank(8, 5, UP, 4, 0, true));
        aiTanks.add(new Tank(8, 6, UP, 4, 0, true));
        aiTanks.add(new Tank(8, 7, UP, 4, 0, true));

        Message message = createMessage();
        map.fillMap(message);
        map.show();
        playerTank.setNextAction(new Action(DOWN, null));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 4))); // me
        assertFalse(map.isEmptyBox(new Point(1, 1))); // top tank
        assertTrue(map.isEmptyBox(new Point(1, 2))); // start top tank
        assertFalse(map.isEmptyBox(new Point(2, 1))); // right tank no way
        assertFalse(map.isEmptyBox(new Point(8, 6)));
        assertFalse(map.isEmptyBox(new Point(8, 7)));
        assertFalse(map.isEmptyBox(new Point(8, 8)));

    }

    @Test
    void makeRound_move_on_my_bullet() {

        aiTanks.add(new Tank(2, 1, LEFT, 4, 0, true));
        bullets.add(new Bullet(1, 2, DOWN, ME));

        map.fillMap(createMessage());
        map.show();

        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertFalse(map.isEmptyBox(new Point(5, 5))); // me
        assertTrue(map.isEmptyBox(new Point(1, 1))); // WOW

        assertEquals(2000, map.getScore());
    }

    @Test
    void makeRound_move_on_ai_bullet() {

        bullets.add(new Bullet(5, 3, UP, AI));

        map.fillMap(createMessage());
        map.show();
        playerTank.setNextAction(new Action(DOWN, null));
        map.makeRound();
        map.show();

        assertTrue(map.isEmptyBox(new Point(5, 5))); // was me
        assertTrue(map.isEmptyBox(new Point(5, 3))); // was bullet
//        assertTrue(map.isEmptyBox(new Point(5, 4))); // WOW

        assertEquals(BotMap.KILL_ME, map.getScore());
    }

    @Test
    void makeRound_move_ai_change_dir() {

        aiTanks.add(new Tank(2, 1, LEFT, 4, 0, true));

        map.fillMap(createMessage());
        map.show();
        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();
//        playerTank.setNextAction(new Action(STOP, null));
        map.makeRound();
        map.show();

        assertTrue(map.isEmptyBox(new Point(1, 1)));

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

    private List<Tank> createAiTanks() {
        List<Tank> tanks = new ArrayList<>();
        tanks.add(new Tank(1, 1, UP, 4, 0, true));
        tanks.add(new Tank(4, 1, UP, 4, 0, true));
        tanks.add(new Tank(6, 1, RIGHT, 4, 0, true));
        return tanks;
    }

    private List<Bullet> createBullets() {
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(5, 1, UP));
        bullets.add(new Bullet(6, 3, RIGHT));
        return bullets;
    }

}