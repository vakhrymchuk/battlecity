package clientlib


import static clientlib.TestSolver.STEP.*

class TestSolver extends Solver {

    enum STEP {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        ACT,
        DELAYED_ACT,

        ACT_AND_UP,
        ACT_AND_DOWN,
        ACT_AND_LEFT,
        ACT_AND_RIGHT,

        UP_AND_ACT,
        DOWN_AND_ACT,
        LEFT_AND_ACT,
        RIGHT_AND_ACT
    }

    private STEP stepToCheck

    TestSolver(STEP stepToCheck) {
        this.stepToCheck = stepToCheck
    }

    @Override
    String move() {
        switch (stepToCheck) {
            case ACT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_BEFORE_TURN, null)
            case DELAYED_ACT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_AFTER_TURN, null)
            case UP: return ActionPrinter.printAction(null, Direction.UP)
            case DOWN: return ActionPrinter.printAction(null, Direction.DOWN)
            case LEFT: return ActionPrinter.printAction(null, Direction.LEFT)
            case RIGHT: return ActionPrinter.printAction(null, Direction.RIGHT)
            case ACT_AND_UP: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_BEFORE_TURN, Direction.UP)
            case ACT_AND_DOWN: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_BEFORE_TURN, Direction.DOWN)
            case ACT_AND_LEFT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_BEFORE_TURN, Direction.LEFT)
            case ACT_AND_RIGHT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_BEFORE_TURN, Direction.RIGHT)
            case UP_AND_ACT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_AFTER_TURN, Direction.UP)
            case DOWN_AND_ACT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_AFTER_TURN, Direction.DOWN)
            case LEFT_AND_ACT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_AFTER_TURN, Direction.LEFT)
            case RIGHT_AND_ACT: return ActionPrinter.printAction(ActionPrinter.FireOrder.FIRE_AFTER_TURN, Direction.RIGHT)
        }
        throw new RuntimeException()
    }
}
