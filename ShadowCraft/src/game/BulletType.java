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
public enum BulletType {
    
    MELEE("confetti"),
    FIST("rory soiffer"),
    HEALTH("plus sign thing"),
    SNIPER("evility"),
    ASSAULT("evil evility"),
    PISTOL("u is ded");
    
    private final String sprite;

    BulletType(String sprite){
        
        this.sprite = sprite;
    }
}
