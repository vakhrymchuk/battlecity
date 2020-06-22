package clientlib.map;

import clientlib.model.Message;
import clientlib.model.Point;
import clientlib.model.Tank;
import clientlib.model.TankStrategy;

import java.util.ArrayList;
import java.util.List;

import static clientlib.map.Type.*;

public class MapPopulator {

    public static List<Tank> staticTanks = new ArrayList<>();

    public static void populate(Message message, BotMap botMap) {
        updateTanksStrategies(message.getEnemies());

        Tank playerTank = message.getPlayerTank();
        putList(message.getBorders(), BORDER, botMap);
        putList(message.getConstructions(), CONSTRUCTION, botMap);
        putTanks(message.getEnemies(), ENEMY, botMap);
        putTanks(message.getAiTanks(), AI, botMap);
        putTank(playerTank, ME, botMap);

    }

    private static void updateTanksStrategies(List<Tank> enemies) {

        enemies.forEach(enemy -> enemy.setStrategy(TankStrategy.AI_GO_ME));

        for (Tank staticTank : staticTanks) {
//            System.out.println("staticTank = " + staticTank);
            enemies.stream()
                    .filter(staticTank::isSamePoint)
                    .findFirst()
                    .ifPresent(tank -> {
//                        System.out.println("new tank = " + tank);
                        tank.setSamePlaceCounter(staticTank.getSamePlaceCounter() + 1);
//                        System.out.println("new tank = " + tank);
                        if (tank.getSamePlaceCounter() > 3) {
                            tank.setStrategy(TankStrategy.STAY);
//                            System.out.println("stay tank = " + tank);
                        }
                    });
        }

        staticTanks.clear();
        staticTanks.addAll(enemies);
    }


    private static void putList(List<? extends Point> points, Type type, BotMap botMap) {
        for (Point point : points) {
            botMap.addBox(point, type);
        }
    }


    private static void putTanks(List<Tank> tanks, Type type, BotMap botMap) {
        for (Tank tank : tanks) {
            if (tank.isAlive()) {
                putTank(tank, type, botMap);
            }
        }
    }

    private static void putTank(Tank tank, Type type, BotMap botMap) {
        tank.setType(type);
        botMap.addBox(tank, type);
    }

}
