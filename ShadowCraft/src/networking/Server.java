package networking;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import network.Connection;
import network.NetworkUtils;
import static networking.MessageType.*;
import util.Log;
import util.Vec2;

public class Server {

    public static void main(String[] args) throws IOException {
        NetworkUtils.server(conn -> {
            ClientInfo client = new ClientInfo(conn);
            CLIENTS.add(client);
            Log.print("Client " + client.id + " connected");
            conn.onClose(() -> {
                CLIENTS.remove(client);
                Log.print("Client " + client.id + " disconnected");
            });

            registerMessageHandlers(client);
            client.conn.open();
            sendInitialData(client);
        }).start();

        //Core.init() and etc.
    }

    private static int maxUnitID = 0;

    private static void registerMessageHandlers(ClientInfo client) {
        handleMessage(client, CREATE_UNIT_CLIENT, data -> {
            int id = maxUnitID++;
            Vec2 pos = new Vec2(100 * 32, client.id % 2 == 0 ? 64 : 198 * 32);
            sendToAll(CREATE_UNIT, data[0], id, client.id, pos);
        });

        relayToAll(client, ORDER_IDLE, ORDER_MOVE, ORDER_ATTACK, UPDATE_TILE_HEALTH, UPDATE_TILE_TYPE, UPDATE_UNIT_HEALTH, UPDATE_UNIT_POSITION);
    }

    private static void sendInitialData(ClientInfo client) {
        sendTo(client, SET_CLIENT_TEAM, client.id);
    }

    private static final List<ClientInfo> CLIENTS = new LinkedList();

    private static class ClientInfo {

        static int maxID = 0;

        String name;
        Connection conn;
        int id = maxID++;

        public ClientInfo(Connection conn) {
            this.conn = conn;
        }

        @Override
        public String toString() {
            return "Client " + id + ": " + conn;
        }
    }

    private static void handleMessage(ClientInfo info, MessageType type, Consumer<Object[]> handler) {
        info.conn.registerHandler(type.id(), () -> {
            Object[] data = new Object[type.dataTypes.length];
            for (int i = 0; i < type.dataTypes.length; i++) {
                data[i] = info.conn.read(type.dataTypes[i]);
            }
            handler.accept(data);
            //ThreadManager.onMainThread(() -> handler.accept(data));
        });
    }

    private static void relayToAll(ClientInfo info, MessageType... types) {
        for (MessageType type : types) {
            handleMessage(info, type, data -> {
                System.out.println("Received message " + type + " from client " + info.id);
                sendToAll(type, data);
            });
        }
    }

    private static void relayToOthers(ClientInfo info, MessageType... types) {
        for (MessageType type : types) {
            handleMessage(info, type, data -> sendToOthers(info, type, data));
        }
    }

    private static void sendTo(ClientInfo info, MessageType type, Object... data) {
        if (!info.conn.isClosed()) {
            if (!type.verify(data)) {
                throw new RuntimeException("Data " + Arrays.toString(data) + " does not fit message type " + type);
            }
            info.conn.sendMessage(type.id(), data);
        }
    }

    private static void sendToAll(MessageType type, Object... data) {
        CLIENTS.forEach(ci -> sendTo(ci, type, data));
    }

    private static void sendToOthers(ClientInfo info, MessageType type, Object... data) {
        CLIENTS.stream().filter(ci -> ci != info).forEach(ci -> sendTo(ci, type, data));
    }
}
