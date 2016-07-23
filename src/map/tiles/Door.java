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
public class Door extends HealthTile{
    
    protected boolean open;
    protected String openSpriteName;
    
    public Door(int maxHealth, double speed, boolean opaque, double penetrability, String closedSpriteName, String openSpriteName, Tile nhpTile) {
        
        super(maxHealth, speed, opaque, penetrability, closedSpriteName, nhpTile);
        open = false;
        this.openSpriteName = openSpriteName;
    }

    @Override
    public String getSpriteName() {
        
        return open ? openSpriteName : spriteName;
    }
    
    @Override
    public boolean isSolid(){
        
        return !open;
    }

    public boolean isOpen() {
        
        return open;
    }

    public void setOpen(boolean open) {
        
        this.open = open;
    }

    public String getOpenSpriteName() {
        
        return openSpriteName;
    }

    public void setOpenSpriteName(String openSpriteName) {
        
        this.openSpriteName = openSpriteName;
    }
    
    public String getClosedSpriteName() {
        
        return spriteName;
    }
}
