module com.carracinggame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Nếu bạn có/ sẽ dùng FXML (không hại nếu chưa dùng)
    opens com.carracinggame to javafx.fxml;
    opens com.carracinggame.scene to javafx.fxml;

    // Export các package bạn đang dùng
    exports com.carracinggame;
    exports com.carracinggame.core;
    exports com.carracinggame.scene;
}
