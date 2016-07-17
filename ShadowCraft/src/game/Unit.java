package game;

import engine.Core;
import engine.Input;
import engine.Signal;
import examples.Premade2D;
import game.Order.AttackOrder;
import game.Order.IdleOrder;
import game.Order.MoveOrder;
import graphics.Graphics2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.Color4;
import static util.Color4.*;
import util.Mutable;
import util.RegisteredEntity;
import util.Vec2;

public class Unit extends RegisteredEntity {

    public static int myTeam = 0;
    public static final Set<Unit> selected = new HashSet();

    public static List<Color4> teamColors = Arrays.asList(
            new Color4(0, 0, 1),
            new Color4(1, 0, 0),
            new Color4(0, 1, 0),
            new Color4(1, .5, 0)
    );

    static {
        Core.update.forEach(dt -> selected.removeIf(u -> u.isDestroyed()));
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
                if (u.unitTeam == myTeam) {
                    if (u.position.get().containedBy(boxStart.o, Input.getMouse())) {
                        selected.add(u);
                    }
                }
            });
        });

        Input.whenMouse(1, true).onEvent(() -> {
            Mutable<Unit> target = new Mutable(null);
            for (Unit u : RegisteredEntity.getAll(Unit.class)) {
                if (u.unitTeam != myTeam) {
                    if (Input.getMouse().containedBy(u.position.get().add(u.size), u.position.get().subtract(u.size))) {
                        target.o = u;
                        break;
                    }
                }
            }
            if (target.o == null) {
                selected.forEach(u -> u.order.set(new MoveOrder(u, Input.getMouse())));
            } else {
                selected.forEach(u -> u.order.set(new AttackOrder(u, target.o)));
            }
        });
    }

    public Signal<Vec2> position, velocity;
    public Signal<Double> rotation;
    public Signal<Order> order = new Signal(new IdleOrder(this));
    public Vec2 size = new Vec2(20);
    public final UnitType type;
    public int unitTeam;
    public Signal<Integer> health = new Signal(null);
    public Signal<Double> attackCooldown = Core.time();

    public Unit(UnitType type, int unitTeam) {
        this.type = type;
        this.unitTeam = unitTeam;
        health.set(type.maxHealth);
    }

    @Override
    protected void createInner() {
        position = Premade2D.makePosition(this);
        velocity = Premade2D.makeVelocity(this);
        rotation = Premade2D.makeRotation(this);
        Premade2D.makeSpriteGraphics(this, type.spriteName);

        add(Core.renderLayer(-1).onEvent(() -> {
            Graphics2D.fillEllipse(position.get(), size, teamColors.get(unitTeam).withA(.2), 20);
            if (selected.contains(this)) {
                Graphics2D.drawEllipse(position.get(), size, WHITE, 20);
            }
        }));
        add(Core.renderLayer(1).onEvent(() -> {
            if (health.get() < type.maxHealth) {
                Graphics2D.fillRect(position.get().subtract(size), new Vec2(size.x * 2, size.x / 5), BLACK);
                Graphics2D.fillRect(position.get().subtract(size), new Vec2(size.x * 2 * health.get() / type.maxHealth, size.x / 5), GREEN);
            }
        }));

        onUpdate(dt -> {
            Order o = order.get();
            if (o.isFinished()) {
                order.set(new IdleOrder(this));
                o.onFinish();
            } else {
                o.execute(dt);
            }
        });
    }
}
