package game;

public enum UnitType {

    BASIC_RANGED(20, 200, 2, 3, 0,
            150, false, 15,
            "ball", 1, 1, 1, 1,
            1, 15, 0, 5),
    HELICOPTER(100, 300, 5, 2, 2,
            250, true, 30,
            "helicopter", 3, 2, 3, 4,
            3, 75, 20, 20);

    public final int maxHealth;
    public final double range;
    public final double attackSpeed;
    public final int damage;
    public final int armor;

    public final double moveSpeed;
    public final boolean flying;
    public final double size;

    public final String spriteName;
    public final int frontFrames;
    public final int sideFrames;
    public final int backFrames;
    public final double scale;

    public final int costPop;
    public final int costCredits;
    public final int costEnergy;
    public final double trainTime;

    private UnitType(int maxHealth, double range, double attackSpeed, int damage, int armor, double moveSpeed, boolean flying, double size, String spriteName, int frontFrames, int sideFrames, int backFrames, double scale, int costPop, int costCredits, int costEnergy, double trainTime) {
        this.maxHealth = maxHealth;
        this.range = range;
        this.attackSpeed = attackSpeed;
        this.damage = damage;
        this.armor = armor;
        this.moveSpeed = moveSpeed;
        this.flying = flying;
        this.size = size;
        this.spriteName = spriteName;
        this.frontFrames = frontFrames;
        this.sideFrames = sideFrames;
        this.backFrames = backFrames;
        this.scale = scale;
        this.costPop = costPop;
        this.costCredits = costCredits;
        this.costEnergy = costEnergy;
        this.trainTime = trainTime;
    }

}
