package game;

import engine.Core;
import engine.Input;
import static game.UnitType.BASIC_RANGED;
import graphics.Window2D;
import org.lwjgl.input.Keyboard;
import static util.Color4.gray;
import util.Vec2;

public class TestMain {

    public static void main(String[] args) {
        Core.init();

        Window2D.background = gray(.5);

        Unit u = new Unit(BASIC_RANGED, 0);
        u.create();

        for (int x = 5; x < 15; x++) {
            for (int y = 5; y < 15; y++) {
                Unit u2 = new Unit(BASIC_RANGED, 1);
                u2.create();
                u2.position.set(new Vec2(x, y).multiply(50));
            }
        }

        Input.mouseWheel.forEach(i -> Window2D.viewSize = Window2D.viewSize.multiply(Math.exp(i / -1000.)));
        double windowSpeed = 500;
        Input.whileKeyDown(Keyboard.KEY_W).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(0, windowSpeed * dt));
        });
        Input.whileKeyDown(Keyboard.KEY_A).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(-windowSpeed * dt, 0));
        });
        Input.whileKeyDown(Keyboard.KEY_S).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(0, -windowSpeed * dt));
        });
        Input.whileKeyDown(Keyboard.KEY_D).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(windowSpeed * dt, 0));
        });

        Core.run();
    }
}
