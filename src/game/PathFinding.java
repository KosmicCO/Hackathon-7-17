/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import map.Terrain;
import map.generation.MapGen;
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

    public static void main(String[] args) {
        Terrain t = (new MapGen(100).generate(40, 40));
        double[][] speedMap = t.getSpeedMap();
        PathFinding p = new PathFinding(speedMap, new Vec2(10, 11));
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 20; i++) {
                if (i == 10 && j == 11) {
                    System.out.print("@");
                } else {
                    System.out.print(speedMap[i][j] > 0 ? "." : "#");
                }

            }
            System.out.println();
        }
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 20; i++) {
                if(p.distMap[i][j] != Double.POSITIVE_INFINITY)
                System.out.printf("[% 3.0f]", p.distMap[i][j]);
                else{
                    System.out.print("[inf]");
                }
            }
            System.out.println();
        }

    }

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
        int index = 0;

        double[][] dm = new double[width][height];
        dm[(int) goal.x][(int) goal.y] = 1;

        for (int i = 0; i < 7; i++) {

            if (i != 0 && i != 2 && i != 5) {

                Vec2 adj = goal.add(new Vec2(ADX[i], ADY[i]));

                if (adj.x < width && adj.y < height && adj.x >= 0 && adj.y >= 0) {

                    dm[(int) adj.x][(int) adj.y] = -2;
                    nl[index] = adj;
                    index++;
                }
            }
        }

        for (int i = 0; i < width * height - 1; i++) {

            Vec2 t = nl[i];
            if (t == null) {
                break;
            }
            double ss = Double.POSITIVE_INFINITY;
            if (speedMap[(int) t.x][(int) t.y] != Double.POSITIVE_INFINITY) {
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
            }
            dm[(int) t.x][(int) t.y] = ss + speedMap[(int) t.x][(int) t.y];

        }

        return dm;
    }
}
