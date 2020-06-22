package clientlib.action;

import clientlib.model.Direction;
import clientlib.action.ActionPrinter.FireOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
public class Action implements Serializable {

    private Direction direction = null;
    private FireOrder fireOrder = null;
    private int score = 0;

    public Action(Direction direction, FireOrder fireOrder) {
        this.direction = direction;
        this.fireOrder = fireOrder;
    }

    public Action(Direction direction) {
        this.direction = direction;
    }

    public Action(int score) {
        this.score = score;
    }

}
