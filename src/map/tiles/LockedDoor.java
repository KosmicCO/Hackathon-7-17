/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map.tiles;

/**
 *
 * @author Kosmic
 */
public class LockedDoor extends Door{
    
    public int team;
    
    public LockedDoor(int maxHealth, double speed, boolean opaque, double penetrability, String closedSpriteName, String openSpriteName, Tile nhpTile) {
        
        super(maxHealth, speed, opaque, penetrability, closedSpriteName, openSpriteName, nhpTile);
        team = -1;
    }

    public int getTeam() {
        
        return team;
    }

    public void setTeam(int team) {
        
        this.team = team;
    }

    @Override
    public void interact(int team) {
        
        if(team == this.team){
            
            super.interact(team);
        }
    }
}
