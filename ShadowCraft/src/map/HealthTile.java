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
public class HealthTile extends Tile{
    
    private int maxHealth;
    private int health;
    private Tile nhpTile;
    
    public HealthTile(boolean solid, int maxHealth, int health, int speed, boolean opaque, int penetrability, String spriteName, Tile nhpTile) {
        
        super(solid, speed, opaque, penetrability, spriteName);
        this.maxHealth = maxHealth;
        this.health = health;
        this.nhpTile = nhpTile;
    }
    
    public void setMaxHealth(int maxHealth) {

        this.maxHealth = maxHealth;
    }

    public void setHealth(int health) {

        this.health = health;
    }
    
    public int getMaxHealth() {

        return maxHealth;
    }

    public int getHealth() {

        return health;
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
