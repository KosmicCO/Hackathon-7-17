package networking;

import game.UnitType;
import java.util.Arrays;
import network.NetworkUtils;
import util.Vec2;

public enum MessageType {

    CREATE_UNIT_CLIENT(UnitType.class), //Unit type
    CREATE_UNIT(UnitType.class, Integer.class, Integer.class, Vec2.class), //Unit type, unit id, unit team, position

    ORDER_IDLE(Integer.class), //Unit id
    ORDER_MOVE(Integer.class, Vec2.class), //Unit id, position
    ORDER_ATTACK(Integer.class, Integer.class), //Unit id, target id

    SET_CLIENT_TEAM(Integer.class), //team

    UPDATE_TILE_HEALTH(Integer.class, Integer.class, Double.class), //x, y, health
    UPDATE_TILE_TYPE(Integer.class, Integer.class, Integer.class), //x, y, tile type

    UPDATE_UNIT_HEALTH(Integer.class, Integer.class), //Unit id, health
    UPDATE_UNIT_POSITION(Integer.class, Vec2.class, Vec2.class); //Unit id, position, velocity

    static {
        NetworkUtils.registerType(UnitType.class, c -> UnitType.valueOf(c.read(String.class)), (c, u) -> c.write(u.name()));
    }

    public final Class[] dataTypes;

    private MessageType(Class... dataTypes) {
        this.dataTypes = dataTypes;
    }

    public int id() {
        return Arrays.asList(values()).indexOf(this);
    }

    public boolean verify(Object... data) {
        if (dataTypes.length != data.length) {
            return false;
        }
        for (int i = 0; i < data.length; i++) {
            if (!dataTypes[i].isInstance(data[i])) {
                return false;
            }
        }
        return true;
    }
}
