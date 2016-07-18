package game;

import networking.Client;
import static networking.MessageType.UPDATE_UNIT_HEALTH;
import util.Vec2;

public abstract class Order {

    protected Unit u;

    public Order(Unit u) {
        this.u = u;
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

        public AttackOrder(Unit u, Unit target) {
            super(u);
            this.target = target;
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
                    int damage = Math.max(u.type.damage - target.type.armor, 1);
                    target.health.edit(h -> h - damage);
                    if (u.unitTeam == Unit.myTeam) {
                        Client.sendMessage(UPDATE_UNIT_HEALTH, target.id, target.health.get());
                    }
                }
            }
        }

        @Override
        public boolean isFinished() {
            return target.isDestroyed();
        }
    }
}
