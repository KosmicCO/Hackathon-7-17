package game;

public enum UnitType {

    BASIC_RANGED(20, 200, 2, 3, 0, 150, "ball");

    public final int maxHealth;
    public final double range;
    public final double attackSpeed;
    public final int damage;
    public final int armor;
    public final double moveSpeed;
    public final String spriteName;
    //cost

    private UnitType(int maxHealth, double range, double attackSpeed, int damage, int armor, double moveSpeed, String spriteName) {
        this.maxHealth = maxHealth;
        this.range = range;
        this.attackSpeed = attackSpeed;
        this.damage = damage;
        this.armor = armor;
        this.moveSpeed = moveSpeed;
        this.spriteName = spriteName;
    }
}
