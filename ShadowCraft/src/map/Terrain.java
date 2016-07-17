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

    public Terrain(int width, int height) {

        mWidth = width;
        mHeight = height;
        terMap = new Tile[width][height];
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
}
