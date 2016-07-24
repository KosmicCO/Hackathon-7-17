/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map.generation;

import java.util.Random;
import map.Terrain;
import map.tiles.Tile;

/**
 *
 * @author petercowal
 */
public class MapGen {

    long seed;
    private final int TILE_EMPTY = 0;
    private final int TILE_BORDER = 6;
    private final int TILE_ROAD = 1;
    private final int TILE_WALL = 2;
    private final int TILE_DIRT = 4;
    private final int TILE_GRASS = 3;
    private final int TILE_TREE = 5;
    private final int TILE_SIDEWALK = 7;

    public MapGen(long seed) {
        this.seed = seed;
    }

    public MapGen() {
        this(new Random().nextLong());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new MapGen().generate(100, 100);
            System.out.println();
        }
    }

    public Terrain generate(int width, int height) {
        Random gen = new Random();
        gen.setSeed(seed);
        int[][] tilesTemp = new int[width][height];

        int startX, startY;

        generateRoad(tilesTemp, gen, width / 2, 0, 3);
        generateRoad(tilesTemp, gen, width / 2, height - 1, 1);

        generateBorder(tilesTemp, TILE_BORDER, 0, 0, width - 1, height - 1);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (tilesTemp[i][j] == TILE_EMPTY) {
                    int fillWith = 2 + gen.nextInt(2);
                    fill(tilesTemp, TILE_EMPTY, fillWith, i, j);

                    if (fillWith == TILE_WALL) {
                        outline(tilesTemp, TILE_WALL, TILE_SIDEWALK, i, j);
                        generateSidewalk(tilesTemp, gen, i + 5, j, 3);
                        generateSidewalk(tilesTemp, gen, i, j + 10, 0);
                    }
                }

            }
        }
        addRandomTrees(tilesTemp, gen, 0.1);

        Tile[][] tiles = new Tile[width][height];
        //copy data over

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                switch (tilesTemp[i][j]) {
                    case TILE_ROAD:
                        tiles[i][j] = MapGen.roadTile();
                        break;
                    case TILE_WALL:
                        tiles[i][j] = MapGen.wallTile();
                        break;
                    case TILE_GRASS:
                        tiles[i][j] = MapGen.grassTile();
                        break;
                    case TILE_TREE:
                        tiles[i][j] = MapGen.treeTile();
                        break;
                    case TILE_SIDEWALK:
                        tiles[i][j] = MapGen.sidewalkTile();
                        break;
                    case TILE_BORDER:
                        tiles[i][j] = MapGen.borderTile();
                        break;
                    default:
                        tiles[i][j] = MapGen.roadTile();
                        break;

                }
            }
        }

        return new Terrain(tiles, (int) seed);
    }

    private void addBuildings(int[][] tiles, Random gen) {
        for (int i = 0; i < 100; i++) {

        }
    }

    private void addRandomTrees(int[][] tiles, Random gen, double prob) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {

                if (tiles[i][j] == TILE_GRASS && gen.nextDouble() < prob) {
                    tiles[i][j] = TILE_TREE;
                }
            }
        }
    }

    private void generateSidewalk(int[][] tiles, Random gen, int x, int y, int direction) {
        int branchPoint = (2 + Math.abs(gen.nextInt()) % 2) * 2;
        int dx = 0;
        int dy = 0;
        switch (direction % 4) {
            case 0:
                dx = 1;
                break;
            case 1:
                dy = -1;
                break;
            case 2:
                dx = -1;
                break;
            default:
                dy = 1;
                break;
        }
        x += dx;
        y += dy;
        while (x > 0 && x < tiles.length - 1 && y > 0 && y < tiles[0].length - 1
                && tiles[x][y] == TILE_WALL && branchPoint > 0) {
            tiles[x][y] = TILE_SIDEWALK;
            x += dx;
            y += dy;
            branchPoint--;
        }
        if (x > 0 && x < tiles.length - 1 && y > 0 && y < tiles[0].length - 1
                && branchPoint == 0) {
            tiles[x][y] = TILE_SIDEWALK;

            boolean generated = false;
            while (!generated) {
                if (gen.nextDouble() > 0.2) {
                    generateSidewalk(tiles, gen, x, y, direction - 1);
                    generated = true;
                }
                if (gen.nextDouble() > 0.3) {
                    generateSidewalk(tiles, gen, x, y, direction);
                    generated = true;
                }
                if (gen.nextDouble() > 0.2) {
                    generateSidewalk(tiles, gen, x, y, direction + 1);
                    generated = true;
                }
            }
        }
    }

    private void generateRoad(int[][] tiles, Random gen, int x, int y, int direction) {
        int branchPoint = (2 + Math.abs(gen.nextInt()) % 5) * 5;
        int dx = 0;
        int dy = 0;
        switch (direction % 4) {
            case 0:
                dx = 1;
                break;
            case 1:
                dy = -1;
                break;
            case 2:
                dx = -1;
                break;
            default:
                dy = 1;
                break;
        }
        x += dx;
        y += dy;
        while (x > 0 && x < tiles.length - 1 && y > 0 && y < tiles[0].length - 1
                && tiles[x][y] == TILE_EMPTY && branchPoint > 0) {
            tiles[x][y] = TILE_ROAD;
            tiles[x - dy][y - dx] = TILE_ROAD;
            tiles[x + dy][y + dx] = TILE_ROAD;
            x += dx;
            y += dy;
            branchPoint--;
        }
        if (x > 0 && x < tiles.length - 1 && y > 0 && y < tiles[0].length - 1
                && branchPoint == 0) {
            tiles[x][y] = TILE_ROAD;
            tiles[x - dy][y - dx] = TILE_ROAD;
            tiles[x + dy][y + dx] = TILE_ROAD;
            tiles[x + dx][y + dy] = TILE_ROAD;
            tiles[x + dx - dy][y + dy - dx] = TILE_ROAD;
            tiles[x + dx + dy][y + dy + dx] = TILE_ROAD;
            boolean generated = false;
            while (!generated) {
                if (gen.nextDouble() > 0.3) {
                    generateRoad(tiles, gen, x - dy, y + dx, direction - 1);
                    generated = true;
                }
                if (gen.nextDouble() > 0.3) {
                    generateRoad(tiles, gen, x + dx, y + dy, direction);
                    generated = true;
                }
                if (gen.nextDouble() > 0.3) {
                    generateRoad(tiles, gen, x + dy, y - dx, direction + 1);
                    generated = true;
                }
            }
        }
    }

    private void fill(int[][] tiles, int value, int x, int y) {
        fill(tiles, tiles[x][y], value, x, y);
    }

    private void fill(int[][] tiles, int oldValue, int newValue, int x, int y) {

        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
            if (tiles[x][y] == oldValue) {
                tiles[x][y] = newValue;
                fill(tiles, oldValue, newValue, x + 1, y);
                fill(tiles, oldValue, newValue, x - 1, y);
                fill(tiles, oldValue, newValue, x, y + 1);
                fill(tiles, oldValue, newValue, x, y - 1);
            }
        }
    }

    private void outline(int[][] tiles, int oldValue, int newValue, int x, int y) {
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
            if (tiles[x][y] == oldValue) {
                boolean border = false;
                if (tiles[x + 1][y] != oldValue && tiles[x + 1][y] != newValue) {
                    border = true;
                }
                if (tiles[x - 1][y] != oldValue && tiles[x - 1][y] != newValue) {
                    border = true;
                }
                if (tiles[x][y + 1] != oldValue && tiles[x][y + 1] != newValue) {
                    border = true;
                }
                if (tiles[x][y - 1] != oldValue && tiles[x][y - 1] != newValue) {
                    border = true;
                }
                if (border) {
                    tiles[x][y] = newValue;
                    outline(tiles, oldValue, newValue, x + 1, y);
                    outline(tiles, oldValue, newValue, x - 1, y);
                    outline(tiles, oldValue, newValue, x, y + 1);
                    outline(tiles, oldValue, newValue, x, y - 1);
                }
            }
        }
    }

    private void generateBorder(int[][] tiles, int value, int x, int y, int w, int h) {
        for (int i = x; i < x + w; i++) {
            tiles[i][y] = value;
            tiles[i][y + h] = value;
        }
        for (int j = y; j <= y + h; j++) {
            tiles[x][j] = value;
            tiles[x + w][j] = value;
        }
    }

    public static Tile roadTile() {
        return new Tile(1.4, false, 1, "road");
    }

    public static Tile wallTile() {
        return new Tile(0, true, 0, "wall");
    }

    public static Tile grassTile() {
        return new Tile(0.9, false, 1, "grass");
    }

    public static Tile treeTile() {
        return new Tile(0, true, 0, "tree");
    }

    public static Tile sidewalkTile() {
        return new Tile(1.2, false, 1, "sidewalk");
    }

    public static Tile borderTile() {
        return new Tile(1, false, 1, "border");
    }
}
