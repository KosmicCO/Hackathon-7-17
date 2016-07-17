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
public class Terrain {

    private int mWidth;
    private int mHeight;
    private Tile[][] terMap;
    private int seed;
   
    public Terrain(Tile[][] map, int seed){
        
        terMap = map;
        mWidth = map.length;
        mHeight = map[0].length;
        this.seed = seed;
    }

    public int getWidth() {
        
        return mWidth;
    }

    public int getHeight() {
        
        return mHeight;
    }

    public Tile[][] getMap() {
        
        return terMap;
    }   
    
    public int getSeed(){
        
        return seed;
    }
}
