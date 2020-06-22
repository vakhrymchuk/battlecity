package clientlib.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Construction extends Point {

    private int timer;
    private int power;

    public Construction(int x, int y, int timer, int power) {
        super(x, y);
        this.timer = timer;
        this.power = power;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int decPowerAndGet() {
        power--;
        return power;
    }
}
