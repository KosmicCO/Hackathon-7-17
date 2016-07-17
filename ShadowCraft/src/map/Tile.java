/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

/**
 *
 * @author Kosmic
 */
public class Tile {

    public static final int RESOLUTION = 32;

    private boolean solid;
    private int speed;
    private boolean opaque;
    private int penetrability;
    private String spriteName;

    public Tile(boolean solid, int speed, boolean opaque, int penetrability, String spriteName) {

        this.solid = solid;
        this.speed = speed;
        this.opaque = opaque;
        this.penetrability = penetrability;
        this.spriteName = spriteName;
    }

    public boolean isSolid() {

        return solid;
    }

    public int getSpeed() {

        return speed;
    }

    public boolean isOpaque() {

        return opaque;
    }

    public int getPenetrability() {

        return penetrability;
    }

    public void setSolid(boolean solid) {

        this.solid = solid;
    }

    public void setSpeed(int speed) {

        this.speed = speed;
    }

    public void setOpaque(boolean opaque) {

        this.opaque = opaque;
    }

    public void setPenetrability(int penetrability) {

        this.penetrability = penetrability;
    }

    public String getSpriteName() {

        return spriteName;
    }

    public void setSpriteName(String spriteName) {

        this.spriteName = spriteName;
    }
}
