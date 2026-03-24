# CarRacingGame2D

Project JavaFX game 2D gồm các màn hình:
- Menu
- Map Select
- Shop
- Garage
- Race

## Cách chạy

### IntelliJ IDEA
1. Mở project bằng IntelliJ.
2. Bảo đảm JDK đang là **17** hoặc cao hơn.
3. Maven reload project.
4. Chạy class: `com.carracinggame.MainApp`

### Terminal
```bash
mvn clean javafx:run
```

## Ghi chú
- Shop và Garage đã hoạt động để mua xe và chọn xe.
- Race có giao diện đua cơ bản và dùng xe đang được chọn trong Garage.
- Nhấn `ESC` để quay lại menu, `R` để reset vị trí xe.
