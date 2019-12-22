package clientlib;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Solver {

    protected int size;
    protected Elements[][] field;
    protected Map<Elements, List<Point>> mapElements;

    /**
     * Метод парсинга игрового поля. Вызывается после ответа сервера
     *
     * @param boardString игровое поле
     */
    public Solver parseField(String boardString) {

        String board = boardString.replaceAll("\n", "");
        size = (int) Math.sqrt(board.length());
        field = new Elements[size][size];
        mapElements = new HashMap<>();

        board = boardString.replaceAll("\n", "");

        char[] temp = board.toCharArray();
        for (int y = 0; y < size ; y++) {
            int dy = y * size;
            for (int x = 0; x < size; x++) {
                field[x][y] = Elements.valueOf(temp[dy + x]);
            }
        }

        return this;
    }

    public abstract String move();
}
