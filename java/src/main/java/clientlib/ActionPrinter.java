package clientlib;


public class ActionPrinter {

    private ActionPrinter() {
    }

    public static String printAction(FireOrder fireOrder, Direction direction) {
        if (direction == null) {
            if (fireOrder == null) {
                return "STOP";
            }
            switch (fireOrder) {
                case FIRE_AFTER_TURN:
                    return "ACT(1)";
                case FIRE_BEFORE_TURN:
                    return "ACT";
            }
        }
        String str = direction.name();
        if (fireOrder == null) {
            return str;
        }
        switch (fireOrder) {
            case FIRE_AFTER_TURN:
                return str + ",ACT";
            case FIRE_BEFORE_TURN:
                return "ACT," + str;
        }
        return "STOP";
    }

    public enum FireOrder {
        FIRE_BEFORE_TURN, FIRE_AFTER_TURN
    }
}
