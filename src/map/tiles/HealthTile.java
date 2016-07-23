/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map.tiles;

import game.interfaces.Health;

/**
 *
 * @author Kosmic
 */
public class HealthTile extends Tile implements Health{
    
    protected int maxHealth;
    protected int health;
    protected Tile nhpTile;
    
    public HealthTile(int maxHealth, double speed, boolean opaque, double penetrability, String spriteName, Tile nhpTile) {
        
        super(speed, opaque, penetrability, spriteName);
        this.maxHealth = maxHealth;
        this.nhpTile = nhpTile;
    }

    @Override
    public void setHealth(int health) {

        this.health = health;
    }

    @Override
    public void setMaxHealth(int hp) {

        maxHealth = hp;
    }
    
    @Override
    public int getMaxHealth() {

        return maxHealth;
    }

    @Override
    public int getHealth() {

        return health;
    }
    
    @Override
    public int takeDamage(int hp) {

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
