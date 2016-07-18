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
    private double speed;
    private boolean opaque;
    private double penetrability;
    private String spriteName;
    
    public Tile(boolean solid, double speed, boolean opaque, double penetrability, String spriteName) {

        this.solid = solid;
        this.speed = speed;
        this.opaque = opaque;
        this.penetrability = penetrability;
        this.spriteName = spriteName;
    }
    
    @Override
    public boolean equals(Object o){
        
        if(o instanceof Tile){
            
            Tile t = (Tile) o;
            return t.solid == solid && t.speed == speed && t.opaque == opaque && t.penetrability == penetrability;
        }
        
        return false;
    }

    public boolean isSolid() {

        return solid;
    }

    public double getSpeed() {

        return speed;
    }

    public boolean isOpaque() {

        return opaque;
    }

    public double getPenetrability() {

        return penetrability;
    }

    public void setSolid(boolean solid) {

        this.solid = solid;
    }

    public void setSpeed(double speed) {

        this.speed = speed;
    }

    public void setOpaque(boolean opaque) {

        this.opaque = opaque;
    }

    public void setPenetrability(double penetrability) {

        this.penetrability = penetrability;
    }

    public String getSpriteName() {

        return spriteName;
    }

    public void setSpriteName(String spriteName) {

        this.spriteName = spriteName;
    }
}
