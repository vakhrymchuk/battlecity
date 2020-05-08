package clientlib.model;

import java.util.List;

public class Message {

    private Tank playerTank;
    private List<Tank> aiTanks;
    private List<Tank> enemies;
    private List<Construction> constructions;
    private List<Bullet> bullets;
    private List<String> layers;
    private List<Point> borders;

    public Message(Tank playerTank,
                   List<Tank> aiTanks,
                   List<Tank> enemies,
                   List<Construction> constructions,
                   List<Bullet> bullets,
                   List<String> layers,
                   List<Point> borders) {
        this.playerTank = playerTank;
        this.aiTanks = aiTanks;
        this.enemies = enemies;
        this.constructions = constructions;
        this.bullets = bullets;
        this.layers = layers;
        this.borders = borders;
    }

    public Tank getPlayerTank() {
        return playerTank;
    }

    public List<Tank> getAiTanks() {
        return aiTanks;
    }

    public List<Tank> getEnemies() {
        return enemies;
    }

    public List<Construction> getConstructions() {
        return constructions;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<String> getLayers() {
        return layers;
    }

    public List<Point> getBorders() {
        return borders;
    }
}
