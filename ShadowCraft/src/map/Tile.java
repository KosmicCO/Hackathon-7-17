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
    
    private boolean solid;
    private int maxHealth;
    private int health;
    private int speed;
    private boolean opaque;
    private int penetrability;
    
    public Tile(boolean solid, int maxHealth, int health, int speed, boolean opaque, int penetrability){
        
        this.solid = solid;
        this.maxHealth = maxHealth;
        this.health = health;
        this.speed = speed;
        this.opaque = opaque;
        this.penetrability = penetrability;
    }

    public boolean isSolid() {
        
        return solid;
    }

    public int getMaxHealth() {
        
        return maxHealth;
    }

    public int getHealth() {
        
        return health;
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

    public void setMaxHealth(int maxHealth) {
        
        this.maxHealth = maxHealth;
    }

    public void setHealth(int health) {
        
        this.health = health;
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
}
