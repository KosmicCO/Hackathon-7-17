/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.interfaces;

/**
 *
 * @author Kosmic
 */
public interface Health {
    
    int getMaxHealth();
    
    int getHealth();
    
    void setMaxHealth(int hp);
    
    void setHealth(int hp);
    
    int takeDamage(int hp);
}
