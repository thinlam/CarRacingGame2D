package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

public final class CarViewFactory {

    private static final Random RANDOM = new Random();

    // Bộ ảnh riêng cho xe AI
    private static final List<String> AI_CAR_PATHS = List.of(
            "/images/cars/ai/truck.png",
            "/images/cars/ai/taxi.png",
            "/images/cars/ai/Ambulance.png",
            "/images/cars/ai/Mini_van.png",
            "/images/cars/ai/Mini_truck.png"
    );

    private CarViewFactory() {}

    // ================= SHOP =================
    public static Node createShopPreview(CarId carId) {
        return createCar(carId, 95, 140);
    }

    // ================= LOBBY =================
    public static Node createLobbyPreview(CarId carId) {
        return createCar(carId, 120, 180);
    }

    // ================= RACE =================
    public static Node createRaceCar(CarId carId) {
        return createCar(carId, 50, 80);
    }

    public static Node createPlayerRaceCar(CarId carId) {
        return createCar(carId, 50, 80);
    }

    // Xe AI dùng ảnh PNG riêng
    public static Node createObstacleRaceCar(CarId carId) {
        String aiPath = AI_CAR_PATHS.get(RANDOM.nextInt(AI_CAR_PATHS.size()));
        Node node = createCarFromPath(aiPath, 50, 80);
        node.setRotate(180); // xe AI quay ngược
        return node;
    }

    // ================= CORE =================
    private static Node createCar(CarId carId, double w, double h) {
        String path = getImagePath(carId);
        return createCarFromPath(path, w, h);
    }

    private static Node createCarFromPath(String path, double w, double h) {
        try (InputStream is = CarViewFactory.class.getResourceAsStream(path)) {

            if (is == null) {
                System.out.println("Không tìm thấy ảnh: " + path);
                return createFallback(w, h);
            }

            Image rawImage = new Image(is);
            Image trimmedImage = trimTransparentEdges(rawImage);

            ImageView iv = new ImageView(trimmedImage);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            StackPane box = new StackPane(iv);
            box.setAlignment(Pos.CENTER);
            box.setPrefSize(w, h);
            box.setMinSize(w, h);
            box.setMaxSize(w, h);

            return box;

        } catch (Exception e) {
            e.printStackTrace();
            return createFallback(w, h);
        }
    }

    private static StackPane createFallback(double w, double h) {
        StackPane fallback = new StackPane();
        fallback.setAlignment(Pos.CENTER);
        fallback.setPrefSize(w, h);
        fallback.setMinSize(w, h);
        fallback.setMaxSize(w, h);
        return fallback;
    }

    /**
     * Cắt phần viền trong suốt xung quanh ảnh
     * để các xe nhìn đồng đều kích thước hơn.
     */
    private static Image trimTransparentEdges(Image image) {
        PixelReader reader = image.getPixelReader();
        if (reader == null) {
            return image;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        int alphaThreshold = 10;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int alpha = (argb >> 24) & 0xff;

                if (alpha > alphaThreshold) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return image;
        }

        int croppedWidth = maxX - minX + 1;
        int croppedHeight = maxY - minY + 1;

        return new WritableImage(reader, minX, minY, croppedWidth, croppedHeight);
    }

    // ================= PLAYER CAR PNG =================
    private static String getImagePath(CarId carId) {
        return switch (carId) {
            case RED_RACER -> "/images/cars/red_racer.png";
            case BLUE_STORM -> "/images/cars/blue_storm.png";
            case GREEN_SHADOW -> "/images/cars/green_shadow.png";
            case YELLOW_FLASH -> "/images/cars/yellow_flash.png";
            default -> "/images/cars/red_racer.png";
        };
    }
}