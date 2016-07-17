package game;

import engine.Core;
import engine.Input;
import engine.Signal;
import examples.Premade2D;
import graphics.Graphics2D;
import java.util.HashSet;
import java.util.Set;
import util.Color4;
import static util.Color4.WHITE;
import util.Mutable;
import util.RegisteredEntity;
import util.Vec2;

public class Unit extends RegisteredEntity {

    public static final Set<Unit> selected = new HashSet();

    static {
        Mutable<Vec2> boxStart = new Mutable(new Vec2(0));
        Input.whenMouse(0, true).onEvent(() -> boxStart.o = Input.getMouse());
        Signal<Boolean> drawingBox = Core.render.map(() -> Input.mouseSignal(0).get() && Input.getMouse().subtract(boxStart.o).lengthSquared() > 10);
        Core.render.filter(drawingBox).onEvent(() -> {
            Graphics2D.fillRect(boxStart.o, Input.getMouse().subtract(boxStart.o), new Color4(.2, .5, 1, .2));
            Graphics2D.drawRect(boxStart.o, Input.getMouse().subtract(boxStart.o), new Color4(.1, .2, .5));
        });
        Input.whenMouse(0, false).filter(drawingBox).onEvent(() -> {
            selected.clear();
            RegisteredEntity.getAll(Unit.class).forEach(u -> {
                if (u.position.get().containedBy(boxStart.o, Input.getMouse())) {
                    selected.add(u);
                }
            });
        });
//        Core.update.forEach(dt -> {
//            System.out.println(boxStart.o);
//            System.out.println(drawingBox.get());
//            System.out.println(Input.getMouse().subtract(boxStart.o).lengthSquared() > 10);
//            System.out.println(Input.mouseSignal(0).get());
//        });
    }

    public Signal<Vec2> position, velocity;
    public Signal<Double> rotation;
    public Vec2 size;
    public final UnitType type;

    public Unit(UnitType type) {
        this.type = type;
    }

    @Override
    protected void createInner() {
        position = Premade2D.makePosition(this);
        velocity = Premade2D.makeVelocity(this);
        rotation = Premade2D.makeRotation(this);
        Premade2D.makeSpriteGraphics(this, type.spriteName);

        Core.render.onEvent(() -> {
            if (selected.contains(this)) {
                Graphics2D.drawEllipse(position.get(), size, WHITE, 20);
            }
        });
    }
}
