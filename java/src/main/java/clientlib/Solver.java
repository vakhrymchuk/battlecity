package clientlib;


import clientlib.model.Message;


public abstract class Solver {
    protected Message message;

    public void applyMessage(Message message) {
        this.message = message;
    }

    public abstract String move();
}
