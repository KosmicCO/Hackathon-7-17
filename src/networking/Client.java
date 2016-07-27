package networking;

import engine.Core;
import engine.Input;
import game.Order.AttackOrder;
import game.Order.IdleOrder;
import game.Order.MoveOrder;
import game.Unit;
import game.UnitType;
import static game.UnitType.BASIC_RANGED;
import static game.UnitType.HELICOPTER;
import game.World;
import java.util.Arrays;
import java.util.function.Consumer;
import network.Connection;
import network.NetworkUtils;
import static networking.MessageType.*;
import org.lwjgl.input.Keyboard;
import util.ThreadManager;
import util.Vec2;

public abstract class Client {

    public static boolean IS_MULTIPLAYER = true;
    private static Connection conn;

    public static void main(String[] args) {
        conn = NetworkUtils.connectManual();
        registerMessageHandlers();
        Core.timer(.5, conn::open);

        Core.init();

        World.init();

        Input.whenKey(Keyboard.KEY_U, true).onEvent(()
                -> sendMessage(CREATE_UNIT_CLIENT, BASIC_RANGED));

        Input.whenKey(Keyboard.KEY_H, true).onEvent(()
                -> sendMessage(CREATE_UNIT_CLIENT, HELICOPTER));

        Core.run();

        System.exit(0);
    }

    public static void registerMessageHandlers() {
        handleMessage(CREATE_UNIT, data -> {
            Unit u = new Unit((UnitType) data[0], (Integer) data[2]);
            u.create();
            u.id = (Integer) data[1];
            u.position.set((Vec2) data[3]);
        });

        handleMessage(ORDER_IDLE, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            if (u == null) {
                return;
            }
            u.order.set(new IdleOrder(u));
        });
        handleMessage(ORDER_MOVE, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            if (u == null) {
                return;
            }
            u.order.set(new MoveOrder(u, (Vec2) data[1]));
        });
        handleMessage(ORDER_ATTACK, data -> {
            Unit u = Unit.findById((Integer) data[0]);
            if (u == null) {
                return;
            }
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
            if (u == null) {
                return;
            }
            u.position.set((Vec2) data[1]);
            u.velocity.set((Vec2) data[2]);
        });
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
