package game;

import engine.Core;
import engine.Input;
import engine.Signal;
import examples.Premade2D;
import game.Order.IdleOrder;
import graphics.Graphics2D;
import graphics.data.Sprite;
import static java.lang.Math.PI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import map.Terrain;
import networking.Client;
import static networking.MessageType.*;
import org.lwjgl.input.Keyboard;
import util.Color4;
import static util.Color4.*;
import util.Mutable;
import util.RegisteredEntity;
import util.Vec2;

public class Unit extends RegisteredEntity {

    public static int myTeam = 0;
    public static final Set<Unit> selected = new HashSet();
    public static boolean aggressive = true;

    public static List<Color4> teamColors = Arrays.asList(
            new Color4(1, 1, 0),
            new Color4(.75, 0, 1)
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
        Input.whenMouse(0, false).filter(drawingBox.map(b -> !b)).onEvent(() -> {
            selected.clear();
            for (Unit u : RegisteredEntity.getAll(Unit.class)) {
                if (u.unitTeam == myTeam) {
                    if (Input.getMouse().containedBy(u.position.get().add(new Vec2(u.type.size)), u.position.get().subtract(new Vec2(u.type.size)))) {
                        selected.add(u);
                        break;
                    }
                }
            }
        });

        Input.whenMouse(1, true).onEvent(() -> {
            Mutable<Unit> target = new Mutable(null);
            for (Unit u : RegisteredEntity.getAll(Unit.class)) {
                if (u.unitTeam != myTeam) {
                    if (Input.getMouse().containedBy(u.position.get().add(new Vec2(u.type.size)), u.position.get().subtract(new Vec2(u.type.size)))) {
                        target.o = u;
                        break;
                    }
                }
            }
            if (target.o == null) {
                selected.forEach(u -> Client.sendMessage(ORDER_MOVE, u.id, Input.getMouse()));
            } else {
                selected.forEach(u -> Client.sendMessage(ORDER_ATTACK, u.id, target.o.id));
            }
        });

        Input.whenKey(Keyboard.KEY_F2, true).onEvent(() -> {
            selected.clear();
            RegisteredEntity.getAll(Unit.class).forEach(u -> {
                if (u.unitTeam == myTeam) {
                    selected.add(u);
                }
            });
        });

        Input.whenKey(Keyboard.KEY_TAB, true).onEvent(() -> aggressive = !aggressive);
    }

    public static Unit findById(int id) {
        return RegisteredEntity.getAll(Unit.class).stream().filter(u -> u.id == id).findAny().orElse(null);
    }

    public int id = -1;
    public Signal<Vec2> position, velocity;
    public Signal<Double> rotation;
    public Signal<Order> order = new Signal(new IdleOrder(this));
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
        Signal<Vec2> prevPos = new Signal(null);
        velocity = Premade2D.makeVelocity(this);
        rotation = Premade2D.makeRotation(this);

        Sprite spriteFront = new Sprite(type.spriteName + "_front", type.frontFrames, 1);
        Sprite spriteSide = new Sprite(type.spriteName + "_side", type.sideFrames, 1);
        Sprite spriteBack = new Sprite(type.spriteName + "_back", type.backFrames, 1);
        spriteFront.scale = new Vec2(type.scale);
        spriteSide.scale = new Vec2(type.scale);
        spriteBack.scale = new Vec2(type.scale);
        onUpdate(dt -> {
            spriteFront.imageIndex += dt * 6;
            spriteSide.imageIndex += dt * 6;
            spriteBack.imageIndex += dt * 6;
        });

