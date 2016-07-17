package networking;

import game.UnitType;
import java.util.Arrays;
import util.Vec2;

public enum MessageType {

    CREATE_UNIT(UnitType.class, Integer.class),
    DAMAGE_UNIT(Integer.class),
    ORDER_MOVE(Integer.class, Vec2.class),
    ORDER_ATTACK(Integer.class, Integer.class),
    UPDATE_UNIT_POSITION(Integer.class, Vec2.class);

//    SCORE(String.class), //who got a point
//    GET_NAME(String.class), //retrieve client name
//
//    SNOWBALL(Vec3.class, Vec3.class, Integer.class), //position, velocity, id
//
//    HIT(Vec3.class, Integer.class), //position, id
//
//    CHAT_MESSAGE(String.class), //the contents of the message
//
//    BLOCK_PLACE(Vec3.class, Integer.class), //position, cube type id
//
//    MAP_FILE(String.class), //map name
//
//    MODEL_PLACE(Vec3.class, Integer.class),
//    RESTART(); //no information needed
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
