package com.carracinggame.core;

public enum GarageCar {
    BLAZE_X1(
            "Blaze X1",
            "Balanced starter build",
            "/images/cars/blaze_x1.png",
            "/images/cars/blaze_x1_left.png",
            "/images/cars/blaze_x1_right.png",
            "/images/cars/blaze_x1_crash.png",
            87,
            91,
            74,
            260,
            0,
            true
    ),
    THUNDER_R(
            "Thunder R",
            "Power-focused sprint machine",
            "/images/cars/thunder_r.png",
            "/images/cars/thunder_r_left.png",
            "/images/cars/thunder_r_right.png",
            "/images/cars/thunder_r_crash.png",
            95,
            78,
            88,
            340,
            12000,
            false
    ),
    NEON_GT(
            "Neon GT",
            "Grip-heavy city racer",
            "/images/cars/neon_gt.png",
            "/images/cars/neon_gt_left.png",
            "/images/cars/neon_gt_right.png",
            "/images/cars/neon_gt_crash.png",
            84,
            97,
            82,
            315,
            17500,
            false
    );

    private final String displayName;
    private final String subtitle;
    private final String imagePath;
    private final String leftImagePath;
    private final String rightImagePath;
    private final String crashImagePath;
    private final int power;
    private final int handling;
    private final int boost;
    private final int baseRewardBonus;
    private final int price;
    private final boolean starterOwned;

    GarageCar(
            String displayName,
            String subtitle,
            String imagePath,
            String leftImagePath,
            String rightImagePath,
            String crashImagePath,
            int power,
            int handling,
            int boost,
            int baseRewardBonus,
            int price,
            boolean starterOwned
    ) {
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.imagePath = imagePath;
        this.leftImagePath = leftImagePath;
        this.rightImagePath = rightImagePath;
        this.crashImagePath = crashImagePath;
        this.power = power;
        this.handling = handling;
        this.boost = boost;
        this.baseRewardBonus = baseRewardBonus;
        this.price = price;
        this.starterOwned = starterOwned;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getLeftImagePath() {
        return leftImagePath;
    }

    public String getRightImagePath() {
        return rightImagePath;
    }

    public String getCrashImagePath() {
        return crashImagePath;
    }

    public int getPower() {
        return power;
    }

    public int getHandling() {
        return handling;
    }

    public int getBoost() {
        return boost;
    }

    public int getBaseRewardBonus() {
        return baseRewardBonus;
    }

    public int getPrice() {
        return price;
    }

    public boolean isStarterOwned() {
        return starterOwned;
    }

    public String getPriceText() {
        return price <= 0 ? "Miễn phí" : String.format("%,d coin", price);
    }

    public String getStatLine() {
        return "POW " + power + "  •  HDL " + handling + "  •  BST " + boost;
    }
}
