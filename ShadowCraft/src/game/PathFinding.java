/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import util.Vec2;

/**
 *
 * @author Kosmic
 */
public class PathFinding {

    private static final int[] ADX = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] ADY = {-1, -1, -1, 0, 0, 1, 1, 1};

    private double[][] speedMap;
    private double[][] distMap;
    private Vec2 goal;
    private int width;
    private int height;

    public PathFinding(double[][] speedMap, Vec2 goal) {

        this.speedMap = toNonDestructableMap(speedMap);
        this.width = speedMap.length;
        this.height = speedMap[0].length;
        this.goal = goal;
        distMap = generateDistanceMap();
    }

    private static double[][] toNonDestructableMap(double[][] speedMap) {

        double[][] sm = new double[speedMap.length][speedMap[0].length];

        for (int i = 0; i < speedMap.length; i++) {

            for (int j = 0; j < speedMap[0].length; j++) {

                sm[i][j] = speedMap[i][j] > 0 ? 1 / speedMap[i][j] : Double.POSITIVE_INFINITY;
            }
        }

        return sm;
    }

    private double[][] generateDistanceMap() {

        Vec2[] nl = new Vec2[width * height - 1];
        nl[0] = goal.add(new Vec2(goal.x == 0 ? 1 : -1, 0));
        int index = 1;

        double[][] dm = new double[width][height];
        dm[(int) goal.x][(int) goal.y] = 1;

        for (int i = 0; i < width * height - 1; i++) {

            Vec2 t = nl[i];
            double ss = Double.POSITIVE_INFINITY;

            for (int j = 0; j < 8; j++) {

                Vec2 adj = t.add(new Vec2(ADX[j], ADY[j]));

                if (adj.x < width && adj.y < height && adj.x >= 0 && adj.y >= 0) {

                    double speed = dm[(int) adj.x][(int) adj.y];

                    if (ss > speed && speed > 0) {

                        ss = speed;
                    } else if (speed == 0) {

                        dm[(int) adj.x][(int) adj.y] = -2;
                        nl[index] = adj;
                        index++;
                    }
                }
            }

            dm[(int) t.x][(int) t.y] = ss + speedMap[(int) t.x][(int) t.y];
        }

        return dm;
    }
}
