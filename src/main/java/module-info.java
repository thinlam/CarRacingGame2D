module com.carracinggame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.carracinggame to javafx.fxml;
    opens com.carracinggame.scene to javafx.fxml;

    exports com.carracinggame;
    exports com.carracinggame.core;
    exports com.carracinggame.scene;
    exports com.carracinggame.shop;
    opens com.carracinggame.shop to javafx.fxml;
}