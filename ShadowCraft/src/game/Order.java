package game;

import static game.Unit.myTeam;
import graphics.Graphics2D;
import static java.util.Comparator.comparingDouble;
import java.util.Optional;
import networking.Client;
import static networking.MessageType.UPDATE_UNIT_HEALTH;
import static util.Color4.WHITE;
import util.RegisteredEntity;
import util.Vec2;

public abstract class Order {

    protected Unit u;

    public Order(Unit u) {
        this.u = u;
    }

    public void aggressive() {
        if (Unit.aggressive) {
            if (u.unitTeam == myTeam) {
                Optional<Unit> nearest = RegisteredEntity.getAll(Unit.class).stream()
                        .filter(o -> o.unitTeam != myTeam)
                        .min(comparingDouble(o -> o.position.get().subtract(u.position.get()).lengthSquared()));
                nearest.ifPresent(o -> {
                    if (o.position.get().subtract(u.position.get()).lengthSquared() < u.type.range * u.type.range) {
                        u.order.set(new AttackOrder(u, o));
                    }
                });
            }
        }
    }

    public void draw() {
    }

    public abstract void execute(double dt);

    public abstract boolean isFinished();

    public void onFinish() {
    }

    public static class IdleOrder extends Order {

        public IdleOrder(Unit u) {
            super(u);
        }

        @Override
        public void execute(double dt) {
            aggressive();
        }

        @Override
        public boolean isFinished() {
            return false;
        }
    }

    public static class MoveOrder extends Order {

        public Vec2 goal;

        public MoveOrder(Unit u, Vec2 goal) {
            super(u);
            this.goal = goal;
        }

        @Override
        public void execute(double dt) {
            u.velocity.set(goal.subtract(u.position.get()).withLength(u.type.moveSpeed));
            aggressive();
        }

        @Override
        public boolean isFinished() {
            return goal.subtract(u.position.get()).lengthSquared() < 15;
        }

        @Override
        public void onFinish() {
            u.velocity.set(new Vec2(0));
        }
    }

    public static class AttackOrder extends Order {

        public Unit target;
        public Vec2 shotAt;
        public double shotDuration;

        public AttackOrder(Unit u, Unit target) {
            super(u);
            this.target = target;
        }

        @Override
        public void draw() {
            if (shotDuration > 0) {
                Graphics2D.drawLine(u.position.get(), shotAt, WHITE, 2);
            }
        }

        @Override
        public void execute(double dt) {
            if (u.position.get().subtract(target.position.get()).lengthSquared() > u.type.range * u.type.range) {
                u.velocity.set(target.position.get().subtract(u.position.get()).withLength(u.type.moveSpeed));
            } else {
                u.velocity.set(new Vec2(0));
                if (u.attackCooldown.get() > 0) {
                    u.attackCooldown.set(-1 / u.type.attackSpeed);
                    //attack
                    shotAt = target.position.get();
                    shotDuration = .1;
                    int damage = Math.max(u.type.damage - target.type.armor, 1);
                    target.health.edit(h -> h - damage);
                    if (u.unitTeam == Unit.myTeam) {
                        Client.sendMessage(UPDATE_UNIT_HEALTH, target.id, target.health.get());
                    }
                }
            }
            shotDuration -= dt;
        }

        @Override
        public boolean isFinished() {
            return target.isDestroyed();
        }
    }
}
