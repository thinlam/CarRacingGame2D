package com.carracinggame.car;

public record CarSkill(
        String name,
        String shortLabel,
        String description,
        double cooldownSeconds,
        double activeSeconds,
        String accentColor
) {
    public static CarSkill forCar(CarId carId) {
        return switch (carId) {
            case RED_RACER -> new CarSkill(
                    "Phantom Dash",
                    "Xuyên pha",
                    "Xe lướt xuyên qua vật cản trong 2.0 giây.",
                    8.0,
                    2.0,
                    "#fb7185"
            );
            case BLUE_STORM -> new CarSkill(
                    "EMP Pulse",
                    "Xung điện từ",
                    "Phá hủy chướng ngại gần nhất phía trước.",
                    7.0,
                    0.15,
                    "#60a5fa"
            );
            case GREEN_SHADOW -> new CarSkill(
                    "Time Warp",
                    "Bẻ cong thời gian",
                    "Làm chậm toàn bộ chướng ngại trong 3.5 giây.",
                    10.0,
                    3.5,
                    "#34d399"
            );
            default -> new CarSkill(
                    "Shield Burst",
                    "Lá chắn",
                    "Tạo lá chắn ngắn để chống va chạm.",
                    9.0,
                    1.8,
                    "#fbbf24"
            );
        };
    }

    public String durationText() {
        return String.format("Hiệu lực: %.1fs", activeSeconds);
    }

    public String cooldownText() {
        return String.format("Hồi chiêu: %.1fs", cooldownSeconds);
    }
}