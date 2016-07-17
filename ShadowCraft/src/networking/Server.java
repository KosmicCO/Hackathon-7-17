package networking;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import network.Connection;
import network.NetworkUtils;
import util.Log;
import util.ThreadManager;

public class Server {

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

    private static final List<ClientInfo> CLIENTS = new LinkedList();

    public static void main(String[] args) throws IOException {
        NetworkUtils.server(conn -> {
            ClientInfo client = new ClientInfo(conn);
            CLIENTS.add(client);
            Log.print("Client " + client.id + " connected");
            conn.onClose(() -> {
                CLIENTS.remove(client);
                Log.print("Client " + client.id + " disconnected");
            });

            handleAllMessages(client);
            client.conn.open();
            sendInitialData(client);
        }).start();

        //Core.init() and etc.
    }

    private static void handleAllMessages(ClientInfo info) {

    }

    private static void sendInitialData(ClientInfo info) {

    }

    private static void handleMessage(ClientInfo info, MessageType type, Consumer<Object[]> handler) {
        info.conn.registerHandler(type.id(), () -> {
            Object[] data = new Object[type.dataTypes.length];
            for (int i = 0; i < type.dataTypes.length; i++) {
                data[i] = info.conn.read(type.dataTypes[i]);
            }
            ThreadManager.onMainThread(() -> handler.accept(data));
        });
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
