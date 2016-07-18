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

    private final int mWidth;
    private final int mHeight;
    private Tile[][] terMap;
    private int seed;

    public Terrain(Tile[][] map, int seed) {
        terrain = this;
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

    public void addObject(int x, int y, Tile[][] obj) {

        for (int i = 0; i < obj.length; i++) {

            for (int j = 0; j < obj[0].length; j++) {

                if (obj[i][j] != null) {

                    terMap[i + x][j + y] = obj[i][j];
                }
            }
        }
    }
}
