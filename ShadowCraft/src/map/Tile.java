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

    private String name;
    private boolean solid;
    private int maxHealth;
    private int health;
    private int speed;
    private boolean opaque;
    private int penetrability;
    private String spriteName;
    private Tile noHealthTile;

    public Tile(String name, boolean solid, int maxHealth, int health, int speed, boolean opaque, int penetrability, String spriteName, Tile nhpTile) {

        this.name = name;
        this.solid = solid;
        this.maxHealth = maxHealth;
        this.health = health;
        this.speed = speed;
        this.opaque = opaque;
        this.penetrability = penetrability;
        this.spriteName = spriteName;
        this.noHealthTile = nhpTile;
    }

    public Tile(String name, boolean solid, int speed, boolean opaque, int penetrability, String spriteName) {

        this.name = name;
        this.solid = solid;
        this.maxHealth = -1;
        this.health = 0;
        this.speed = speed;
        this.opaque = opaque;
        this.penetrability = penetrability;
        this.spriteName = spriteName;
        this.noHealthTile = null;
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

    public String getSpriteName() {

        return spriteName;
    }

    public void setSpriteName(String spriteName) {

        this.spriteName = spriteName;
    }

    public int health(int hp) {

        health += hp;

        if (maxHealth != -1) {
            
            if (health > maxHealth) {

                hp += maxHealth - health;
                health = maxHealth;
                return hp;
            } else if (health < 0) {

                hp += health;
                return hp;
            }
        }
        
        return 0;
    }
}
