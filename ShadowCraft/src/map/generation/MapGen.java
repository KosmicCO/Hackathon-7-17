/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map.generation;

import java.util.Random;
import map.Terrain;
import map.Tile;

/**
 *
 * @author petercowal
 */
public class MapGen {
    long seed;
    private final int TILE_EMPTY = 0;
    private final int TILE_ROAD = 1;
    private final int TILE_WALL = 2;
    private final int TILE_DIRT = 3;
    private final int TILE_GRASS = 4;
    public MapGen(long seed){
        this.seed = seed;
    }
    public MapGen(){
        this(new Random().nextLong());
    }
    
    public static void main(String[] args) {
        new MapGen().generate(40, 40);
    }
    
    public Terrain generate(int width, int height){
        Random gen = new Random();
        gen.setSeed(seed);
        int[][] tilesTemp = new int[width][height];
        
        int startX, startY;
        
        
        
        generateRoad(tilesTemp, gen, 0, height/2, 0);
        generateRoad(tilesTemp, gen, width/2, height-1, 1);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                fill(tilesTemp, TILE_EMPTY, 2+gen.nextInt(3), i, j);
            }
        }
        
        
        char[] disp = new char[]{' ','.','#',' ','-'};
        for(int j = 0; j < height; j++){
            for(int i = 0; i < width; i++){
                
                System.out.print(disp[tilesTemp[i][j]] + " ");
            }
            System.out.println();
        }
        Tile[][] tiles = new Tile[width][height];
        //copy data over
        return new Terrain(tiles, (int)seed);
    }
    
    private void generateRoad(int[][] tiles, Random gen, int x, int y, int direction){
        int branchPoint = (2 + Math.abs(gen.nextInt()) % 5)*2;
        int dx = 0;
        int dy = 0;
        switch(direction%4){
            case 0: 
                dx = 1; break;
            case 1:
                dy = -1; break;
            case 2:
                dx = -1; break;
            default:
                dy = 1; break;
        }
        x += dx;
        y += dy;
        while(x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length 
                && tiles[x][y] == TILE_EMPTY && branchPoint > 0){
            tiles[x][y] = TILE_ROAD;
            x += dx;
            y += dy;
            branchPoint--;
        }
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length 
                && branchPoint == 0){
            tiles[x][y] = TILE_ROAD;
            if(gen.nextDouble()>0.3)generateRoad(tiles, gen, x, y, direction-1);
            if(gen.nextDouble()>0.3)generateRoad(tiles, gen, x, y, direction);
            if(gen.nextDouble()>0.3)generateRoad(tiles, gen, x, y, direction+1);
        }
    }
    private void fill(int[][] tiles, int oldValue, int newValue, int x, int y){
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length ){
            if(tiles[x][y]==oldValue){
                tiles[x][y]=newValue;
                fill(tiles, oldValue, newValue, x+1, y);
                fill(tiles, oldValue, newValue, x-1, y);
                fill(tiles, oldValue, newValue, x, y+1);
                fill(tiles, oldValue, newValue, x, y-1);
            }
        }
    }
    private void generateBorder(int[][] tiles, int x, int y, int w, int h){
        for(int i = x; i < x+w; i++){
            tiles[i][y] = TILE_ROAD;
            tiles[i][y+h] = TILE_ROAD;
        }
        for(int j = y; j <= y+h; j++){
            tiles[x][j] = TILE_ROAD;
            tiles[x+w][j] = TILE_ROAD;
        }
    }
    public static Tile roadTile(){
        return null;
        //return new Tile(false, -1, -1, 1, false, 100);
    }
    public static Tile wallTile(){
        return null;
        //return new Tile(true, 100, 100, 1, true, 0);
    }
}
