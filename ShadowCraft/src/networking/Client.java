package networking;

import engine.Core;
import engine.Input;
import game.Order.AttackOrder;
import game.Order.IdleOrder;
import game.Order.MoveOrder;
import game.Unit;
import game.UnitType;
import graphics.Window2D;
import java.util.Arrays;
import java.util.function.Consumer;
import network.Connection;
import network.NetworkUtils;
import static networking.MessageType.*;
import org.lwjgl.input.Keyboard;
import static util.Color4.gray;
import util.ThreadManager;
import util.Vec2;

public abstract class Client {

    public static boolean IS_MULTIPLAYER = true;
    private static Connection conn;

    public static void main(String[] args) {
        connect("localhost");

        Core.init();

        Window2D.background = gray(.5);

        Input.whenKey(Keyboard.KEY_U, true).onEvent(()
                -> sendMessage(CREATE_UNIT_CLIENT, UnitType.BASIC_RANGED));

//        Unit u = new Unit(BASIC_RANGED, 0);
//        u.create();
//
//        for (int x = 5; x < 7; x++) {
//            for (int y = 5; y < 7; y++) {
//                Unit u2 = new Unit(BASIC_RANGED, 1);
//                u2.create();
//                u2.position.set(new Vec2(x, y).multiply(50));
//            }
//        }
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

    public static void connect(String ip) {
        if (IS_MULTIPLAYER) {
            conn = NetworkUtils.connect(ip);
            registerMessageHandlers();
            Core.timer(.5, conn::open);
        }
    }

    public static void handleMessage(MessageType type, Consumer<Object[]> handler) {
        conn.registerHandler(type.id(), () -> {
            Object[] data = new Object[type.dataTypes.length];
            for (int i = 0; i < type.dataTypes.length; i++) {
                data[i] = conn.read(type.dataTypes[i]);
            }
            ThreadManager.onMainThread(() -> handler.accept(data));
        });
    }

    public static void registerMessageHandlers() {
        handleMessage(CREATE_UNIT, data -> {
            Unit u = new Unit((UnitType) data[0], (Integer) data[2]);
            u.create();
            u.id = (Integer) data[1];
        });

        handleMessage(ORDER_IDLE, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            u.order.set(new IdleOrder(u));
        });
        handleMessage(ORDER_MOVE, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            u.order.set(new MoveOrder(u, (Vec2) data[1]));
        });
        handleMessage(ORDER_ATTACK, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            u.order.set(new AttackOrder(u, Unit.findById((Integer) data[1])));
        });

        handleMessage(SET_CLIENT_TEAM, data -> {
            Unit.myTeam = (Integer) data[0];
        });

        handleMessage(UPDATE_UNIT_HEALTH, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            if (u == null) {
                return;
            }
            u.health.set((Integer) data[1]);
            if (u.health.get() <= 0) {
                u.destroy();
            }
        });
        handleMessage(UPDATE_UNIT_POSITION, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            u.position.set((Vec2) data[1]);
        });
    }

    public static void sendMessage(MessageType type, Object... contents) {
        if (conn != null && !conn.isClosed()) {
            if (!type.verify(contents)) {
                throw new RuntimeException("Data " + Arrays.toString(contents) + " does not fit message type " + type);
            }
            conn.sendMessage(type.id(), contents);
        } else {
            System.out.println("Failed to send message");
        }
    }
}
