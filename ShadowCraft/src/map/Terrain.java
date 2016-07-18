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

    public static Terrain terrain;
    public static Terrain terrainVis;
    public static final int VISION_RANGE = 10;

    private final int mWidth;
    private final int mHeight;
    private final Tile[][] terMap;
    private final int seed;

    public Terrain(Tile[][] map, int seed) {
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

    public int getSeed() {

        return seed;
    }

    public double[][] getSpeedMap() {

        double[][] svr = new double[mWidth][mHeight];

        for (int i = 0; i < mWidth; i++) {

            for (int j = 0; j < mHeight; j++) {

                Tile t = terMap[i][j];
                double speed = t.getSpeed();
                svr[i][j] = speed > 0 ? speed : (t instanceof HealthTile ? 0 : -1);
            }
        }

        return svr;
    }
    
    public static boolean updateTerrain(int x, int y){
        
        boolean changed = false;
        
        for (int i = 0; i < VISION_RANGE * 2 + 1; i++) {
            
            for (int j = 0; j < VISION_RANGE * 2 + 1; j++) {
                
                if(i - VISION_RANGE + x >= 0 && j - VISION_RANGE + y >= 0 && i - VISION_RANGE 
                        + x < terrain.mWidth && j - VISION_RANGE + y < terrain.mHeight){
                    
                    Tile t = terrain.terMap[i - VISION_RANGE + x][j - VISION_RANGE + y];
                    Tile tv = terrainVis.terMap[i - VISION_RANGE + x][j - VISION_RANGE + y];
                    
                    if(!t.equals(tv)){
                        
                        terrainVis.terMap[i - VISION_RANGE + x][j - VISION_RANGE + y] = terrain.terMap[i - VISION_RANGE + x][j - VISION_RANGE + y];
                        changed = true;
                    }
                    
                    if(!t.getSpriteName().equals(tv.getSpriteName())){
                            
                            terrainVis.terMap[i - VISION_RANGE + x][j - VISION_RANGE + y] = terrain.terMap[i - VISION_RANGE + x][j - VISION_RANGE + y];
                    }
                }
            }
        }
        
        return changed;
    }

    public static void addObject(int x, int y, Tile[][] obj) {

        for (int i = 0; i < obj.length; i++) {

            for (int j = 0; j < obj[0].length; j++) {

                if (obj[i][j] != null) {

                    terrain.terMap[i + x][j + y] = obj[i][j];
                    terrainVis.terMap[i + x][j + y] = obj[i][j];
                }
            }
        }
    }
}
