package clientlib;

import clientlib.action.Action;
import clientlib.action.ActionPrinter;
import clientlib.finder.DynPathFinder;
import clientlib.map.BotMap;
import clientlib.model.Bullet;
import clientlib.model.Direction;
import clientlib.model.Tank;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SampleSolver extends Solver {

    long lastTime;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String move() {

        long now = System.currentTimeMillis();
        long diff = now - lastTime;
        lastTime = now;
        System.out.println("==== [diff = " + diff + " ] ========================================================");

        System.out.println(mapper.writeValueAsString(message));


        Tank playerTank = message.getPlayerTank();
        if (!playerTank.isAlive()) {
            System.out.println("----[MY TANK IS DEAD]-------------------------------------------");
            return ActionPrinter.printActionStop();
        }

        BotMap botMap = new BotMap();
        botMap.fillMap(message);
//        botMap.show();

        for (Tank enemy : message.getEnemies())
            if (playerTank.manhattanDistance(enemy) < 5)
                System.out.println("enemy = " + enemy);


        botMap = botMap.trimMap(playerTank, 12);
        botMap.show();


        botMap.showNearestDistance(playerTank);
        System.out.println("playerTank = " + playerTank);
        for (Tank enemy : message.getEnemies())
            if (playerTank.manhattanDistance(enemy) < 8)
                System.out.println("enemy = " + enemy);
        for (Tank enemy : message.getAiTanks())
            if (playerTank.manhattanDistance(enemy) < 5)
                System.out.println("ai = " + enemy);
        for (Bullet bullet : message.getBullets())
            if (playerTank.manhattanDistance(bullet) < 5)
                System.out.println("bullets = " + bullet);


        try {
            long start = System.currentTimeMillis();
            Action action = DynPathFinder.findBest(botMap, playerTank, 1);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            System.out.println("time= " + time + " action dyn path = " + action);
            if (time > 1000) System.out.println("TOO LONG");
            return ActionPrinter.printAction(action);
        } catch (Exception e) {
            e.printStackTrace();
            return ActionPrinter.printAction(new Action(Direction.UP));
        }
    }
}