        onUpdate(dt -> {
            if (!type.flying && Terrain.isSolid(position.get(), new Vec2(type.size))) {
                int detail = 20;
                Vec2 delta = position.get().subtract(prevPos.get()).divide(detail);
                position.set(prevPos.get());
                for (int i = 0; i < detail; i++) {
                    position.edit(delta.withY(0)::add);
                    if (Terrain.isSolid(position.get(), new Vec2(type.size))) {
                        position.edit(delta.withY(0).reverse()::add);
                        velocity.edit(v -> v.withX(0));
                        break;
                    }
                }
                for (int i = 0; i < detail; i++) {
                    position.edit(delta.withX(0)::add);
                    if (Terrain.isSolid(position.get(), new Vec2(type.size))) {
                        position.edit(delta.withX(0).reverse()::add);
                        velocity.edit(v -> v.withY(0));
                        break;
                    }
                }
            }
        });
        onUpdate(dt -> prevPos.set(position.get()));

//        Premade2D.makeSpriteGraphics(this, type.spriteName);
        add(Core.renderLayer(-.5).onEvent(() -> {
            Graphics2D.fillEllipse(position.get(), new Vec2(type.size), teamColors.get(unitTeam).withA(.2), 20);
            if (selected.contains(this)) {
                Graphics2D.drawEllipse(position.get(), new Vec2(type.size), WHITE, 20);
            }
        }));
        add(Core.renderLayer(0).onEvent(() -> {
            if (velocity.get().lengthSquared() > 0) {
                rotation.set(velocity.get().direction());
            }
            double rot = rotation.get();
            Sprite spr;
            if (rot >= -PI / 4 && rot < PI / 4) {
                spr = spriteSide;
                spr.scale = new Vec2(type.scale);
            } else if (rot >= PI / 4 && rot < PI * 3 / 4) {
                spr = spriteBack;
            } else if (rot >= PI * 3 / 4 || rot < -PI * 3 / 4) {
                spr = spriteSide;
                spr.scale = new Vec2(-type.scale, type.scale);
            } else { //else if (rot >= PI * 5 / 4 && rot < PI * 7 / 4) {
                spr = spriteFront;
            }
            spr.draw(position.get(), 0);

        }));
        add(Core.renderLayer(1).onEvent(() -> {
            if (health.get() < type.maxHealth) {
                Graphics2D.fillRect(position.get().subtract(new Vec2(type.size)), new Vec2(type.size * 2, type.size / 5), BLACK);
                Graphics2D.fillRect(position.get().subtract(new Vec2(type.size)), new Vec2(type.size * 2 * Math.max(health.get(), 0) / type.maxHealth, type.size / 5), GREEN);
            }
        }));

        add(Core.interval(.5).filter(() -> unitTeam == myTeam).onEvent(()
                -> Client.sendMessage(UPDATE_UNIT_POSITION, id, position.get(), velocity.get())));

        onUpdate(dt -> velocity.edit(v -> v.divide(Math.exp(dt))));

        onUpdate(dt -> {
            Order o = order.get();
            if (o.isFinished()) {
                if (unitTeam == myTeam) {
                    Client.sendMessage(ORDER_IDLE, id);
                    order.set(new IdleOrder(this));
                }
                o.onFinish();
            } else {
                o.execute(dt);
            }
        });
        onRender(() -> {
            order.get().draw();
        });

        onUpdate(dt -> {
            RegisteredEntity.getAll(Unit.class).forEach(other -> {
                if (other != this && other.unitTeam == unitTeam) {
                    Vec2 diff = position.get().subtract(other.position.get()).reverse();
                    if (diff.lengthSquared() < (type.size + other.type.size) * (type.size + other.type.size)) {//(sc.type.size + other.type.size) * (sc.type.size + other.type.size)) {
                        Vec2 diffN = diff.normalize();
                        if (velocity.get().dot(diffN) >= 0) {
                            Vec2 change = diff.subtract(diffN.multiply(type.size + other.type.size)).multiply(.15);
                            position.edit(v -> v.add(change));
                            other.position.edit(v -> v.subtract(change));
                            other.velocity.edit(v -> v.subtract(change));
//                            velocity.edit(v -> v.subtract(diffN.multiply(v.dot(diffN))));
                        }
                    }
                }
            });
        });
    }
}
