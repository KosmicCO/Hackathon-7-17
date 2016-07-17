package game;

import engine.Core;
import static game.UnitType.BASIC_RANGED;
import graphics.Window2D;
import static util.Color4.gray;

public class TestMain {

    public static void main(String[] args) {
        Core.init();

        Window2D.background = gray(.5);

        Unit u = new Unit(BASIC_RANGED);
        u.create();

        Core.run();
    }
}
