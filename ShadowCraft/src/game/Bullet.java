/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author Kosmic
 */
public class Bullet {
    
    private double speed;
    private double distance;
    private int damage;
    private int damageRange;
    
    public Bullet(double speed, double distance, int damage, int damageRange){
        
        this.speed = speed;
        this.distance = distance;
        this.damage = damage;
        this.damageRange = damageRange;
    }

    public double getSpeed() {
        
        return speed;
    }

    public double getDistance() {
        
        return distance;
    }

    public int getDamage() {
        return damage;
    }

    public int getDamageRange() {
        return damageRange;
    }
}
