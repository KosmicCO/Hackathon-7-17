package game;

import engine.Core;
import static game.UnitType.BASIC_RANGED;

public class TestMain {

    public static void main(String[] args) {
        Core.init();

        Unit u = new Unit(BASIC_RANGED);
        u.create();

        Core.run();
    }
}
