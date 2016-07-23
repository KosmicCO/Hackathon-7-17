/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import util.Vec2;

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
    public Tile[][] terMap;
    private int seed;

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

    public static boolean isSolid(Vec2 pos, Vec2 size) {
        for (int i = (int) (pos.x - size.x); i <= pos.x + size.x; i++) {
            for (int j = (int) (pos.y - size.y); j <= pos.y + size.y; j++) {
                if (i < terrain.getWidth() && i >= 0 && j < terrain.getHeight() && j >= 0) {
                    if (terrain.terMap[i][j].getSpeed() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean updateTerrain(Vec2 pos) {

        boolean changed = false;

        for (int i = 0; i < VISION_RANGE * 2 + 1; i++) {

            for (int j = 0; j < VISION_RANGE * 2 + 1; j++) {

                if (i - VISION_RANGE + pos.x >= 0 && j - VISION_RANGE + pos.y >= 0 && i - VISION_RANGE
                        + pos.x < terrain.mWidth && j - VISION_RANGE + pos.y < terrain.mHeight) {

                    Tile t = terrain.terMap[i - VISION_RANGE + (int) pos.x][j - VISION_RANGE + (int) pos.y];
                    Tile tv = terrainVis.terMap[i - VISION_RANGE + (int) pos.x][j - VISION_RANGE + (int) pos.y];

                    if (!t.equals(tv)) {

                        terrainVis.terMap[i - VISION_RANGE + (int) pos.x][j - VISION_RANGE
                                + (int) pos.y] = terrain.terMap[i - VISION_RANGE
                                + (int) pos.x][j - VISION_RANGE + (int) pos.y];
                        changed = true;
                    }

                    if (!t.getSpriteName().equals(tv.getSpriteName())) {

                        terrainVis.terMap[i - VISION_RANGE + (int) pos.x][j - VISION_RANGE
                                + (int) pos.y] = terrain.terMap[i - VISION_RANGE
                                + (int) pos.x][j - VISION_RANGE + (int) pos.y];
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
