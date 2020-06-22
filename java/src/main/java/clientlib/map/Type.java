package clientlib.map;

public enum Type {
    BORDER('☼'),
    CONSTRUCTION('╬'),
    ENEMY('«'),
    AI('˂'),
    ME('▲');

    private final char c;
    Type(char c) {
        this.c = c;
    }

    public char getC() {
        return c;
    }
}
