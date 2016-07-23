package game;

import engine.Core;
import engine.Input;
import engine.Signal;
import graphics.Graphics2D;
import graphics.Window2D;
import graphics.data.Texture;
import graphics.loading.SpriteContainer;
import java.util.Arrays;
import map.Terrain;
import map.tiles.Tile;
import map.generation.MapGen;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import static org.newdawn.slick.opengl.renderer.SGL.GL_QUADS;
import static org.newdawn.slick.opengl.renderer.SGL.GL_TEXTURE_2D;
import static util.Color4.WHITE;
import static util.Color4.gray;
import util.Vec2;

public class World {

    public static void init() {
        Window2D.background = gray(.5);

        Vec2 wSize = Window2D.viewSize;
        Signal<Integer> windowScale = new Signal(0);
        Input.mouseWheel.forEach(i -> windowScale.edit(x -> x - i / 120));
        windowScale.filter(i -> i < -5).forEach(i -> windowScale.set(-5));
        windowScale.filter(i -> i > 5).forEach(i -> windowScale.set(5));
        windowScale.forEach(i -> Window2D.viewSize = wSize.multiply(Math.exp(i / 3.0)));

        Signal<Double> windowSpeed = windowScale.map(i -> 500. * Math.exp(i / 3.0));
        Input.whileKeyDown(Keyboard.KEY_W).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(0, windowSpeed.get() * dt));
        });
        Input.whileKeyDown(Keyboard.KEY_A).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(-windowSpeed.get() * dt, 0));
        });
        Input.whileKeyDown(Keyboard.KEY_S).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(0, -windowSpeed.get() * dt));
        });
        Input.whileKeyDown(Keyboard.KEY_D).forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.add(new Vec2(windowSpeed.get() * dt, 0));
        });
        Core.update.forEach(dt -> {
            Window2D.viewPos = Window2D.viewPos.clamp(Window2D.viewSize.divide(2), new Vec2(200 * Tile.RESOLUTION).subtract(Window2D.viewSize.divide(2)));
        });

        Terrain terrain = new MapGen(0).generate(200, 200);
        Terrain.terrain = terrain;
        Terrain.terrainVis = terrain;
        Core.renderLayer(-1).onEvent(() -> {
            glEnable(GL_TEXTURE_2D);
            WHITE.glColor();
            Arrays.asList("border", "grass", "road", "sidewalk", "tree", "wall").forEach(texture -> {
                Texture tex = SpriteContainer.loadSprite(texture);
                tex.bind();
                glBegin(GL_QUADS);
                for (int x = 0; x < terrain.getWidth(); x++) {
                    for (int y = 0; y < terrain.getHeight(); y++) {
                        Tile t = terrain.terMap[x][y];
                        if (t.getSpriteName().equals(texture)) {
                            Graphics2D.drawSpriteFast(tex,
                                    new Vec2(x * Tile.RESOLUTION, y * Tile.RESOLUTION),
                                    new Vec2((x + 1) * Tile.RESOLUTION, y * Tile.RESOLUTION),
                                    new Vec2((x + 1) * Tile.RESOLUTION, (y + 1) * Tile.RESOLUTION),
                                    new Vec2(x * Tile.RESOLUTION, (y + 1) * Tile.RESOLUTION));
                        }
                    }
                }
                glEnd();
            });
        });
    }
}
